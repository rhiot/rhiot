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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;

public class MockBluetoothDeviceProvider implements BluetoothDevicesProvider {

    public static final List<BluetoothDevice> BLUETOOTH_DEVICES = asList(new BluetoothDevice("name1", "address1"),
            new BluetoothDevice("name2", "address2"));

    public static final List<BluetoothDevice> BLUETOOTH_DEVICES_WITH_SERVICES = asList(
            new BluetoothDevice("name1", "address1", new HashSet<>(Arrays.asList(
                    new BluetoothService("serviceName1a", "serviceUrl1a"),
                    new BluetoothService("serviceName1b", "serviceUrl1b")
            ))),
            new BluetoothDevice("name2", "address2", new HashSet<>(Arrays.asList(
                    new BluetoothService("serviceName2a", "serviceUrl2a"),
                    new BluetoothService("serviceName2b", "serviceUrl2b")
            ))));

    @Override
    public List<BluetoothDevice> bluetoothDevices(boolean serviceDiscovery) {
        if (serviceDiscovery) {
            return BLUETOOTH_DEVICES_WITH_SERVICES;
        } else {
            return BLUETOOTH_DEVICES;
        }
    }
}
