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
package io.rhiot.scanner

import io.rhiot.utils.WithLogger
import io.rhiot.utils.ssh.client.SshClient

import java.util.concurrent.Callable
import java.util.concurrent.TimeoutException

import static com.google.common.base.MoreObjects.firstNonNull
import static com.google.common.collect.Lists.newLinkedList
import static java.util.Collections.emptyList
import static java.util.concurrent.Executors.newCachedThreadPool
import static java.util.concurrent.TimeUnit.SECONDS

class SimplePortScanningDeviceDetector implements DeviceDetector, WithLogger {

    // Constants

    private static final DEFAULT_PING_TIMEOUT = 500

    // Configuration members

    private final String username

    private final String password

    private final int timeout

    // Collaborators

    private final InterfacesProvider interfacesProvider

    private final executor = newCachedThreadPool()

    // Constructors

    SimplePortScanningDeviceDetector(InterfacesProvider interfacesProvider, String username, String password, int timeout) {
        this.interfacesProvider = interfacesProvider
        this.username = firstNonNull(username, 'pi')
        this.password = firstNonNull(password, 'raspberry')
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

    List<Device> detectDevices(int port) {
        if(port <= 0)
            port = 22;
        def networkInterfaces = interfacesProvider.interfaces()
        if (networkInterfaces.isEmpty()) {
            return emptyList();
        }

        List<Inet4Address> addressesToScan = newLinkedList()
        networkInterfaces.each {
            def address = it.broadcast
            int lastDot = address.lastIndexOf('.') + 1;
            def addressBase = address.substring(0, lastDot);
            def addressesNumber = address.substring(lastDot).toInteger()
            for (int i = 0; i < addressesNumber; i++) {
                addressesToScan.add((Inet4Address) Inet4Address.getByName(addressBase + (i + 1)));
            }
        }
        addressesToScan.collect {
            executor.submit(new Callable<ScanResult>() {
                @Override
                ScanResult call() throws Exception {
                    try {
                        Socket client = new Socket(it, port)
                        boolean reachable = client.isConnected()
                        client.close()
                        log().debug('Scanning address: {}, reachable: {}', it.hostAddress, reachable)
                        return new ScanResult(it, reachable);
                    } catch(NoRouteToHostException e){
                        log().debug('Scanning address: {}, reachable: false', it.hostAddress)
                        return new ScanResult(it, false);
                    } catch(SocketException e){
                        log().debug('Scanning address: {}, reachable: false', it.hostAddress)
                        return new ScanResult(it, false);
                    } catch(ConnectException e) {
                        log().debug('Scanning address: {}, reachable: false', it.hostAddress)
                        return new ScanResult(it, false);
                    } catch (SocketException e) {
                        if (e.message.contains('Permission denied')) {
                            log().debug("Cannot scan " + it + " - permission denied.")
                            return new ScanResult(it, false)
                        } else {
                            throw new RuntimeException(e);
                        }
                    }
                }
            })
        }.findAll{ try{it.get(5, SECONDS).reachable} catch(TimeoutException e){return null}}.findAll{ it != null }.collect{it.get().address()}.collect { device ->
            executor.submit(new Callable<Device>() {
                @Override
                Device call() throws Exception {
                    try {
                        log().debug("Probing for Device on " + device.hostAddress + ":" + port + " using username: " + username + " and password: " + password)
                        SshClient sshClient = new SshClient(device.hostAddress, port, username, password)
						sshClient.checkConnection()
						// Check with uname command if kernel version contains "poky-edison"
                        def cpuInfo = sshClient.command("uname -r");
                        if (!cpuInfo.empty && cpuInfo.get(0).contains("poky-edison")) {
                        	new Device(device, Device.DEVICE_INTEL_EDISON)
                        } else {
                        	new Device(device, Device.DEVICE_RASPBERRY_PI_2)
                        }
                    } catch (Exception ex) {
                        log().debug("Can't connect to the Raspberry Pi device: " + device.getHostAddress() + "\n" + ex.printStackTrace());
                        return null
                    }
                }
            })
        }.collect{try{it.get(5, SECONDS)}catch(TimeoutException ex){return null}}.findAll{it != null}
    }

    List<Device> detectDevices() {
        detectDevices(22)
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