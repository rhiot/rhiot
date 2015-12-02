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

import org.apache.camel.component.kura.KuraRouter;
import org.apache.felix.connect.launch.PojoServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestKuraServer {

    private static Logger log = LoggerFactory.getLogger(TestKuraServer.class);

    private PojoServiceRegistry registry;

    public <T extends KuraRouter> T start(Class<? extends KuraRouter> kuraRouter) {
        registry = PojosrRegistry.getInstance().getRegistry();
        T router = null;
        try {
            log.debug("Starting router for class {}...", kuraRouter.getName());
            router = (T) kuraRouter.newInstance();
            router.start(registry.getBundleContext());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return router;
    }

}
