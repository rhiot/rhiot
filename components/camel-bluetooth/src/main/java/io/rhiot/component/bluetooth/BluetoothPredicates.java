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

import org.apache.camel.Predicate;

public class BluetoothPredicates {

    public static Predicate deviceWithName(String deviceName) {
        return exchange -> Arrays.stream(exchange.getIn().getBody(BluetoothDevice[].class))
                .filter(bluetoothDevice -> bluetoothDevice.getName().equals(deviceName)).count() > 0;
    }

    public static Predicate deviceWithAddress(String deviceAddress) {
        return exchange -> Arrays.stream(exchange.getIn().getBody(BluetoothDevice[].class))
                .filter(bluetoothDevice -> bluetoothDevice.getAddress().equals(deviceAddress)).count() > 0;
    }

    public static Predicate deviceWithServiceName(String serviceName) {
        return exchange -> Arrays.stream(exchange.getIn().getBody(BluetoothDevice[].class))
                .flatMap(bluetoothDevice -> bluetoothDevice.getBluetoothServices().stream())
                .filter(bluetoothService -> bluetoothService.getName().equals(serviceName))
                .count() > 0;
    }
}
