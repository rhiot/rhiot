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
package io.rhiot.datastream.source.leshan;

import org.eclipse.leshan.server.californium.LeshanServerBuilder;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.client.ClientRegistry;

public class LeshanDataStreamSource {

    private final ClientRegistry clientRegistry;

    private final LeshanServer leshanServer;

    public LeshanDataStreamSource(ClientRegistry clientRegistry) {
        this.clientRegistry = clientRegistry;
        LeshanServerBuilder leshanServerBuilder = new LeshanServerBuilder();
        leshanServerBuilder.setLocalAddress("0.0.0.0", LeshanServerBuilder.PORT);
        leshanServer = leshanServerBuilder.setClientRegistry(clientRegistry).build();
    }

    public void start() {
        leshanServer.start();
    }

}