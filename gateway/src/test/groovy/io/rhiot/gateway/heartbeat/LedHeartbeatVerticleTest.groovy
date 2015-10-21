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
package io.rhiot.gateway.heartbeat

import io.rhiot.gateway.test.GatewayTest
import org.apache.camel.component.mock.MockEndpoint
import org.junit.Test

class LedHeartbeatVerticleTest extends GatewayTest {

    @Override
    protected void doBefore() {
        System.setProperty('camellabs.iot.gateway.heartbeat.led', 'true')
        System.setProperty('camellabs.iot.gateway.heartbeat.led.component', 'mock')
    }

    @Test
    void shouldReceiveHeartbeatFromEventBus() {
        // Given
        def mockLedEndpoint = camelContext.getEndpoint('mock:0?mode=DIGITAL_OUTPUT&state=LOW&action=BLINK', MockEndpoint.class)
        mockLedEndpoint.setMinimumExpectedMessageCount(1)

        // When
        mockLedEndpoint.assertIsSatisfied()
    }

}
