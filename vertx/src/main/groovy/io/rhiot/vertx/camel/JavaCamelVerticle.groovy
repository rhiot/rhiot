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
package io.rhiot.vertx.camel

import io.rhiot.steroids.camel.CamelBootInitializer
import io.vertx.core.AbstractVerticle
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition

/**
 * Base Java class for the verticles that are supposed to access CamelContext and bridge Camel with the event bus.
 */
class JavaCamelVerticle extends AbstractVerticle {

    def fromEventBus(String address, RouteDefinitionCallback routeDefinitionCallback) {
        CamelBootInitializer.camelContext().addRoutes(new RouteBuilder() {
            @Override
            void configure() {
                routeDefinitionCallback.route(from("event-bus:${address}"))
            }
        })
    }

}

interface RouteDefinitionCallback {

    void route(RouteDefinition route)

}