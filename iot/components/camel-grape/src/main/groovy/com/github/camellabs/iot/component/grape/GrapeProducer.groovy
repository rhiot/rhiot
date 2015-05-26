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

import groovy.grape.Grape
import org.apache.camel.Exchange
import org.apache.camel.impl.DefaultProducer

import static com.github.camellabs.iot.component.grape.GrapeCommand.clearPatches
import static com.github.camellabs.iot.component.grape.GrapeCommand.grab
import static com.github.camellabs.iot.component.grape.GrapeCommand.listPatches
import static GrapeConstants.GRAPE_COMMAND
import static com.github.camellabs.iot.component.grape.MavenCoordinates.parseMavenCoordinates

class GrapeProducer extends DefaultProducer {

    GrapeProducer(GrapeEndpoint endpoint) {
        super(endpoint)
    }

    @Override
    void process(Exchange exchange) {
        def command = exchange.in.getHeader(GRAPE_COMMAND, grab, GrapeCommand.class)
        switch(command) {
            case grab:
                def classLoader = exchange.context.applicationContextClassLoader
                def rawCoordinates = exchange.in.getBody(String.class)
                try {
                    def coordinates = parseMavenCoordinates(rawCoordinates)
                    Grape.grab(classLoader: classLoader, group: coordinates.groupId, module: coordinates.artifactId, version: coordinates.version)
                    endpoint.component.patchesRepository.install(rawCoordinates)
                } catch (IllegalArgumentException ex) {
                    def coordinates = parseMavenCoordinates(getEndpoint().defaultCoordinates)
                    Grape.grab(classLoader: classLoader, group: coordinates.groupId, module: coordinates.artifactId, version: coordinates.version)
                    endpoint.component.patchesRepository.install(getEndpoint().defaultCoordinates)
                }
                break

            case listPatches:
                def patches = endpoint.component.patchesRepository.listPatches()
                exchange.getIn().setBody(patches)
                break

            case clearPatches:
                endpoint.component.patchesRepository.clear()
                break
        }
    }

    @Override
    GrapeEndpoint getEndpoint() {
        super.getEndpoint() as GrapeEndpoint
    }

}
