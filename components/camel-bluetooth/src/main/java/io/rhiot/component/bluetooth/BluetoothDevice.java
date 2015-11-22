/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rhiot.component.bluetooth;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class BluetoothDevice implements Serializable {
    private String name;
    private String address;
    private Set<BluetoothService> bluetoothServices;

    public BluetoothDevice(String name, String address) {
        this(name, address, new HashSet<>());
    }

    public BluetoothDevice(String name, String address, Set<BluetoothService> bluetoothServices) {
        this.name = name;
        this.address = address;
        this.bluetoothServices = bluetoothServices;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public Set<BluetoothService> getBluetoothServices() {
        return bluetoothServices;
    }

    public void setBluetoothServices(Set<BluetoothService> bluetoothServices) {
        this.bluetoothServices = bluetoothServices;
    }

    @Override
    public String toString() {
        return "BluetoothDevice{" + "name='" + name + '\'' + ", address='" + address + '\'' + ", bluetoothServices=" + bluetoothServices + '}';
    }
}
