/**
 * Licensed to the Camel Labs under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.camellabs.iot.gateway.mock

import com.github.camellabs.iot.gateway.VertxGateway
import org.apache.activemq.broker.BrokerService;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test

import static com.github.camellabs.iot.utils.Properties.booleanProperty;
import static com.github.camellabs.iot.vertx.camel.CamelContextFactories.camelContext
import static com.github.camellabs.iot.vertx.camel.CamelContextFactories.closeCamelContext;
import static java.lang.System.setProperty;
import static org.springframework.util.SocketUtils.findAvailableTcpPort;

class MockSensorTest extends Assert {

    // Collaborators fixtures

    static int mqttPort = findAvailableTcpPort();

    @BeforeClass
    static void beforeClass() {
        BrokerService broker = new BrokerService();
        broker.setBrokerName(MockSensorTest.class.getName());
        broker.setPersistent(false);
        broker.addConnector("mqtt://localhost:${mqttPort}");
        broker.start();

        camelContext().addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("paho:mock?brokerUrl=tcp://localhost:" + mqttPort).
                        to("mock:test");
            }
        });

        booleanProperty('camellabs_iot_gateway_mock_sensor', true)
        booleanProperty('camellabs_iot_gateway_mock_sensor_consumer', true)
        setProperty("camellabs_iot_gateway_mock_sensor_consumer_mqtt_broker_url", "tcp://localhost:${mqttPort}")

        new VertxGateway().start();
    }

    @AfterClass
    public static void afterClass() {
        closeCamelContext()

        booleanProperty("camellabs_iot_gateway_mock_sensor", false);
        booleanProperty("camellabs_iot_gateway_mock_sensor_consumer", false);
    }

    // Tests

    @Test
    public void shouldSendMockEventsToTheMqttServer() throws InterruptedException {
        MockEndpoint mockEndpoint = camelContext().getEndpoint("mock:test", MockEndpoint.class);
        mockEndpoint.setMinimumExpectedMessageCount(1000);
        mockEndpoint.assertIsSatisfied();
    }

}