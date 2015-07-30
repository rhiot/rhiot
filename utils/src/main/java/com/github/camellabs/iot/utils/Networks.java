/**
 * Licensed to the Camel Labs under one or more
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
package com.github.camellabs.iot.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.List;
import java.util.Optional;

import static java.net.NetworkInterface.getNetworkInterfaces;
import static java.util.Collections.list;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;

public final class Networks {

    private Networks() {
    }

    public static Optional<String> localNetworkIp() {
        try {
            List<NetworkInterface> interfaces = list(getNetworkInterfaces()).parallelStream().
                    filter(iface -> iface.getName().startsWith("wlan") || iface.getName().startsWith("eth")).
                    collect(toList());
            if (interfaces.isEmpty()) {
                return empty();
            }
            if (interfaces.size() > 1) {
                throw new IllegalStateException("Expected single or zero interfaces, found: " + interfaces.size());
            }
            List<InetAddress> addresses = list(interfaces.get(0).getInetAddresses()).
                    parallelStream().filter(address -> address instanceof Inet4Address).collect(toList());
            return Optional.of(addresses.get(0).getHostAddress());
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

}
