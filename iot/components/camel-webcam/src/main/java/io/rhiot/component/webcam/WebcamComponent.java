/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rhiot.component.webcam;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import com.github.sarxos.webcam.*;
import com.github.sarxos.webcam.ds.v4l4j.V4l4jDriver;
import io.rhiot.utils.OsUtils;
import io.rhiot.utils.install.DefaultInstaller;
import io.rhiot.utils.install.Installer;
import io.rhiot.utils.install.exception.PermissionDeniedException;
import io.rhiot.utils.process.DefaultProcessManager;
import io.rhiot.utils.process.ProcessManager;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;

import org.apache.camel.impl.UriEndpointComponent;
import org.slf4j.Logger;

import static java.lang.Thread.sleep;
import static io.rhiot.component.webcam.WebcamConstants.*;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Represents the component that manages {@link WebcamEndpoint}.
 */
public class WebcamComponent extends UriEndpointComponent implements WebcamDiscoveryListener {
    
    private static final Logger LOG = getLogger(WebcamComponent.class);
    
    private Map<String, Webcam> webcams = new HashMap<>();
    private int timeout = 30000;
    private String driver;

    private int webcamRestartInterval = 5000;
    private boolean webcamStarted;
    private String v4l2WebcamLoadingCommand = V4L2_WEBCAM_LOADING_COMMAND;
    
    private Installer installer;
    private String requiredPackages = WebcamConstants.WEBCAM_DEPENDENCIES_LINUX;

    private ProcessManager processManager;

    private boolean ignoreInstallerProblems = true;
    
    public WebcamComponent() {
        super(WebcamEndpoint.class);
    }

    public WebcamComponent(CamelContext context) {
        super(context, WebcamEndpoint.class);
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Endpoint endpoint = new WebcamEndpoint(uri, this);
        setProperties(endpoint, parameters);

        return endpoint;
    }
    
    // Life-cycle

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        installer = resolveInstaller();

        String requiredPackages = getRequiredPackages();

        try {
            if (!installer.install(requiredPackages) && !ignoreInstallerProblems) {
                throw new IllegalStateException("Unable to start webcam, failed to install dependencies");
            }
        } catch (Exception ex) {
            if(ignoreInstallerProblems) {
                LOG.warn(ex.getMessage());
            } else {
                throw ex;
            }
        }
        
