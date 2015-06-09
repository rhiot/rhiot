/**
 * Licensed to the Camel Labs under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.camellabs.iot.component.gps.bu353;

import com.github.camellabs.iot.utils.process.DefaultProcessManager;
import com.github.camellabs.iot.utils.process.ProcessManager;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.UriEndpointComponent;

import java.util.Map;
import java.util.Set;

public class GpsBu353Component extends UriEndpointComponent {

    public GpsBu353Component() {
        super(GpsBu353Endpoint.class);
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        GpsBu353Endpoint endpoint = new GpsBu353Endpoint(uri, this);
        setProperties(endpoint, parameters);
        return endpoint;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        ProcessManager processManager = resolveProcessManager();
        processManager.executeAndJoinOutput("killall", "gpsd");
        processManager.executeAndJoinOutput("gpsd", "/dev/ttyUSB0");
        processManager.executeAndJoinOutput("killall", "gpsd");
        processManager.executeAndJoinOutput("gpsd", "/dev/ttyUSB0");
        processManager.executeAndJoinOutput("gpsctl", "-f", "-n", "/dev/ttyUSB0");
    }

    protected ProcessManager resolveProcessManager() {
        Set<ProcessManager> processManagers = getCamelContext().getRegistry().findByType(ProcessManager.class);
        if(processManagers.isEmpty()) {
            return new DefaultProcessManager();
        } else if(processManagers.size() == 1) {
            return processManagers.iterator().next();
        } else {
            return new DefaultProcessManager();
        }
    }

}
