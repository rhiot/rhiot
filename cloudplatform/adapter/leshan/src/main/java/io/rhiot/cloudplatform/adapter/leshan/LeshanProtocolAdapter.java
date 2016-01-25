/**
 * Licensed to the Rhiot under one or more
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
package io.rhiot.cloudplatform.adapter.leshan;

import org.eclipse.leshan.server.californium.LeshanServerBuilder;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.client.ClientRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Protocol adapter connecting Leshan LWM2M events with an IoT Connector.
 */
public class LeshanProtocolAdapter {

    // Static collaborators

    private static final Logger LOG = getLogger(LeshanProtocolAdapter.class);

    // Collaborators

    private final LeshanServer leshanServer;

    // Constructor

    public LeshanProtocolAdapter(ClientRegistry clientRegistry, int port) {
        LeshanServerBuilder leshanServerBuilder = new LeshanServerBuilder();
        LOG.debug("Creating Leshan server using port {}.", port);
        leshanServerBuilder.setLocalAddress("0.0.0.0", port);
        leshanServer = leshanServerBuilder.setClientRegistry(clientRegistry).build();
    }

    // Lifecycle

    public void start() {
        leshanServer.start();
    }

    public void stop() {
        leshanServer.stop();
    }

}