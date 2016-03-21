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

import static java.net.NetworkInterface.getNetworkInterfaces

class JavaNetInterfaceProvider implements InterfacesProvider, WithLogger {

    @Override
    List<NetworkInterface> interfaces() {
        log().debug("Found network interfaces : " + getNetworkInterfaces().findAll())

        getNetworkInterfaces().findAll { def iface = it.displayName
            iface.startsWith("wlan") || iface.startsWith("eth") || iface.startsWith("en") || iface.startsWith("docker") || iface.startsWith("wlp")}.
                collect { java.net.NetworkInterface it ->
                    def ipv4Address = it.interfaceAddresses.find{ it.getAddress().getHostAddress().length() < 15 }
                    log().debug("Checking ipv4Address " + ipv4Address)
                    def broadcast = ipv4Address.broadcast.hostName
                    new NetworkInterface(ipv4Address: ipv4Address, broadcast: broadcast)
                }
    }

}