        //Use the provided webcam/s
        if (getWebcams().size() == 0) {
            loadWebcamDriver();

            List<Webcam> webcamList = Webcam.getWebcams(timeout);
            if (webcamList == null || webcamList.size() == 0) {
                throw new IllegalStateException("No webcams found");
            }
            webcamList.forEach(w -> webcams.put(w.getDevice().getName(), w));

            LOG.info("Detected webcams : {}", webcams.keySet());
        }
        
    }

    protected void loadWebcamDriver() {
        
        if (webcamStarted) {
            return;
        }

        try {
            
            // use specified driver
            if (getDriver() != null) {
                Class<WebcamDriver> clazz = (Class<WebcamDriver>) Class.forName(getDriver());
                Constructor<WebcamDriver> constructor = clazz.getConstructor();
                WebcamDriver driver = constructor.newInstance();
                LOG.debug("Using specified driver [{}]", driver);
                Webcam.setDriver(driver);
                webcamStarted = true;

            } else if (OsUtils.isPlatform("linux")) {
                try {
                    processManager = resolveProcessManager();
                    do {

                        LOG.debug("Loading v4l2 module");
                        List<String> v4l2Result = processManager.executeAndJoinOutput("/bin/sh", "-c", getV4l2WebcamLoadingCommand());
                        
                        if (v4l2Result.contains("FATAL: Module bcm2835-v4l2 not found.")) {
                            throw new RuntimeException("Video for Linux module is not installed");
                            
                        } else if (v4l2Result.contains("ERROR: could not insert 'bcm2835_v4l2': Operation not permitted")){
                            throw new RuntimeException("User has insufficient privileges to load Video for Linux module");
                        }
                        
                        processManager.executeAndJoinOutput("/bin/sh", "-c", WebcamConstants.V4L2_SET_FORMAT_JPEG_COMMAND);
                        
                        v4l2Result = processManager.executeAndJoinOutput("/bin/sh", "-c", V4L2_LIST_DEVICES_COMMAND);
                        LOG.info("Result of V4L2 listing devices {}", v4l2Result);

                        List<String> failureToOpen = v4l2Result.stream().filter(e -> e.contains("Failed to open")).collect(Collectors.toList());

                        if (failureToOpen.size() == 0) {
                            webcamStarted = true;
                            Webcam.setDriver(new V4l4jDriver());
                        } else {
                            throw new RuntimeException(failureToOpen.get(0));  
                        }
                        sleep(webcamRestartInterval);
                    } while (!webcamStarted);
                } catch (Error e) {
                    LOG.error("Failed to install and configure the v4l driver", e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected ProcessManager resolveProcessManager() {
        LOG.debug("Started resolving ProcessManager...");
        if(processManager != null) {
            LOG.debug("ProcessManager has been set on the component level. Camel will use it: {}", processManager);
            return processManager;
        }
        Set<ProcessManager> processManagers = getCamelContext().getRegistry().findByType(ProcessManager.class);
        if(processManagers.isEmpty()) {
            LOG.debug("No ProcessManager found in the registry - creating new DefaultProcessManager.");
            return new DefaultProcessManager();
        } else if(processManagers.size() == 1) {
            return processManagers.iterator().next();
        } else {
            return new DefaultProcessManager();
        }
    }

    protected Installer resolveInstaller() {
        LOG.debug("Started resolving Installer...");
        if(installer != null) {
            LOG.debug("Installer has been set on the component level. Camel will use it: {}", installer);
            return installer;
        }
        Set<Installer> installers = getCamelContext().getRegistry().findByType(Installer.class);
        if(installers.isEmpty()) {
            LOG.debug("No Installer found in the registry - creating new DefaultInstaller.");
            return new DefaultInstaller();
        } else if(installers.size() == 1) {
            return installers.iterator().next();
        } else {
            return new DefaultInstaller();
        }
    }
    
    @Override
    protected void doStop() throws Exception {
        webcams.values().forEach(w -> w.close());
        
        super.doStop();
    }
    
    // Webcam listeners


    @Override
    public void webcamFound(WebcamDiscoveryEvent event) {
        Webcam webcam = event.getWebcam();
        LOG.debug("Discovered webcam : {}", webcam.getDevice().getName());

        webcams.put(webcam.getDevice().getName(), webcam);
    }

    @Override
    public void webcamGone(WebcamDiscoveryEvent event) {
        LOG.info("Webcam : {} is gone", event.getWebcam().getDevice().getName());
        webcams.remove(event.getWebcam().getDevice().getName());
    }

    /**
     * Returns the webcam by name, null if not found.
     * @param name webcam name
     * @return webcam instance
     */
    public Webcam getWebcam(String name, Dimension dimension) {
        if (name == null) {
            return getWebcam(dimension);
        }

        Webcam webcam = webcams.get(name);
        openWebcam(webcam, dimension);
        return webcam;
    }

    /**
     * Returns the first webcam.
     */
    public Webcam getWebcam(Dimension dimension) {
        if (webcams.size() == 0) {
            throw new IllegalStateException("No webcams found");
        }
        
        Webcam webcam = webcams.values().iterator().next();
        openWebcam(webcam, dimension);

        return webcam;
    }

    private Webcam openWebcam(Webcam webcam, Dimension dimension) {
        
        //Once started another endpoint may want to change from low res to high res, for example
        if (webcam.isOpen() && isStarted() && !dimension.equals(webcam.getViewSize())) {
            webcam.close();
        } 
        
        if (!webcam.isOpen() && dimension != null) {
            webcam.setCustomViewSizes(new Dimension[]{dimension});
            webcam.setViewSize(dimension);
            webcam.open(true);
        } else if (!webcam.isOpen()) {
            webcam.open(true);
        }
        
        return webcam;
    }
    
    /**
     * Returns the list of webcams
     */
    protected List<String> getWebcamNames(){
        return new ArrayList<String>(webcams.keySet());
    }

    public Map<String, Webcam> getWebcams() {
        return webcams;
    }

    public void setWebcams(Map<String, Webcam> webcams) {
        this.webcams = webcams;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }
    
    public ProcessManager getProcessManager() {
        return processManager;
    }

    public void setProcessManager(ProcessManager processManager) {
        this.processManager = processManager;
    }

    public String getV4l2WebcamLoadingCommand() {
        return v4l2WebcamLoadingCommand;
    }

    public void setV4l2WebcamLoadingCommand(String v4l2WebcamLoadingCommand) {
        this.v4l2WebcamLoadingCommand = v4l2WebcamLoadingCommand;
    }

    public Installer getInstaller() {
        return installer;
    }

    public void setInstaller(Installer installer) {
        this.installer = installer;
    }

    public String getRequiredPackages() {
        return requiredPackages;
    }

    public void setRequiredPackages(String requiredPackages) {
        this.requiredPackages = requiredPackages;
    }

    public boolean isIgnoreInstallerProblems() {
        return ignoreInstallerProblems;
    }

    public void setIgnoreInstallerProblems(boolean ignoreInstallerProblems) {
        this.ignoreInstallerProblems = ignoreInstallerProblems;
    }

}
