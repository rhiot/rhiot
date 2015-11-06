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

import io.vertx.core.eventbus.Message

/**
 * Base class for StreamConsumer, providing common access to logger, bootstrap and other commonly used collaborators.
 */
abstract class AbstractServiceStreamConsumer<T> extends AbstractStreamConsumer {

    protected final Class<T> serviceClass

    protected T service

    private ServiceBinding serviceBinding = new ServiceBinding()

    AbstractServiceStreamConsumer(String channel, Class<T> serviceInterface) {
        super(channel)
        this.service = service
        this.serviceClass = serviceInterface
    }

    @Override
    void start() {
        log().debug('Starting {} stream consumer.', getClass().simpleName)
        def serviceFromRegistry = bootstrap.beanRegistry().bean(serviceClass)
        if(!serviceFromRegistry.isPresent()) {
            throw new IllegalStateException("Can't find ${serviceClass.name} in a Rhiot Bootstrap bean registry.")
        }
        service = serviceFromRegistry.get()
    }

    @Override
    void consume(Message message) {
        serviceBinding.handleOperation(serviceClass, service, message)
    }

}