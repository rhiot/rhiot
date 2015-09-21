/**
 * Licensed to the Rhiot under one or more
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
package io.rhiot.gateway.mock

import io.rhiot.gateway.GatewayVerticle
import com.github.camellabs.iot.vertx.camel.GroovyCamelVerticle

import static io.rhiot.utils.Properties.intProperty
import static io.rhiot.utils.Properties.stringProperty

@GatewayVerticle(conditionProperty = 'camellabs_iot_gateway_mock_sensor_consumer')
class MockSensorConsumerVerticle extends GroovyCamelVerticle {

    def target = stringProperty('camellabs_iot_gateway_mock_sensor_consumer_target')

    def mqttQos = intProperty('camellabs_iot_gateway_mock_sensor_consumer_mqtt_qos', 0)

    def mqttBrokerUrl = stringProperty('camellabs_iot_gateway_mock_sensor_consumer_mqtt_broker_url')

    @Override
    void start() {
        fromEventBus('mockSensor') {
            it.routeId("mockSensorConsumer").to("${target ?: "paho:mock?qos=${mqttQos}&brokerUrl=${mqttBrokerUrl}"}")
        }
    }

}
