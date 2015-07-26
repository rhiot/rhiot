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
package com.github.camellabs.iot.vertx.camel

import io.vertx.core.Vertx
import org.apache.camel.CamelContext
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.component.vertx.VertxComponent
import org.slf4j.Logger

import static org.slf4j.LoggerFactory.getLogger

/**
 * Static singleton access point for the CamelContext instance shared between the verticles in the same JVM.
 */
final class CamelContextFactories {

    private static final Logger LOG = getLogger(CamelContextFactories.class)

    private static CamelContext camelContext

    private CamelContextFactories() {
    }

    static CamelContextFactory resolveCamelContextFactory() {
        new DefaultCamelContextFactory()
    }

    synchronized static connect(Vertx vertx) {
        LOG.debug('Connecting to the Vert.x instance {}.', vertx)
        if(camelContext().hasComponent('vertx') != null) {
            camelContext().removeComponent('vertx')
        }
        def vertxComponent = new VertxComponent(vertx: vertx)
        camelContext().addComponent('vertx', vertxComponent)
    }

    /**
     * Stop and dispose the singleton CamelContext. Next call to {@code camelContext()} method will re-initialize the
     * context.
     */
    synchronized static void closeCamelContext() {
        if(camelContext != null) {
            camelContext.stop()
            camelContext = null
        }
    }

    /**
     * Global access point for accessing singleton CamelContext instance.
     *
     * @return Started singleton CamelContext instance.
     */
    synchronized static CamelContext camelContext() {
        if(camelContext == null) {
            camelContext = resolveCamelContextFactory().createCamelContext()
            camelContext.start()
        }
        camelContext
    }

    static MockEndpoint mockEndpoint(String uri) {
        camelContext().getEndpoint(uri, MockEndpoint.class)
    }

}
