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

import com.github.camellabs.iot.vertx.camel.CamelContextFactories;
import org.apache.activemq.broker.BrokerService;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static java.lang.System.setProperty;
import static org.springframework.util.SocketUtils.findAvailableTcpPort;

public class CamelIotGatewayTest extends Assert {

    static int mqttPort = findAvailableTcpPort();

    static {
        System.setProperty("camellabs.iot.gateway.heartbeat.mqtt", true + "");
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        BrokerService broker = new BrokerService();
        broker.setBrokerName(CamelIotGatewayTest.class.getName());
        broker.setPersistent(false);
        broker.addConnector("mqtt://localhost:" + mqttPort);
        broker.start();

        setProperty("camellabs.iot.gateway.heartbeat.mqtt.broker.url", "tcp://localhost:" + mqttPort);

        new VertxGateway().start();
    }

    // Tests

    @Test
    public void shouldReceiveHeartbeatMqttMessage() {
        ConsumerTemplate consumerTemplate = CamelContextFactories.camelContext().createConsumerTemplate();
        String heartbeat = consumerTemplate.receiveBody("paho:heartbeat?brokerUrl=tcp://localhost:" + mqttPort, String.class);
        assertNotNull(heartbeat);
    }

}