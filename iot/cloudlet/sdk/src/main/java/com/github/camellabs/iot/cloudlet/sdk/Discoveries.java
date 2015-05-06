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
package com.github.camellabs.iot.cloudlet.sdk;

import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Utility class providing common tools for the discovery of the cloudlets for the Java clients. Cloudlet Java client
 * is usually deployed on the field device (like Gateway) or into the cloudlet willing to connect to the another
 * cloudlet. Discovery is a process of finding the right connection URL to the target cloudlet.
 */
public final class Discoveries {

    private static final Logger LOG = getLogger(Discoveries.class);

    private Discoveries() {
    }

    public static String discoverServiceUrl(String serviceName, int defaultPort, HealthCheck healthCheck) {
        LOG.debug("Starting {} service discovery process.", serviceName);

        String serviceUrl = "http://localhost:" + defaultPort;
        try {
            healthCheck.check(serviceUrl);
        } catch (Exception e) {
            String message = String.format("Can't connect to the %s service %s . " +
                            "Are you sure there is a %s service instance running there? " +
                            "%s has been chosen as a default connection URL for %s service.",
                    serviceName, serviceUrl, serviceName, serviceUrl, serviceName);
            LOG.debug(message);
            throw new ServiceDiscoveryException(message, e);
        }
        return serviceUrl;
    }

}
