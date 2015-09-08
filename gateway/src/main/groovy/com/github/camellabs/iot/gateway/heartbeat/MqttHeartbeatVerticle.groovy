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
package com.github.camellabs.iot.gateway.heartbeat

import com.github.camellabs.iot.gateway.GatewayVerticle
import com.github.camellabs.iot.vertx.camel.GroovyCamelVerticle
import org.apache.camel.model.RouteDefinition

import static io.rhiot.utils.Properties.stringProperty
import static java.lang.System.currentTimeMillis
import static java.net.InetAddress.getLocalHost

@GatewayVerticle(conditionProperty = 'camellabs.iot.gateway.heartbeat.mqtt')
class MqttHeartbeatVerticle extends GroovyCamelVerticle {

    def topic = stringProperty('camellabs.iot.gateway.heartbeat.mqtt.topic', 'heartbeat')

    def brokerUrl = stringProperty('camellabs.iot.gateway.heartbeat.mqtt.broker.url')

    @Override
    void start() {
        super.start()
        fromEventBus('heartbeat') { RouteDefinition route ->
            route.transform().simple(generateHeartBeatMessage()).
                    to("paho:${topic}?brokerUrl=${brokerUrl}")
        }
    }

    // Private helpers

    private String generateHeartBeatMessage() {
        try {
            return getLocalHost().getHostName() + ":" + currentTimeMillis();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

}
