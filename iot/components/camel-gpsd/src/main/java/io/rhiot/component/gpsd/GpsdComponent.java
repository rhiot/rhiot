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

import io.rhiot.utils.process.DefaultProcessManager;
import io.rhiot.utils.process.ProcessManager;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;

import org.apache.camel.impl.UriEndpointComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Thread.sleep;

/**
 * Represents the component that manages {@link GpsdEndpoint}.
 */
public class GpsdComponent extends UriEndpointComponent {
    
    private static final Logger LOG = LoggerFactory.getLogger(GpsdComponent.class);

    private int gpsdRestartInterval = 5000;

    private ProcessManager processManager;
    
    private CountDownLatch isLocalGpsdStarting = new CountDownLatch(1);
    private CountDownLatch isLocalGpsdStarted = new CountDownLatch(1);
    private boolean gpsdStarted;
    
    public GpsdComponent() {
        super(GpsdEndpoint.class);
    }

    public GpsdComponent(CamelContext context) {
        super(context, GpsdEndpoint.class);
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
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
            
            ProcessManager processManager = resolveProcessManager();
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


    public ProcessManager getProcessManager() {
        return processManager;
    }

    public void setProcessManager(ProcessManager processManager) {
        this.processManager = processManager;
    }
}
