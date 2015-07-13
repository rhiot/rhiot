/**
 * Licensed to the Camel Labs under one or more
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
package com.github.camellabs.iot.deployer

import com.github.camellabs.iot.utils.ssh.client.SshClient
import org.slf4j.Logger

import static com.github.camellabs.iot.deployer.Device.DEVICE_RASPBERRY_PI_2
import static com.google.common.collect.Lists.newLinkedList
import static java.lang.Integer.parseInt
import static java.net.NetworkInterface.getNetworkInterfaces
import static java.util.Collections.emptyList
import static java.util.Collections.list
import static java.util.stream.Collectors.toList
import static org.slf4j.LoggerFactory.getLogger

class SimplePortScanningDeviceDetector implements DeviceDetector {

    // Constants

    private static final int DEFAULT_PING_TIMEOUT = 500;

    // Logger

    private final static Logger LOG = getLogger(SimplePortScanningDeviceDetector.class);

    // Configuration members

    private final int timeout;

    // Constructors

    public SimplePortScanningDeviceDetector(int timeout) {
        this.timeout = timeout;
    }

    public SimplePortScanningDeviceDetector() {
        this(DEFAULT_PING_TIMEOUT);
    }

    // Operations

    List<Inet4Address> detectReachableAddresses() {
        List<NetworkInterface> networkInterfaces = list(getNetworkInterfaces()).parallelStream().
                filter { iface -> iface.getDisplayName().startsWith("wlan") || iface.getDisplayName().startsWith("eth") }.
                collect(toList());

        if (networkInterfaces.isEmpty()) {
            return emptyList();
        }

        InterfaceAddress interfaceAddress = networkInterfaces.get(0).getInterfaceAddresses().parallelStream().
                filter { ifaceAddress -> ifaceAddress.getAddress().getHostAddress().length() < 15 }.
                collect(toList()).get(0);
        String address = interfaceAddress.getBroadcast().getHostAddress();
        int lastDot = address.lastIndexOf('.') + 1;
        String addressBase = address.substring(0, lastDot);
        int addressesNumber = parseInt(address.substring(lastDot));
        List<Inet4Address> addressesToScan = newLinkedList();
        for (int i = 0; i < addressesNumber; i++) {
            addressesToScan.add((Inet4Address) Inet4Address.getByName(addressBase + (i + 1)));
        }
        return addressesToScan.parallelStream().filter {
            addressToScan ->
                try {
                    return addressToScan.isReachable(timeout);
                } catch (SocketException e) {
                    if (e.message.contains('Permission denied')) {
                        LOG.debug('Cannot scan {} - permission denied.', addressesToScan)
                        return false
                    } else {
                        throw new RuntimeException(e);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

        }.collect(toList());
    }

    public List<Device> detectDevices() {
        List<Device> devices = newLinkedList();
        detectReachableAddresses().parallelStream().forEach { device ->
            try {
                new SshClient(device.getHostAddress(), 22, "pi", "raspberry").command("echo ping");
                devices.add(new Device(device, DEVICE_RASPBERRY_PI_2));
            } catch (Exception ex) {
                LOG.debug("Can't connect to the Raspberry Pi device: " + device.getHostAddress(), ex);
            }
        };
        return devices;
    }

}