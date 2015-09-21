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
package com.github.camellabs.iot.gateway.app;

import com.github.camellabs.iot.gateway.GatewayVerticle;
import com.github.camellabs.iot.gateway.Gateway;
import com.github.camellabs.iot.vertx.camel.JavaCamelVerticle;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static com.github.camellabs.iot.vertx.camel.CamelContextFactories.camelContext;
import static com.github.camellabs.iot.vertx.camel.CamelContextFactories.mockEndpoint;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.apache.camel.component.mock.MockEndpoint.assertIsSatisfied;

public class CustomJavaCamelVerticleTest {

    static Gateway gateway = new Gateway();

    @BeforeClass
    public static void beforeClass() throws InterruptedException {
        gateway.start();
        TimeUnit.SECONDS.sleep(30);
    }

    @AfterClass
    public static void afterClass() {
        gateway.stop();
    }

    @Test
    public void shouldReceiveHeartbeat() throws InterruptedException {
        // Given
        MockEndpoint mockEndpoint = mockEndpoint("mock:customJava");
        mockEndpoint.setMinimumExpectedMessageCount(1);

        // Then
        assertIsSatisfied(5, MINUTES, mockEndpoint);
    }

}

@GatewayVerticle
class CustomJavaHeartbeatConsumerVerticle extends JavaCamelVerticle {

    @Override
    public void start() throws Exception {
        fromEventBus("heartbeat", route -> route.to("mock:customJava"));
    }

}