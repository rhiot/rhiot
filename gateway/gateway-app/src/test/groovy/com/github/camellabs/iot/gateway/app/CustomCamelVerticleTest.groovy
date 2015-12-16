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
import io.rhiot.gateway.test.GatewayTest
import io.rhiot.vertx.camel.GroovyCamelVerticle
import org.apache.camel.component.mock.MockEndpoint
import org.junit.Test

import static io.rhiot.steroids.camel.CamelBootInitializer.camelContext

class CustomCamelVerticleTest extends GatewayTest {

    // Tests

    @Test
    void shouldReceiveHeartbeat() {
        // Given
        def mockEndpoint = camelContext().getEndpoint('mock:camelHeartbeatConsumer', MockEndpoint.class)
        mockEndpoint.setMinimumExpectedMessageCount(1)

        // Then
        mockEndpoint.assertIsSatisfied()
    }

}

@GatewayVerticle
class HeartbeatConsumerVerticle extends GroovyCamelVerticle {

    @Override
    void start() {
        fromEventBus('heartbeat') { it.to('mock:camelHeartbeatConsumer') }
    }

}
