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
import com.github.sarxos.webcam.WebcamResolution;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

/**
 * Represents a Webcam endpoint.
 */
@UriEndpoint(scheme = "webcam", title = "webcam", syntax="webcam:name", consumerClass = WebcamMotionConsumer.class, label = "Webcam")
public class WebcamEndpoint extends DefaultEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(WebcamEndpoint.class);
    
    @UriParam(defaultValue = "true", description = "Whether the consumer is scheduled or not")
    private boolean scheduled = true;

    private Webcam webcam;
    @UriParam(defaultValue = "false", description = "Indicates if the endpoint should detect motion.")
    private boolean motion = false;
    @UriParam(defaultValue = "500", description = "Interval in milliseconds to detect motion.")
    private int motionInterval = 500;
    @UriParam(defaultValue = "PNG", description = "Capture format, one of [PNG,GIF,JPG]")
    private String format = "PNG";    
    @UriParam(defaultValue = "25", description = "Intensity threshold (0 - 255)")
    private int pixelThreshold = 25;
    @UriParam(defaultValue = "0.2", description = "Percentage threshold of image covered by motion (0 - 100)")
    private double areaThreshold = 0.2;
    @UriParam(defaultValue = "-1 (2 * motionInterval)", description = "Motion inertia (time when motion is valid)")
    private int motionInertia = -1;
    @UriParam(description = "Default resolution to use, overriding width and height")
    private String resolution;
    @UriParam(defaultValue = "320", description = "Width in pixels, must be supported by the webcam")
    private int width = 320;
    @UriParam(defaultValue = "240", description = "Height in pixels, must be supported by the webcam")
    private int height = 240;
    @UriParam(description = "Webcam device name, on Linux this may default to /dev/video0")
    private String deviceName;
    
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
        Consumer consumer = isMotion() ? new WebcamMotionConsumer(this, processor) : new WebcamScheduledConsumer(this, processor);

        if(isScheduled()) {
            if(!getConsumerProperties().containsKey("delay")) {
                ((WebcamScheduledConsumer) consumer).setDelay(1000);
            }
        }
        configureConsumer(consumer);
        return consumer;
    }

    /**
     * Returns the default resolution by name if provided, eg HD720, otherwise the width and height.
     */
    private Dimension getDefaultResolution() {
        if (getResolution() != null) {
            WebcamResolution res = WebcamResolution.valueOf(getResolution());
            return res.getSize();
        } else {
          return new Dimension(getWidth(), getHeight());   
        }
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
        //Allow tests to inject a webcam, but fallback to the component that takes care of the management of webcams as they come and go
        return this.webcam != null ? this.webcam : ((WebcamComponent)getComponent()).getWebcam(getDeviceName(), getDefaultResolution());
    }

    public void setWebcam(Webcam webcam) {
        this.webcam = webcam;
    }

    public boolean isMotion() {
        return motion;
    }

    public void setMotion(boolean motion) {
        this.motion = motion;
    }

    public int getMotionInterval() {
        return motionInterval;
    }

    public void setMotionInterval(int motionInterval) {
        this.motionInterval = motionInterval;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getPixelThreshold() {
        return pixelThreshold;
    }

    public void setPixelThreshold(int pixelThreshold) {
        this.pixelThreshold = pixelThreshold;
    }

    public double getAreaThreshold() {
        return areaThreshold;
    }

    public void setAreaThreshold(double areaThreshold) {
        this.areaThreshold = areaThreshold;
    }

    public int getMotionInertia() {
        return motionInertia;
    }

    public void setMotionInertia(int motionInertia) {
        this.motionInertia = motionInertia;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String name) {
        this.deviceName = name;
    }
}
