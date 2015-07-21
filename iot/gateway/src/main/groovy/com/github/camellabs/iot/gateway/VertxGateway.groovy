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
import org.reflections.util.ConfigurationBuilder

import static com.github.camellabs.iot.vertx.PropertyResolver.stringProperty
import static com.github.camellabs.iot.vertx.camel.CamelContextFactories.connect
import static io.vertx.groovy.core.Vertx.vertx
import static java.lang.Boolean.parseBoolean
import static org.reflections.util.ClasspathHelper.forJavaClassPath

/**
 * IoT gateway boostrap. Starts Vert.x event bus, detects verticles and starts these.
 */
class VertxGateway {

    final def vertx = vertx()

    final def classpath = new Reflections(new ConfigurationBuilder().setUrls(forJavaClassPath()))

    static final def JSON = new ObjectMapper()

    VertxGateway start() {
        connect(vertx.delegate.asType(Vertx.class))
        classpath.getTypesAnnotatedWith(GatewayVerticle.class).each {
            String conditionProperty = it.getAnnotation(GatewayVerticle.class).conditionProperty()
            if(conditionProperty.isEmpty() || parseBoolean(stringProperty(conditionProperty))) {
                vertx.deployVerticle("groovy:${it.getName()}")
            }
        }
        this
    }

}