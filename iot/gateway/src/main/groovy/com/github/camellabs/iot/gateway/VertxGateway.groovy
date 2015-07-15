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
import com.github.camellabs.iot.gateway.heartbeat.HeartbeatVerticle
import com.github.camellabs.iot.gateway.heartbeat.LoggingHeartbeatVerticle

import static io.vertx.groovy.core.Vertx.vertx

class VertxGateway {

    final def vertx = vertx()

    static final def JSON = new ObjectMapper()

    VertxGateway() {
        vertx.deployVerticle("groovy:${HeartbeatVerticle.class.getName()}")
        vertx.deployVerticle("groovy:${LoggingHeartbeatVerticle.class.getName()}")
    }

    public static void main(String[] args) {
        new VertxGateway()
    }

}