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

public class BluetoothComponentWithServiceDiscoveryTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:shouldFindBluetoothDevicesWithServices")
    MockEndpoint findBluetoothDevicesMockEndpoint;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("bluetooth://scan?serviceDiscovery=true").to("mock:shouldFindBluetoothDevicesWithServices");
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
    public void shouldListBluetoothDevicesWithServices() throws InterruptedException {
        BluetoothDevice[] bluetoothDevices = consumer.receiveBody("bluetooth://scan?serviceDiscovery=true", BluetoothDevice[].class);
        assertEquals(2, bluetoothDevices.length);
        assertEquals("name1", bluetoothDevices[0].getName());
        assertEquals("address2", bluetoothDevices[1].getAddress());

        assertEquals(2, bluetoothDevices[0].getBluetoothServices().size());
        assertEquals(2, bluetoothDevices[1].getBluetoothServices().size());
    }

    @Test
    public void shouldListBluetoothDevicesWithServices2() throws InterruptedException {
        findBluetoothDevicesMockEndpoint.setExpectedMessageCount(1);
        findBluetoothDevicesMockEndpoint.assertIsSatisfied();
    }
}
