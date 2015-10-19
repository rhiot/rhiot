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

import org.apache.camel.spi.Registry;
import org.eclipse.kura.KuraException;
import org.eclipse.kura.linux.net.NetworkServiceImpl;
import org.eclipse.kura.net.NetworkService;
import org.eclipse.kura.net.wifi.WifiAccessPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static io.rhiot.utils.Reflections.writeField;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Access point provider using the Kura NetworkService to scan the WiFi networks.
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
        Set<NetworkService> servicesFromRegistry = registry.findByType(NetworkService.class);
        if(servicesFromRegistry.size() != 1) {
            LOG.info("Found {} Kura NetworkService instances in the registry. Creating and using custom instance then.",
                    servicesFromRegistry.size());
            NetworkService networkService = new NetworkServiceImpl();
            initializeNetworkService(networkService);
            return networkService;
        }
        LOG.info("Found Kura NetworkService in the registry. Kura component will use that instance.");
        return servicesFromRegistry.iterator().next();
    }

    protected void initializeNetworkService(NetworkService networkService) {
        writeField(networkService, "m_addedModems", new ArrayList());
    }

}
