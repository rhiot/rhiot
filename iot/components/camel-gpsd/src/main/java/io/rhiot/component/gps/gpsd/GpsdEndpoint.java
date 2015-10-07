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
package io.rhiot.component.gps.gpsd;

import de.taimos.gpsd4java.backend.GPSdEndpoint;
import de.taimos.gpsd4java.backend.ResultParser;
import io.rhiot.utils.process.DefaultProcessManager;
import io.rhiot.utils.process.ProcessManager;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

import static java.lang.Thread.sleep;

/**
 * Represents a Gpsd endpoint.
 */
@UriEndpoint(scheme = "gpsd", title = "Gpsd", syntax="gpsd:name", consumerClass = GpsdConsumer.class, label = "Gpsd")
public class GpsdEndpoint extends DefaultEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(GpsdEndpoint.class);

    private int gpsdRestartInterval = 5000;

    private ProcessManager processManager;

    @UriParam(defaultValue = "2947")
    private int port = 2947;
    @UriParam(defaultValue = "localhost")
    private String host = "localhost";
    @UriParam(defaultValue = "0", description = "Distance in km, eg 0.1 for 100m")
    private double distance = 0;

    private GPSdEndpoint gpsd4javaEndpoint;
    
    public GpsdEndpoint() {
    }

    public GpsdEndpoint(String uri, GpsdComponent component) {
        super(uri, component);
    }

    public GpsdEndpoint(String endpointUri) {
        super(endpointUri);
    }

    // Producer/consumer factories

    @Override
    public Producer createProducer() throws Exception {
        throw new UnsupportedOperationException("GPSD component supports only consumer endpoints.");
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        GpsdConsumer consumer = new GpsdConsumer(this, processor);
        configureConsumer(consumer);
        return consumer;
    }

    // Life cycle

    @Override
    protected void doStart() throws Exception {
        // localhost endpoint should also start the component
        
        if ("localhost".equals(getHost())) {
            restartGpsDaemon();
        }
        gpsd4javaEndpoint = new GPSdEndpoint(host, port, new ResultParser());
        super.doStart();
    }

    @Override
    protected void doStop() throws Exception {
        if (gpsd4javaEndpoint != null) {
            gpsd4javaEndpoint.stop();
        }
        super.doStop();
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
        try {
            ProcessManager processManager = resolveProcessManager();
            List<String> gpsctlResult;
            do {
                LOG.info("(Re)starting GPS daemon.");
                processManager.executeAndJoinOutput("killall", "gpsd");
                processManager.executeAndJoinOutput("gpsd", "/dev/ttyUSB0");
                sleep(gpsdRestartInterval);
                gpsctlResult = processManager.executeAndJoinOutput("gpsctl", "-n", "/dev/ttyUSB0");
                LOG.info("gpsctl result: {}", gpsctlResult);
            } while (!gpsctlResult.contains("gpsctl:ERROR: /dev/ttyUSB0 mode change to NMEA failed"));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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

    public ProcessManager getProcessManager() {
        return processManager;
    }

    public void setProcessManager(ProcessManager processManager) {
        this.processManager = processManager;
    }

}