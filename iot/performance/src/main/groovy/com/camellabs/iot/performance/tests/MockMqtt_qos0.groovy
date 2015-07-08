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
package com.camellabs.iot.performance.tests

import com.camellabs.iot.performance.TestSpecification

import static com.camellabs.iot.performance.MqttServer.mqttPort

class MockMqtt_qos0 implements TestSpecification {

    @Override
    boolean supportsHardwareKit(String kit) {
        kit.startsWith('RPI2')
    }

    @Override
    Map<String, String> additionalProperties() {
        def ip = ''
        def nics = Collections.list(NetworkInterface.getNetworkInterfaces())
        for (NetworkInterface n : nics) {
            if (n.name.startsWith('wlan')) {
                def x = n.inetAddresses
                x.nextElement()
                ip = x.nextElement().hostAddress
            }
        }
        def mqttBrokerUrl = 'tcp://' + ip + ':' + mqttPort
        [camellabs_iot_gateway_mock_sensor                         : true,
         camellabs_iot_gateway_mock_sensor_number                  : 15,
         camellabs_iot_gateway_mock_sensor_period                  : 5,
         camellabs_iot_gateway_mock_sensor_consumer                : true,
         camellabs_iot_gateway_mock_sensor_consumer_number         : 20,
         camellabs_iot_gateway_mock_sensor_consumer_mqtt_broker_url: mqttBrokerUrl,
         camellabs_iot_gateway_mock_sensor_consumer_mqtt_qos       : 0]
    }

}
