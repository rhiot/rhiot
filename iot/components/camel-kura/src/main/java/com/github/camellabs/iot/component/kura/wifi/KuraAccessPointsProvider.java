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
package com.github.camellabs.iot.component.kura.wifi;

import org.eclipse.kura.KuraException;
import org.eclipse.kura.linux.net.NetworkServiceImpl;
import org.eclipse.kura.net.NetworkService;
import org.eclipse.kura.net.wifi.WifiAccessPoint;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.reflect.FieldUtils.writeField;

/**
 * Access point provider using the Kura NetworkService to scan the WiFi networks.
 */
public class KuraAccessPointsProvider implements AccessPointsProvider {

    private final NetworkService networkService = new NetworkServiceImpl();

    public KuraAccessPointsProvider() {
        initializeNetworkService();
    }

    @Override
    public List<WifiAccessPoint> accessPoints(String forInterface) {
        try {
            if (forInterface.equals("*")) {
                return networkService.getAllWifiAccessPoints();
            } else {
                return networkService.getWifiAccessPoints(forInterface);
            }
        } catch (KuraException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeNetworkService() {
        try {
            writeField(networkService, "m_addedModems", new ArrayList(), true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
