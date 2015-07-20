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
package com.github.camellabs.iot.gateway

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.core.Vertx
import org.reflections.Reflections

import static com.github.camellabs.iot.vertx.PropertyResolver.stringProperty
import static com.github.camellabs.iot.vertx.camel.CamelContextFactories.connect
import static io.vertx.groovy.core.Vertx.vertx
import static java.lang.Boolean.parseBoolean

class VertxGateway {

    final def vertx = vertx()

    static final def JSON = new ObjectMapper()

    VertxGateway() {
        connect(vertx.delegate.asType(Vertx.class))
        new Reflections('').getTypesAnnotatedWith(GatewayVerticle.class).each {
            String conditionProperty = it.getAnnotation(GatewayVerticle.class).conditionProperty()
            if(conditionProperty.isEmpty() || parseBoolean(stringProperty(conditionProperty))) {
                vertx.deployVerticle("groovy:${it.getName()}")
            }
        }
    }

}