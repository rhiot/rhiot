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
package com.github.camellabs.iot.gateway;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Camel route reading current position data from the BU353 GPS device.
 */
@Component
@ConditionalOnProperty(value = "camellabs_iot_gateway_mock_sensor_consumer", havingValue = "true")
public class MockSensorConsumerRoutes extends RouteBuilder {

    @Override
    public void configure() throws Exception {
            from("seda:mockSensor?concurrentConsumers={{camellabs_iot_gateway_mock_sensor_consumer_number:15}}").
                    routeId("mockSensorConsumer").
                    to("{{camellabs_iot_gateway_mock_sensor_consumer_target:paho:mock?qos={{camellabs_iot_gateway_mock_sensor_consumer_mqtt_qos:0}}&brokerUrl={{camellabs_iot_gateway_mock_sensor_consumer_mqtt_broker_url}}}}");
        }

}