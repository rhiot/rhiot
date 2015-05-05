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
package com.github.camellabs.iot.component.kura.wifi;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.eclipse.kura.net.wifi.WifiAccessPoint;
import org.junit.Test;

public class KuraComponentTest extends CamelTestSupport {

    // Routes fixtures

    @EndpointInject(uri = "mock:shouldFilterSsid")
    MockEndpoint filteredAccessPointMockEndpoint;

    @EndpointInject(uri = "mock:shouldFindAllAccessPoints")
    MockEndpoint allAccessPointsMockEndpoint;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("kura-wifi:*/ssid1?accessPointsProvider=#accessPointsProvider").to("mock:shouldFilterSsid");

                from("kura-wifi:*/*?accessPointsProvider=#accessPointsProvider").to("mock:shouldFindAllAccessPoints");
            }
        };
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        registry.bind("accessPointsProvider", new MockAccessPointProvider());
        return registry;
    }

    // Tests

    @Test
    public void shouldFilterSsid() throws InterruptedException {
        filteredAccessPointMockEndpoint.setExpectedMessageCount(1);
        filteredAccessPointMockEndpoint.assertIsSatisfied();
        WifiAccessPoint[] accessPoint = filteredAccessPointMockEndpoint.getExchanges().get(0).getIn().getBody(WifiAccessPoint[].class);
        assertEquals("ssid1", accessPoint[0].getSSID());
    }

    @Test
    public void shouldFindAllAccessPoints() throws InterruptedException {
        allAccessPointsMockEndpoint.setExpectedMessageCount(1);
        allAccessPointsMockEndpoint.assertIsSatisfied();
        WifiAccessPoint[] accessPoint = allAccessPointsMockEndpoint.getExchanges().get(0).getIn().getBody(WifiAccessPoint[].class);
        assertEquals(2, accessPoint.length);
    }

    @Test
    public void shouldFindAllAccessPointsUsingProducer() {
        WifiAccessPoint[] accessPoints = template.requestBody("kura-wifi:*/*?accessPointsProvider=#accessPointsProvider", null, WifiAccessPoint[].class);
        assertEquals(2, accessPoints.length);
    }

}
