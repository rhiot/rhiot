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
package io.rhiot.datastream.engine

import io.rhiot.bootstrap.Bootstrap
import io.rhiot.steroids.bootstrap.BootstrapAware
import io.rhiot.utils.WithLogger
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus

import java.lang.reflect.Method

abstract class AbstractServiceStreamSource<T> implements StreamSource, BootstrapAware, WithLogger {

    protected final Class<T> serviceClass

    protected T service

    protected EventBus eventBus

    private ServiceBinding serviceBinding = new ServiceBinding()

    protected Bootstrap bootstrap

    AbstractServiceStreamSource(Class<T> serviceClass) {
        this.serviceClass = serviceClass
    }

    abstract void registerOperationEndpoint(Method operation)

    @Override
    void start() {
        log().debug('Starting {} stream source.', getClass().simpleName)
        def serviceFromRegistry = bootstrap.beanRegistry().bean(serviceClass)
        if(!serviceFromRegistry.isPresent()) {
            throw new IllegalStateException("Can't find ${serviceClass.name} in a Rhiot Bootstrap bean registry.")
        }
        service = serviceFromRegistry.get()

        eventBus = bootstrap.beanRegistry().bean(Vertx.class).get().eventBus()
    }

    @Override
    void stop() {
    }

    @Override
    EventBus eventBus() {
        eventBus
    }

    @Override
    void bootstrap(Bootstrap bootstrap) {
        this.bootstrap = bootstrap
    }

}
