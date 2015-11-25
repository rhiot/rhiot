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

import de.taimos.gpsd4java.backend.GPSdEndpoint;
import de.taimos.gpsd4java.backend.ResultParser;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a Gpsd endpoint.
 */
@UriEndpoint(scheme = "gpsd", title = "Gpsd", syntax="gpsd:name", consumerClass = GpsdDefaultConsumer.class, label = "Gpsd")
public class GpsdEndpoint extends DefaultEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(GpsdEndpoint.class);

    private int gpsdRestartInterval = 5000;

    @UriParam(defaultValue = "false", description = "Whether the consumer is scheduled or not")
    private boolean scheduled = false;
    @UriParam(defaultValue = "2947")
    private int port = 2947;
    @UriParam(defaultValue = "localhost")
    private String host = "localhost";

    @UriParam(defaultValue = "0", description = "Distance in km, eg 0.1 for 100m")
    private double distance = 0;

    @UriParam(defaultValue = "true", description = "Indicates if the endpoint should try (re)start the local GPSD daemon on start.")
    private boolean restartGpsd = true;

    private GPSdEndpoint gpsd4javaEndpoint;
    
    public GpsdEndpoint(String uri, GpsdComponent component) {
        super(uri, component);
    }

    // Producer/consumer factories

    @Override
    public Producer createProducer() throws Exception {
        return new GpsdProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        LOG.debug("Creating GPSD consumer.");
        Consumer consumer = isScheduled() ? new GpsdScheduledConsumer(this, processor) : new GpsdDefaultConsumer(this, processor);

        if(isScheduled()) {
            if(!getConsumerProperties().containsKey("delay")) {
                ((GpsdScheduledConsumer) consumer).setDelay(5000);
            }
        }
        configureConsumer(consumer);
        return consumer;
    }

    // Life cycle

    @Override
    protected void doStart() throws Exception {
        
        // localhost endpoint should also start the component

        if (restartGpsd && "localhost".equals(getHost())) {
            ((GpsdComponent)getComponent()).restartGpsDaemon();
        }
        if(gpsd4javaEndpoint == null) {
            gpsd4javaEndpoint = new GPSdEndpoint(host, port, new ResultParser());
        }
        gpsd4javaEndpoint.start();

        LOG.info("GPSD Version: {}", gpsd4javaEndpoint.version());

        LOG.debug("Starting GPSD WATCH...");
        gpsd4javaEndpoint.watch(true, true);
        LOG.debug("Started GPSD WATCH.");

        super.doStart();
    }

    @Override
    protected void doStop() throws Exception {
        if (gpsd4javaEndpoint != null) {
            gpsd4javaEndpoint.stop();
            gpsd4javaEndpoint = null;
        }
        super.doStop();
    }

    // Configuration

    @Override
    public boolean isSingleton() {
        return true;
    }

    // Configuration getters and setters


    public GPSdEndpoint getGpsd4javaEndpoint() {
        return gpsd4javaEndpoint;
    }

    public void setGpsd4javaEndpoint(GPSdEndpoint gpsd4javaEndpoint) {
        this.gpsd4javaEndpoint = gpsd4javaEndpoint;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }


    public int getGpsdRestartInterval() {
        return gpsdRestartInterval;
    }

    public void setGpsdRestartInterval(int gpsdRestartInterval) {
        this.gpsdRestartInterval = gpsdRestartInterval;
    }

    public boolean isScheduled() {
        return scheduled;
    }

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }

    public void setRestartGpsd(boolean restartGpsd) {
        this.restartGpsd = restartGpsd;
    }

}