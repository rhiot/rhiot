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

import io.rhiot.gateway.GatewayVerticle;
import io.rhiot.gateway.Gateway;
import io.rhiot.gateway.test.GatewayTest;
import io.rhiot.steroids.camel.CamelBootInitializer;
import io.rhiot.vertx.camel.JavaCamelVerticle;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class CustomJavaCamelVerticleTest extends GatewayTest {

    @Test
    public void shouldReceiveHeartbeat() throws InterruptedException {
        // Given
        MockEndpoint mockEndpoint = CamelBootInitializer.camelContext().getEndpoint("mock:customJava", MockEndpoint.class);
        mockEndpoint.setMinimumExpectedMessageCount(1);

        // Then
        mockEndpoint.assertIsSatisfied();
    }

}

@GatewayVerticle
class CustomJavaHeartbeatConsumerVerticle extends JavaCamelVerticle {

    @Override
    public void start() throws Exception {
        fromEventBus("heartbeat", route -> route.to("mock:customJava"));
    }

}