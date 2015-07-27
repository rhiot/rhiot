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
package com.github.camellabs.iot.gateway.app;

import com.github.camellabs.iot.gateway.GatewayVerticle;
import com.github.camellabs.iot.gateway.Gateway;
import com.github.camellabs.iot.vertx.camel.JavaCamelVerticle;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.github.camellabs.iot.vertx.camel.CamelContextFactories.camelContext;
import static java.util.concurrent.TimeUnit.MINUTES;

public class CustomJavaCamelVerticleTest {

    static Gateway vertxGateway = new Gateway();

    @BeforeClass
    public static void beforeClass() {
        vertxGateway.start();
    }

    @AfterClass
    public static void afterClass() {
        vertxGateway.stop();
    }

    @Test
    public void shouldReceiveHeartbeat() throws InterruptedException {
        // Given
        MockEndpoint mockEndpoint = camelContext().getEndpoint("mock:customJava", MockEndpoint.class);
        mockEndpoint.setMinimumExpectedMessageCount(1);

        // Then
        MockEndpoint.assertIsSatisfied(1, MINUTES, mockEndpoint);
    }

}

@GatewayVerticle
class CustomJavaHeartbeatConsumerVerticle extends JavaCamelVerticle {

    @Override
    public void start() throws Exception {
        fromEventBus("heartbeat", route -> route.to("mock:customJava"));
    }

}