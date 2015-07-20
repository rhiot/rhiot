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

import io.vertx.lang.groovy.GroovyVerticle
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition

import static com.github.camellabs.iot.vertx.camel.CamelContextFactories.camelContext

/**
 * Base class for the verticles that are supposed to access CamelContext and bridge Camel with the event bus.
 */
class GroovyCamelVerticle extends GroovyVerticle {

    protected def camelContext = camelContext()

    def fromEventBus(String address, Closure<RouteDefinition> routeCallback) {
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            void configure() throws Exception {
                routeCallback(from("vertx:${address}"))
            }
        })
    }

}