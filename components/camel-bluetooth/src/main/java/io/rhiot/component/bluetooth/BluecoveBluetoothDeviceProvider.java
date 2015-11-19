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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.bluetooth.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class BluecoveBluetoothDeviceProvider implements BluetoothDevicesProvider {

    private static final Logger LOG = LoggerFactory.getLogger(BluecoveBluetoothDeviceProvider.class);

    private LocalDevice localDevice;

    public BluecoveBluetoothDeviceProvider() {
        try {
            this.localDevice = LocalDevice.getLocalDevice();
            LOG.info("Local bluetooth device({}) with address {}", localDevice.getFriendlyName(),
                    localDevice.getBluetoothAddress());
        } catch (BluetoothStateException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<BluetoothDevice> bluetoothDevices() {
        DiscoveryListenerImpl discoveryListenerImpl;
        List<BluetoothDevice> bluetoothDevices = new ArrayList<>();
        CountDownLatch lock = new CountDownLatch(1);

        DiscoveryAgent agent = localDevice.getDiscoveryAgent();

        try {
            discoveryListenerImpl = new DiscoveryListenerImpl(lock, bluetoothDevices);
            agent.startInquiry(DiscoveryAgent.GIAC, discoveryListenerImpl);
            lock.await();
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return bluetoothDevices;
        }
        return discoveryListenerImpl.getBluetoothDevices();
    }

    class DiscoveryListenerImpl implements DiscoveryListener {

        private CountDownLatch lock;
        private List<BluetoothDevice> bluetoothDevices;

        public DiscoveryListenerImpl(CountDownLatch lock, List<BluetoothDevice> bluetoothDevices) {
            this.lock = lock;
            this.bluetoothDevices = bluetoothDevices;
        }

        public List<BluetoothDevice> getBluetoothDevices() {
            return bluetoothDevices;
        }

        @Override
        public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
            String name = "";
            try {
                name = remoteDevice.getFriendlyName(false);

            } catch (IOException e) {
                LOG.error("Could not get device name on address {}", remoteDevice.getBluetoothAddress());
            }
            bluetoothDevices.add(new BluetoothDevice(name, remoteDevice.getBluetoothAddress()));
        }

        @Override
        public void servicesDiscovered(int i, ServiceRecord[] serviceRecords) {

        }

        @Override
        public void serviceSearchCompleted(int i, int i1) {

        }

        @Override
        public void inquiryCompleted(int i) {
            lock.countDown();
        }
    }
}
