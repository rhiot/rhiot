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

import org.apache.activemq.broker.BrokerService;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static java.lang.System.setProperty;
import static org.springframework.util.SocketUtils.findAvailableTcpPort;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CamelIotGatewayTest.class)
public class CamelIotGatewayTest extends Assert {

    static int mqttPort = findAvailableTcpPort();

    @Autowired
    ConsumerTemplate consumerTemplate;

    static {
        System.setProperty("camellabs.iot.gateway.heartbeat.mqtt", true + "");
    }

    // TODO https://github.com/camel-labs/camel-labs/issues/66 (Camel Spring Boot should start embedded MQTT router for tests)
    @Bean(initMethod = "start", destroyMethod = "stop")
    BrokerService broker() throws Exception {
        BrokerService broker = new BrokerService();
        broker.setBrokerName(getClass().getName());
        broker.setPersistent(false);
        broker.addConnector("mqtt://localhost:" + mqttPort);
        return broker;
    }

    @BeforeClass
    public static void beforeClass() {
        setProperty("camellabs.iot.gateway.heartbeat.mqtt.broker.url", "tcp://localhost:" + mqttPort);
    }

    // Tests

    @Test
    public void shouldReceiveHeartbeatMqttMessage() {
        String heartbeat = consumerTemplate.receiveBody("paho:heartbeat?brokerUrl=tcp://localhost:" + mqttPort, String.class);
        assertNotNull(heartbeat);
    }

}