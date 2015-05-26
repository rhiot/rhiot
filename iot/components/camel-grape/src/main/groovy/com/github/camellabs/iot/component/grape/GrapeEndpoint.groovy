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
package com.github.camellabs.iot.component.grape

import org.apache.camel.CamelContext
import org.apache.camel.Component
import org.apache.camel.Consumer
import org.apache.camel.Processor
import org.apache.camel.Producer
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultEndpoint

import static groovy.grape.Grape.grab

class GrapeEndpoint extends DefaultEndpoint {

    private final String defaultCoordinates

    GrapeEndpoint(String endpointUri, String defaultCoordinates, GrapeComponent component) {
        super(endpointUri, component)
        this.defaultCoordinates = defaultCoordinates
    }

    static def loadPatches(CamelContext camelContext) {
            def classLoader = camelContext.applicationContextClassLoader
            def patchesRepository = camelContext.getComponent('grape', GrapeComponent.class).patchesRepository
            patchesRepository.listPatches().each {
            def coordinates = it.split('/')
            grab(classLoader: classLoader, group: coordinates[0], module: coordinates[1], version: coordinates[2])
        }
    }

    @Override
    Producer createProducer() {
        new GrapeProducer(this)
    }

    @Override
    Consumer createConsumer(Processor processor) {
        throw new UnsupportedOperationException('Grape component supports only the producer side of the route.')
    }

    @Override
    boolean isSingleton() {
        true
    }

    String getDefaultCoordinates() {
        defaultCoordinates
    }

    @Override
    GrapeComponent getComponent() {
        super.getComponent() as GrapeComponent
    }

}