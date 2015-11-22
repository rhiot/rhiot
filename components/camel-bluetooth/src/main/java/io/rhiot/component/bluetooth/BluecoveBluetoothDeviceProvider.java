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
import javax.bluetooth.UUID;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Documentation links
 * UUID: http://bluecove.org/bluecove/apidocs/javax/bluetooth/UUID.html
 * ServiceRecord: http://bluecove.org/bluecove/apidocs/javax/bluetooth/ServiceRecord.html
 */
public class BluecoveBluetoothDeviceProvider implements BluetoothDevicesProvider {

    private static final Logger LOG = LoggerFactory.getLogger(BluecoveBluetoothDeviceProvider.class);

    private static final UUID[] SERVICE_TYPE = new UUID[] {
            new UUID(0x0001),                       //SDP
            new UUID(0x0003),                       //RFCOMM
            new UUID(0x0008),                       //OBEX
            new UUID(0x000C),                       //HTTP
            new UUID(0x0100),                       //L2CAP
            new UUID(0x000F),                       //BNEP
            new UUID(0x1101),                       //Serial Port
            new UUID(0x1000),                       //ServiceDiscoveryServerServiceClassID
            new UUID(0x1001),                       //BrowseGroupDescriptorServiceClassID
            new UUID(0x1002),                       //PublicBrowseGroup
            new UUID(0x1105),                       //OBEX Object Push Profile
            new UUID(0x1106),                       //OBEX File Transfer Profile
            new UUID(0x1115),                       //Personal Area Networking User
            new UUID(0x1116),                       //Network Access Point
            new UUID(0x1117)                        //Group Network
    };

    private static final int[] ATTR_ID = new int[] {
            0x0100                                   //Service name
    };

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
    public List<BluetoothDevice> bluetoothDevices(boolean serviceDiscovery) {
        DiscoveryListenerImpl discoveryListenerImpl;
        Map<RemoteDevice, BluetoothDevice> bluetoothDeviceMap = new HashMap<>();
        CountDownLatch lock = new CountDownLatch(1);

        DiscoveryAgent agent = localDevice.getDiscoveryAgent();

        try {
            discoveryListenerImpl = new DiscoveryListenerImpl(lock, bluetoothDeviceMap);
            agent.startInquiry(DiscoveryAgent.GIAC, discoveryListenerImpl);
            lock.await();

            if (serviceDiscovery) {
                bluetoothDeviceMap.keySet().stream()
                        .forEach(remoteDevice -> searchServices(discoveryListenerImpl, agent, remoteDevice));
            }

        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ArrayList<>(bluetoothDeviceMap.values());
        }
        return new ArrayList<>(discoveryListenerImpl.getBluetoothDeviceMap().values());
    }

    private void searchServices(DiscoveryListenerImpl discoveryListenerImpl, DiscoveryAgent agent, RemoteDevice remoteDevice) {
        CountDownLatch lock;
        try {
            for (UUID aSERVICE_TYPE : SERVICE_TYPE) {
                lock = discoveryListenerImpl.resetLock();
                agent.searchServices(ATTR_ID, new UUID[]{aSERVICE_TYPE}, remoteDevice, discoveryListenerImpl);
                lock.await();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    class DiscoveryListenerImpl implements DiscoveryListener {

        private CountDownLatch lock;
        private Map<RemoteDevice, BluetoothDevice> bluetoothDeviceMap;

        public DiscoveryListenerImpl(CountDownLatch lock, Map<RemoteDevice, BluetoothDevice> bluetoothDeviceMap) {
            this.lock = lock;
            this.bluetoothDeviceMap = bluetoothDeviceMap;
        }

        public Map<RemoteDevice, BluetoothDevice> getBluetoothDeviceMap() {
            return bluetoothDeviceMap;
        }

        @Override
        public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
            String name = "";
            try {
                name = remoteDevice.getFriendlyName(false);
            } catch (IOException e) {
                LOG.error("Could not get device name on address {}", remoteDevice.getBluetoothAddress());
            }
            bluetoothDeviceMap.put(remoteDevice, new BluetoothDevice(name, remoteDevice.getBluetoothAddress()));
        }

        @Override
        public void servicesDiscovered(int arg0, ServiceRecord[] serviceRecords) {
            for (ServiceRecord serviceRecord : serviceRecords) {
                String url = serviceRecord.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                if (url == null) {
                    continue;
                }

                DataElement serviceName = serviceRecord.getAttributeValue(0x0100);

                if (serviceName != null) {
                    bluetoothDeviceMap.get(serviceRecord.getHostDevice()).getBluetoothServices()
                            .add(new BluetoothService(serviceName.getValue().toString(), url));
                }
            }
        }

        @Override
        public void serviceSearchCompleted(int i, int i1) {
            lock.countDown();
        }

        @Override
        public void inquiryCompleted(int i) {
            lock.countDown();
        }

        protected CountDownLatch resetLock() {
            lock = new CountDownLatch(1);
            return lock;
        }
    }
}
