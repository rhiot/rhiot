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

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.v4l4j.V4l4jDriver;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a Webcam endpoint.
 */
@UriEndpoint(scheme = "webcam", title = "webcam", syntax="webcam:name", consumerClass = WebcamMotionConsumer.class, label = "Webcam")
public class WebcamEndpoint extends DefaultEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(WebcamEndpoint.class);
    
    @UriParam(defaultValue = "false", description = "Whether the consumer is scheduled or not")
    private boolean scheduled = false;

    private Webcam webcam;
    @UriParam(defaultValue = "true", description = "Indicates if the endpoint should try open the webcam on start.")
    private boolean openWebcam = true;
    @UriParam(defaultValue = "true", description = "Indicates if the endpoint should detect motion.")
    private boolean detectMotion = true;
    @UriParam(defaultValue = "500", description = "Interval in milliseconds to detect motion.")
    private int motionInterval = 500;
    
    public WebcamEndpoint() {
    }

    public WebcamEndpoint(String uri, WebcamComponent component) {
        super(uri, component);
    }

    public WebcamEndpoint(String endpointUri) {
        super(endpointUri);
        createEndpointConfiguration(endpointUri);
    }

    // Producer/consumer factories

    @Override
    public Producer createProducer() throws Exception {
        return new WebcamProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        Consumer consumer = isScheduled() ? new WebcamScheduledConsumer(this, processor) : new WebcamMotionConsumer(this, processor);

        if(isScheduled()) {
            if(!getConsumerProperties().containsKey("delay")) {
                ((WebcamScheduledConsumer) consumer).setDelay(5000);
            }
        }
        configureConsumer(consumer);
        return consumer;
    }

    // Life cycle

    @Override
    protected void doStart() throws Exception {
        
        if(openWebcam && webcam == null) {
            try {
                Webcam.setDriver(new V4l4jDriver()); // this is important for Raspberry Pi
            } catch (UnsatisfiedLinkError e) {
                //default is Pi but allow the webcam to fallback, aim to support specifying driver
                try {
                    webcam = Webcam.getDefault();
                } catch (Exception ex) {
                    LOG.error("Failed to load the webcam driver", ex);
                }
            }
        }
        if (webcam != null && webcam.open()) {
            LOG.info("Opened webcam device : {}", webcam.getDevice().getName());
        } else {
            throw new IllegalStateException("Failed to open webcam");
        }
        
        super.doStart();
    }

    @Override
    protected void doStop() throws Exception {
        if (webcam != null) {
            webcam.close();
            webcam = null;
        }
        super.doStop();
    }

    // Configuration

    @Override
    public boolean isSingleton() {
        return true;
    }

    // Configuration getters and setters


    public boolean isScheduled() {
        return scheduled;
    }

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }

    public Webcam getWebcam() {
        return webcam;
    }

    public void setWebcam(Webcam webcam) {
        this.webcam = webcam;
    }

    public boolean isOpenWebcam() {
        return openWebcam;
    }

    public void setOpenWebcam(boolean openWebcam) {
        this.openWebcam = openWebcam;
    }

    public boolean isDetectMotion() {
        return detectMotion;
    }

    public void setDetectMotion(boolean detectMotion) {
        this.detectMotion = detectMotion;
    }

    public int getMotionInterval() {
        return motionInterval;
    }

    public void setMotionInterval(int motionInterval) {
        this.motionInterval = motionInterval;
    }
}