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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.sarxos.webcam.*;
import com.github.sarxos.webcam.ds.v4l4j.V4l4jDriver;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;

import org.apache.camel.impl.UriEndpointComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the component that manages {@link WebcamEndpoint}.
 */
public class WebcamComponent extends UriEndpointComponent implements WebcamDiscoveryListener {
    
    private static final Logger LOG = LoggerFactory.getLogger(WebcamComponent.class);
    
    private Map<String, Webcam> webcams = new HashMap();
    private int timeout = 60000;
    private String driver;
    
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
        
        WebcamDriver driver = null;
        String osName = System.getProperty("os.name").toLowerCase();
        
        LOG.info("Starting webcam component for OS {}", System.getProperty("os.name"));

        // use specified driver
        if (getDriver() != null) {
            Class<WebcamDriver> clazz = (Class<WebcamDriver>) Class.forName(getDriver());
            Constructor<WebcamDriver> constructor = clazz.getConstructor();
            driver = constructor.newInstance();

        } else {
            // else try to load the v4l4 driver for Linux 
            if (osName.indexOf("nix") > -1 || osName.indexOf("nux") > -1 || osName.indexOf("aix") > -1 ) {
                try {
                    Webcam.setDriver(new V4l4jDriver());
                } catch (Exception e) {
                    LOG.warn("v4l4 driver not supported on [{}]", osName);
                }
            }
        } 
        if (driver != null) {
            Webcam.setDriver(driver);
        }

        List<Webcam> webcamList = Webcam.getWebcams(timeout);
        if (webcamList == null || webcamList.size() == 0) {
            throw new IllegalStateException("No webcams found");
        }
        webcamList.forEach(w -> webcams.put(w.getName(), w));
        
        super.doStart();
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
        LOG.info("Discovered webcam : {}", webcam.getName());

        webcams.put(webcam.getName(), webcam);
    }

    @Override
    public void webcamGone(WebcamDiscoveryEvent event) {
        LOG.info("Webcam : {} is gone", event.getWebcam().getName());
        webcams.remove(event.getWebcam().getName());
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
     * Returns the default webcam.
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
            webcam.setViewSize(dimension);
        } else if (!webcam.isOpen()) {
            webcam.open();
        }
        return webcam;
    }
    
    /**
     * Returns the list of webcams
     */
    protected List<String> getWebcamNames(){
        return new ArrayList<String>(webcams.keySet());
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }
}
