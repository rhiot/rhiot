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
package io.rhiot.utils

import java.util.concurrent.atomic.AtomicInteger

import static io.rhiot.utils.Properties.intProperty
import static java.net.NetworkInterface.getNetworkInterfaces;
import static java.util.Optional.empty
import static org.slf4j.LoggerFactory.getLogger;

/**
 * IP networking related utilities.
 */
final class Networks {

    /**
     * The minimum server currentMinPort number for IPv4.
     * Set at 1100 to avoid returning privileged currentMinPort numbers.
     */
    static final def MIN_PORT_NUMBER = 1100

    /**
     * The maximum server currentMinPort number for IPv4.
     */
    static final def MAX_PORT_NUMBER = 65535

    // Logger

    private static final def LOG = getLogger(Networks.class)

    // Constructors

    private Networks() {
    }

    // Utilities API

    /**
     * Returns IPv4 address of the current machine in the local network.
     *
     * @return String representing IPv4 address of the current machine in the local network or empty option is no
     * interface or address can be found.
     */
    static Optional<String> currentLocalNetworkIp() {
        List<NetworkInterface> interfacesWithV4Address = getNetworkInterfaces().findAll {
            !it.getInetAddresses().findAll{address -> address instanceof Inet4Address}.isEmpty()
        }.asImmutable()

        // Prefer standard interfaces
        def interfaces = interfacesWithV4Address.findAll {
            iface -> iface.name.startsWith('wlan') || iface.name.startsWith('eth')
        }
        if (interfaces.isEmpty()) {
            if(interfacesWithV4Address.isEmpty()) {
                return empty()
            } else {
                interfaces = interfacesWithV4Address
            }
        }

        List<InetAddress> addresses = interfaces.first().getInetAddresses().findAll{address -> address instanceof Inet4Address}
        return Optional.of(addresses.first().getHostAddress());
    }

    static boolean isReachable(String host, int timeout) {
        try {
            InetAddress.getByName(host).isReachable(timeout)
        } catch (UnknownHostException e) {
            LOG.debug('Cannot find host {}. Returning false.', host)
            LOG.trace('Due to the: ', e)
            false
        }
    }

    static boolean isReachable(String host) {
        isReachable(host, 2000)
    }

    static String serviceHost(String service) {
        def host = Properties.stringProperty("${service.toUpperCase()}_SERVICE_HOST")
        if(host != null) {
            return host
        }
        LOG.debug('Trying to connect to the service host {}.', service)
        if (isReachable(service)) {
            LOG.debug('Successfully connected to the service host {}.', service)
            return service
        } else {
            LOG.debug('Cannot connect to the service host {}.', service)
        }
        'localhost'
    }

    static int servicePort(String service, Integer defaultPort) {
        intProperty("${service.toUpperCase()}_SERVICE_PORT") ?: defaultPort
    }

    /**
     * We'll hold open the lowest port in this process
     * so parallel processes won't use the same block
     * of ports.   They'll go up to the next block.
     */
    private static final ServerSocket LOCK;

    /**
     * Incremented to the next lowest available port when getNextAvailable() is called.
     */
    private static AtomicInteger currentMinPort = new AtomicInteger(MIN_PORT_NUMBER);

    static {
        int port = MIN_PORT_NUMBER;
        ServerSocket ss = null;

        while (ss == null) {
            try {
                ss = new ServerSocket(port);
            } catch (Exception e) {
                ss = null;
                port += 200;
            }
        }
        LOCK = ss;
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    LOCK.close();
                } catch (Exception ex) {
                    //ignore
                }
            }
        });
        currentMinPort.set(port + 1);
    }

    /**
     * Gets the next available port starting at the lowest number. This is the preferred
     * method to use. The port return is immediately marked in use and doesn't rely on the caller actually opening
     * the port.
     *
     * @throws IllegalArgumentException is thrown if the port number is out of range
     * @throws NoSuchElementException if there are no ports available
     * @return the available port
     */
    public static synchronized int findAvailableTcpPort() {
        int next = findAvailableTcpPort(currentMinPort.get());
        currentMinPort.set(next + 1);
        return next;
    }

    /**
     * Gets the next available port starting at a given from port.
     *
     * @param fromPort the from port to scan for availability
     * @throws IllegalArgumentException is thrown if the port number is out of range
     * @throws NoSuchElementException if there are no ports available
     * @return the available port
     */
    public static synchronized int findAvailableTcpPort(int fromPort) {
        if (fromPort < currentMinPort.get() || fromPort > MAX_PORT_NUMBER) {
            throw new IllegalArgumentException("From port number not in valid range: " + fromPort);
        }

        for (int i = fromPort; i <= MAX_PORT_NUMBER; i++) {
            if (available(i)) {
                LOG.info("getNextAvailable({}) -> {}", fromPort, i);
                return i;
            }
        }

        throw new NoSuchElementException("Could not find an available port above " + fromPort);
    }

    /**
     * Checks to see if a specific port is available on the specified host.
     *
     * @param port the port number to check for availability
     * @return <tt>true</tt> if the port is available, or <tt>false</tt> if not
     * @throws IllegalArgumentException is thrown if the port number is out of range
     */
    public static boolean available(String host, int port) throws IllegalArgumentException {
        
        if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
            throw new IllegalArgumentException("Invalid port");
        }
        
        def available
        if (isReachable(host)) {
            def socket
            try {
                socket = new Socket(host, port)
                available = socket.isConnected()
            } catch (ConnectException e) {
                //noop, port is unavailable
            } finally {
                if (socket != null) {
                    socket.close()
                }
            }
        }
        return available
    }

    /**
     * Checks to see if a specific port is available.
     *
     * @param port the port number to check for availability
     * @return <tt>true</tt> if the port is available, or <tt>false</tt> if not
     * @throws IllegalArgumentException is thrown if the port number is out of range
     */
    public static boolean available(int port) throws IllegalArgumentException {
        if (port < currentMinPort.get() || port > MAX_PORT_NUMBER) {
            throw new IllegalArgumentException("Invalid start currentMinPort: " + port);
        }

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            // Do nothing
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }

        return false;
    }

}
