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
package io.rhiot.component.kura.test;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.felix.connect.launch.ClasspathScanner;
import org.apache.felix.connect.launch.PojoServiceRegistry;
import org.apache.felix.connect.launch.PojoServiceRegistryFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

public final class PojosrRegistry {
    private static ServiceLoader<PojoServiceRegistryFactory> loader = ServiceLoader
            .load(PojoServiceRegistryFactory.class);
    private static PojoServiceRegistry registry;
    private static BundleContext bundleContext;
    private static PojosrRegistry instance = new PojosrRegistry();

    private PojosrRegistry() {
        init();
    }

    public static void init() {
        // Tries to initialize the registry
        if (registry == null) {
            Map config = new HashMap();
            try {
                // Exclude the org.eclipse.osgi bundle from being initialized
                String filter = "(!(" + Constants.BUNDLE_SYMBOLICNAME + "=org.eclipse.osgi*))";
                // Scan the classpath for bundles and add them to the registry
                config.put(PojoServiceRegistryFactory.BUNDLE_DESCRIPTORS,
                        new ClasspathScanner().scanForBundles(filter));
                registry = loader.iterator().next().newPojoServiceRegistry(config);
                bundleContext = registry.getBundleContext();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static PojosrRegistry getInstance() {
        return instance;
    }

    public BundleContext getBundleContext() {
        return instance.bundleContext;
    }

    public PojoServiceRegistry getRegistry() {
        return instance.registry;
    }
}
