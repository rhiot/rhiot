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
package io.rhiot.component.kura.utils;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Set;

import org.apache.camel.spi.Registry;
import org.slf4j.Logger;

public final class KuraServiceFactory<T> {

    // Logger

    private static final Logger LOG = getLogger(KuraServiceFactory.class);

    // Constructors

    private KuraServiceFactory() {
    }

    // Operations

    public static <T> T retrieveService(Class<T> clazz, Registry registry) {
        if(registry == null) {
            throw new IllegalArgumentException("Registry cannot be null.");
        }

        Set<T> servicesFromRegistry = registry.findByType(clazz);
        if (servicesFromRegistry.size() == 1) {
            T service = servicesFromRegistry.iterator().next();
            LOG.info("Found Kura " + clazz.getCanonicalName() + " in the registry. Kura component will use that instance.");
            return service;
        } else if (servicesFromRegistry.size() > 1) {
            throw new IllegalStateException("Too many " + clazz.getCanonicalName() + " services found in a registry: "
                    + servicesFromRegistry.size());
        } else {
            throw new IllegalArgumentException(
                    "No " + clazz.getCanonicalName() + " service instance found in a registry.");
        }
    }

}