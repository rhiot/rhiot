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
package io.rhiot.gateway

import com.fasterxml.jackson.databind.ObjectMapper
import io.rhiot.steroids.bootstrap.Bootstrap
import io.rhiot.steroids.camel.CamelBootInitializer
import io.vertx.core.Vertx
import org.jolokia.jvmagent.JvmAgent
import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder
import org.slf4j.Logger

import static io.rhiot.steroids.camel.CamelBootInitializer.vertx
import static io.rhiot.utils.Properties.stringProperty
import static java.lang.Boolean.parseBoolean
import static org.reflections.util.ClasspathHelper.forJavaClassPath
import static org.slf4j.LoggerFactory.getLogger

/**
 * IoT gateway boostrap. Starts Vert.x event bus, detects verticles and starts these.
 */
class Gateway extends Bootstrap {

    private static final LOG = getLogger(Gateway.class)

    final def classpath = new Reflections(new ConfigurationBuilder().setUrls(forJavaClassPath()))

    static final def JSON = new ObjectMapper()

    // Life-cycle

    Gateway start() {
        def gateway = super.start() as Gateway
        classpath.getTypesAnnotatedWith(GatewayVerticle.class).each {
            LOG.debug('Classpath scanner found gateway verticle {}.', it.name)
            String conditionProperty = it.getAnnotation(GatewayVerticle.class).conditionProperty()
            if(conditionProperty.isEmpty() || parseBoolean(stringProperty(conditionProperty))) {
                LOG.debug('Loading gateway verticle {}.', it.name)
                vertx().deployVerticle("groovy:${it.name}")
            }
        }
        gateway
    }

    @Override
    Gateway stop() {
        super.stop() as Gateway
    }

    // Main method handler

    public static void main(String[] args) {
        JvmAgent.agentmain('host=0.0.0.0')
        new Gateway().start()
    }

}