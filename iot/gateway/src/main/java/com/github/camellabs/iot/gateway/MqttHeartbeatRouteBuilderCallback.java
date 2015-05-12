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
package com.github.camellabs.iot.gateway;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;

import static com.github.camellabs.iot.gateway.CamelIotGatewayConstants.HEARTBEAT_ENDPOINT;
import static java.lang.System.currentTimeMillis;
import static java.net.InetAddress.getLocalHost;

@Component
@ConditionalOnProperty(value = "camellabs.iot.gateway.heartbeat.mqtt", havingValue = "true")
public class MqttHeartbeatRouteBuilderCallback implements RouteBuilderCallback {

    @Override
    public void beforeRoutesDefinition(RouteBuilder routeBuilder) {
        routeBuilder.
                interceptFrom(HEARTBEAT_ENDPOINT).
                transform().simple(generateHeartBeatMessage()).
                to("paho:heartbeat?brokerUrl={{camellabs.iot.gateway.heartbeat.mqtt.broker.url}}");
    }

    private String generateHeartBeatMessage() {
        try {
            return getLocalHost().getHostName() + ":" + currentTimeMillis();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

}