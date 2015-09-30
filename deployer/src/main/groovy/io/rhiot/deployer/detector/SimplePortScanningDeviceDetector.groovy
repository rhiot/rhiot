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
package io.rhiot.deployer.detector

import com.github.camellabs.iot.utils.ssh.client.SshClient
import org.slf4j.Logger

import java.util.concurrent.Callable
import java.util.concurrent.TimeoutException

import static com.google.common.collect.Lists.newLinkedList
import static java.lang.Integer.parseInt
import static java.util.Collections.emptyList
import static java.util.concurrent.Executors.newCachedThreadPool
import static java.util.concurrent.TimeUnit.MILLISECONDS
import static org.slf4j.LoggerFactory.getLogger

class SimplePortScanningDeviceDetector implements DeviceDetector {

    // Constants

    private static final DEFAULT_PING_TIMEOUT = 500

    // Logger

    private final static Logger LOG = getLogger(SimplePortScanningDeviceDetector.class);

    // Configuration members

    private final String username

    private final String password

    private final int timeout;

    // Collaborators

    private final InterfacesProvider interfacesProvider

    private final executor = newCachedThreadPool()

    // Constructors

    SimplePortScanningDeviceDetector(InterfacesProvider interfacesProvider, String username, String password, int timeout) {
        this.interfacesProvider = interfacesProvider
        this.username = username
        this.password = password
        this.timeout = timeout
    }

    SimplePortScanningDeviceDetector(int timeout) {
        this(new JavaNetInterfaceProvider(), null, null, timeout)
    }

    SimplePortScanningDeviceDetector() {
        this(DEFAULT_PING_TIMEOUT);
    }

    SimplePortScanningDeviceDetector(InterfacesProvider interfacesProvider) {
        this(interfacesProvider, null, null, DEFAULT_PING_TIMEOUT)
    }

    // Lifecycle

    void close() {
        executor.shutdown()
    }

    // Operations

    List<Inet4Address> detectReachableAddresses() {
        def networkInterfaces = interfacesProvider.interfaces()
        if (networkInterfaces.isEmpty()) {
            return emptyList();
        }

        List<Inet4Address> addressesToScan = newLinkedList()
        networkInterfaces.each {
                def address = it.broadcast
                int lastDot = address.lastIndexOf('.') + 1;
                def addressBase = address.substring(0, lastDot);
                int addressesNumber = parseInt(address.substring(lastDot));
                for (int i = 0; i < addressesNumber; i++) {
                    addressesToScan.add((Inet4Address) Inet4Address.getByName(addressBase + (i + 1)));
                }
        }
        addressesToScan.collect {
            executor.submit(new Callable<ScanResult>() {
                @Override
                ScanResult call() throws Exception {
                    try {
                        return new ScanResult(it, it.isReachable(timeout));
                    } catch (SocketException e) {
                        if (e.message.contains('Permission denied')) {
                            LOG.debug('Cannot scan {} - permission denied.', it)
                            return new ScanResult(it, false)
                        } else {
                            throw new RuntimeException(e);
                        }
                    }
                }
            })
        }.findAll {it.get().isReachable()}.collect{it.get().address()}
    }

    List<Device> detectDevices() {
        detectReachableAddresses().collect { device ->
            executor.submit(new Callable<Device>() {
                @Override
                Device call() throws Exception {
                    try {
                        new SshClient(device.hostAddress, 22, username, password).command("echo ping");
                        new Device(device, Device.DEVICE_RASPBERRY_PI_2)
                    } catch (Exception ex) {
                        LOG.debug("Can't connect to the Raspberry Pi device: " + device.getHostAddress(), ex);
                        return null
                    }
                }
            })
        }.collect{try{it.get(timeout, MILLISECONDS)}catch(TimeoutException ex){return null}}.findAll{it != null}
    }

    private static class ScanResult {

        private final Inet4Address address

        private final boolean reachable

        ScanResult(Inet4Address address, boolean reachable) {
            this.address = address
            this.reachable = reachable
        }

        Inet4Address address() {
            return address
        }

        boolean isReachable() {
            return reachable
        }

    }

}