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
package io.rhiot.component.gps.bu353;

import io.rhiot.utils.process.FixedMockProcessManager;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class GpsBu353ConsumerTest extends CamelTestSupport {

    // Collaborators fixtures

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        registry.bind("gpsCoordinatesSource", new MockGpsCoordinatesSource());
        registry.bind("processManager", new FixedMockProcessManager("gpsctl:ERROR: /dev/ttyUSB0 mode change to NMEA failed"));
        return registry;
    }

    // Route fixtures

    @EndpointInject(uri = "mock:shouldReadTwoGpsCoordinates")
    MockEndpoint mockEndpoint;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("gps-bu353://shouldReadTwoGpsCoordinates").
                        to("mock:shouldReadTwoGpsCoordinates");
            }
        };
    }

    // Tests

    @Test
    public void shouldReadGpsCoordinates() {
        ClientGpsCoordinates coordinates = consumer.receiveBody("gps-bu353://gps", ClientGpsCoordinates.class);
        assertEquals(49.82, coordinates.lat(), 0.01);
        assertEquals(19.05, coordinates.lng(), 0.01);
    }

    @Test
    public void shouldReadTwoGpsCoordinates() throws InterruptedException {
        mockEndpoint.setExpectedCount(2);
        mockEndpoint.assertIsSatisfied();
    }

}
