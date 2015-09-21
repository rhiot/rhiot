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
package com.github.camellabs.iot.gateway.app

import io.rhiot.gateway.GatewayVerticle
import io.rhiot.gateway.Gateway
import com.github.camellabs.iot.vertx.camel.GroovyCamelVerticle
import org.apache.camel.component.mock.MockEndpoint
import org.junit.BeforeClass
import org.junit.Test

import static com.github.camellabs.iot.vertx.camel.CamelContextFactories.camelContext
import static java.util.concurrent.TimeUnit.MINUTES
import static java.util.concurrent.TimeUnit.SECONDS

class CustomCamelVerticleTest {

    @BeforeClass
    static void beforeClass() {
        new Gateway().start()
        SECONDS.sleep(60) // Attempt to fix the unstable build
    }

    // Tests

    @Test
    void shouldReceiveHeartbeat() {
        // Given
        def mockEndpoint = camelContext().getEndpoint('mock:camelHeartbeatConsumer', MockEndpoint.class)
        mockEndpoint.setMinimumExpectedMessageCount(1)

        // Then
        MockEndpoint.assertIsSatisfied(1, MINUTES, mockEndpoint)
    }

}

@GatewayVerticle
class HeartbeatConsumerVerticle extends GroovyCamelVerticle {

    @Override
    void start() {
        fromEventBus('heartbeat') { it.to('mock:camelHeartbeatConsumer') }
    }

}
