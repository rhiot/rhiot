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
package io.rhiot.steroids.camel

import io.rhiot.bootstrap.AbstractBootInitializer

import io.rhiot.bootstrap.MapBeanRegistry
import io.rhiot.datastream.engine.Bootstrap
import io.rhiot.utils.Reflections
import io.vertx.core.Vertx;
import org.apache.camel.CamelContext;
import org.apache.camel.component.vertx.VertxComponent
import org.apache.camel.impl.CompositeRegistry
import org.apache.camel.spring.spi.ApplicationContextRegistry

class CamelBootInitializer extends AbstractBootInitializer {

    private static CamelContext camelContext

    private static Vertx vertx

    @Override
    public void start() {
        camelContext = Bootstrap.applicationContext.getBean(CamelContext.class)
        Reflections.writeField(camelContext, 'registry', new CompositeRegistry([new ApplicationContextRegistry(Bootstrap.applicationContext), new BootstrapRegistry(bootstrap.beanRegistry())]))
        camelContext.streamCaching = true
        vertx = Vertx.vertx()
        def vertxComponent = new VertxComponent(vertx: vertx)
        camelContext.addComponent('event-bus', vertxComponent)

        bootstrap.beanRegistry().register(camelContext)
    }

    @Override
    void stop() {
        vertx.close()
        camelContext.stop()
        if(bootstrap.beanRegistry().bean(CamelContext.class).isPresent()) {
            def contextKey = bootstrap.beanRegistry().asType(MapBeanRegistry.class).registry.entrySet().find{ it.value instanceof CamelContext }.key
            bootstrap.beanRegistry().asType(MapBeanRegistry.class).registry.remove(contextKey)
        }
    }

    @Override
    public int order() {
        return 1100;
    }

    static CamelContext camelContext() {
        camelContext
    }

    static Vertx vertx() {
        vertx
    }

    static String eventBus(String channel) {
        "event-bus:${channel}"
    }

}