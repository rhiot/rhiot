/**
 * Licensed to the Rhiot under one or more
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
package io.rhiot.gateway.mock

import io.rhiot.gateway.Gateway
import io.rhiot.steroids.camel.CamelBootInitializer
import org.apache.activemq.broker.BrokerService;
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test

import static io.rhiot.utils.Properties.setBooleanProperty;
import static java.lang.System.setProperty;
import static org.springframework.util.SocketUtils.findAvailableTcpPort;

class MockSensorTest extends Assert {

    // Collaborators fixtures

    static int mqttPort = findAvailableTcpPort();

    static def gateway = new Gateway()

    @BeforeClass
    static void beforeClass() {
        setBooleanProperty('camellabs_iot_gateway_mock_sensor', true)
        setBooleanProperty('camellabs_iot_gateway_mock_sensor_consumer', true)
        setProperty('camellabs_iot_gateway_mock_sensor_consumer_mqtt_broker_url', "tcp://localhost:${mqttPort}")

        def broker = new BrokerService()
        broker.setBrokerName(MockSensorTest.class.getName());
        broker.setPersistent(false);
        broker.addConnector("mqtt://localhost:${mqttPort}");
        broker.start()

        gateway.start()

        CamelBootInitializer.camelContext().addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("paho:mock?brokerUrl=tcp://localhost:" + mqttPort).
                        to("mock:test");
            }
        })
    }

    @AfterClass
    static void afterClass() {
        setBooleanProperty('camellabs_iot_gateway_mock_sensor', false)
        setBooleanProperty('camellabs_iot_gateway_mock_sensor_consumer', false)
        gateway.stop()
    }

    // Tests

    @Test
    void shouldSendMockEventsToTheMqttServer() {
        def mock = CamelBootInitializer.camelContext().getEndpoint('mock:test', MockEndpoint.class)
        mock.setMinimumExpectedMessageCount(1000)
        mock.assertIsSatisfied()
    }

}