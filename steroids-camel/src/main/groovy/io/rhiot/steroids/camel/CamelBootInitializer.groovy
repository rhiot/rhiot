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

import io.rhiot.steroids.bootstrap.BootInitializer
import io.rhiot.steroids.bootstrap.Bootstrap
import io.rhiot.steroids.bootstrap.BootstrapAware
import io.vertx.core.Vertx;
import org.apache.camel.CamelContext;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.vertx.VertxComponent
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.SimpleRegistry;

import static io.rhiot.steroids.Steroids.beans
import static org.slf4j.LoggerFactory.getLogger;

class CamelBootInitializer implements BootInitializer, BootstrapAware {

    private static final def LOG = getLogger(CamelBootInitializer.class)

    private static CamelContext camelContext

    private static Vertx vertx

    private static SimpleRegistry registry = new SimpleRegistry()

    Bootstrap bootstrap

    @Override
    public void start() {
        camelContext = new DefaultCamelContext(registry)
        camelContext.streamCaching = true
        vertx = Vertx.vertx()
        def vertxComponent = new VertxComponent(vertx: vertx)
        camelContext.addComponent('event-bus', vertxComponent)

        beans(RoutesBuilder.class, Route.class).each { if(it instanceof BootstrapAware) it.bootstrap(bootstrap) }.each { camelContext.addRoutes(it) }

        camelContext.start()
    }

    @Override
    void stop() {
        vertx.close()
        camelContext.stop()
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

    static SimpleRegistry registry() {
        registry
    }

    static String eventBus(String channel) {
        "event-bus:${channel}"
    }

    @Override
    void bootstrap(Bootstrap bootstrap) {
        this.bootstrap = bootstrap
    }

}