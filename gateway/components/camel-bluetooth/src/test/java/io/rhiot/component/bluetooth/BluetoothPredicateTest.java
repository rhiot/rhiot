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
package io.rhiot.component.bluetooth;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import static io.rhiot.component.bluetooth.BluetoothPredicates.deviceWithName;

public class BluetoothPredicateTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:shouldFilterBluetoothDevices")
    MockEndpoint filterdBluetoothDevicesMockEndpoint;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("bluetooth://scan").routeId("two").filter(deviceWithName("name3"))
                        .to("mock:shouldFilterBluetoothDevices");
            }
        };
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        registry.bind("bluetoothDeviceProvider", new MockBluetoothDeviceProvider());

        return registry;
    }

    @Test
    public void shouldFilterBluetoothDevicesUsingDedicatedPredicates() throws InterruptedException {
        filterdBluetoothDevicesMockEndpoint.setExpectedMessageCount(0);
        filterdBluetoothDevicesMockEndpoint.assertIsSatisfied();
    }

}
