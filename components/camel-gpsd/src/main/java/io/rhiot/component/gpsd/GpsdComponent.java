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
package io.rhiot.component.gpsd;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import io.rhiot.utils.install.DefaultInstaller;
import io.rhiot.utils.install.Installer;
import io.rhiot.utils.process.DefaultProcessManager;
import io.rhiot.utils.process.ProcessManager;
import org.apache.camel.Endpoint;

import org.apache.camel.impl.UriEndpointComponent;
import org.slf4j.Logger;

import static java.lang.Thread.sleep;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Represents the component that manages {@link GpsdEndpoint}.
 */
public class GpsdComponent extends UriEndpointComponent {
    
    private static final Logger LOG = getLogger(GpsdComponent.class);

    private int gpsdRestartInterval = 5000;

    private ProcessManager processManager;
    
    private CountDownLatch isLocalGpsdStarting = new CountDownLatch(1);
    private CountDownLatch isLocalGpsdStarted = new CountDownLatch(1);
    private boolean gpsdStarted;
    
    private Installer installer;
    private String requiredPackages = GpsdConstants.GPSD_DEPENDENCIES_LINUX;
    
    private boolean ignoreInstallerProblems = true;

    public GpsdComponent() {
        super(GpsdEndpoint.class);
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        LOG.debug("Creating GPSD endpoint for URI {} .", uri);
        Endpoint endpoint = new GpsdEndpoint(uri, this);
        setProperties(endpoint, parameters);
        return endpoint;
    }
    // Life-cycle

    @Override
    protected void doStart() throws Exception {
        super.doStart();
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

    protected void restartGpsDaemon() {

        installer = resolveInstaller();

        try {
            if (!installer.install(getRequiredPackages()) && !ignoreInstallerProblems) {
                throw new IllegalStateException("Unable to start gpsd, failed to install dependencies");
            }
        } catch (Exception ex) {
            if(ignoreInstallerProblems) {
                LOG.warn(ex.getMessage());
            } else {
                throw ex;
            }
        }

        try {
            if (gpsdStarted){
                return;
            }
            
            if (isLocalGpsdStarting.getCount() == 0) {
                isLocalGpsdStarted.await();
            } else {
                isLocalGpsdStarting.countDown();
            }
            
            //Recheck if GPSD is started
            if (gpsdStarted) {
                return;
            }
            
            processManager = resolveProcessManager();
            List<String> gpsctlResult;
            do {
                LOG.info("(Re)starting GPS daemon.");
                processManager.executeAndJoinOutput("killall", "gpsd");
                processManager.executeAndJoinOutput("gpsd", "/dev/ttyUSB0");
                sleep(gpsdRestartInterval);
                gpsctlResult = processManager.executeAndJoinOutput("gpsctl", "-n", "/dev/ttyUSB0");
                LOG.info("gpsctl result: {}", gpsctlResult);
                if (gpsctlResult.contains("gpsctl:ERROR: /dev/ttyUSB0 mode change to NMEA failed")) {
                    gpsdStarted = true;
                    isLocalGpsdStarted.countDown();
                }
            } while (!gpsdStarted);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    protected Installer resolveInstaller() {
        LOG.debug("Started resolving Installer...");
        if(installer != null) {
            LOG.debug("Installer has been set on the component level. Camel will use it: {}", installer);
            return installer;
        }
        Set<DefaultInstaller> installers = getCamelContext().getRegistry().findByType(DefaultInstaller.class);
        if(installers.isEmpty()) {
            LOG.debug("No Installer found in the registry - creating new DefaultInstaller.");
            return new DefaultInstaller();
        } else if(installers.size() == 1) {
            return installers.iterator().next();
        } else {
            return new DefaultInstaller();
        }
    }


    public ProcessManager getProcessManager() {
        return processManager;
    }

    public void setProcessManager(ProcessManager processManager) {
        this.processManager = processManager;
    }

    public Installer getInstaller() {
        return installer;
    }

    public void setInstaller(DefaultInstaller installer) {
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
