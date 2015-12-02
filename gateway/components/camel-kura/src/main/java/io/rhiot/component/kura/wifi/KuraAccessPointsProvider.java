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
package io.rhiot.component.kura.wifi;

import static org.slf4j.LoggerFactory.getLogger;

import io.rhiot.component.kura.utils.KuraServiceFactory;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.spi.Registry;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.eclipse.kura.KuraException;
import org.eclipse.kura.net.NetworkService;
import org.eclipse.kura.net.wifi.WifiAccessPoint;
import org.slf4j.Logger;

/**
 * Access point provider using the Kura NetworkService to scan the WiFi
 * networks.
 */
public class KuraAccessPointsProvider implements AccessPointsProvider {

    // Logger

    private static final Logger LOG = getLogger(KuraAccessPointsProvider.class);

    // Collaborators

    private final NetworkService networkService;

    // Constructors

    public KuraAccessPointsProvider(Registry registry) {
        networkService = resolveNetworkService(registry);
    }

    // Overridden

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

    // Helpers

    private NetworkService resolveNetworkService(Registry registry) {
        return KuraServiceFactory.retrieveService(NetworkService.class, registry);
    }

    protected void initializeNetworkService(NetworkService networkService) {
        writeField(networkService, "m_addedModems", new ArrayList());
    }

    private static void writeField(Object object, String field, Object value) {
        try {
            FieldUtils.writeField(object, field, value, true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
