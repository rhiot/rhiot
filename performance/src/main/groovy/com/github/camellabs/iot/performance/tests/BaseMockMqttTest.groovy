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
package com.github.camellabs.iot.performance.tests

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.camellabs.iot.performance.TestSpecification
import io.rhiot.scanner.Device
import io.rhiot.utils.ssh.client.SshClient

import java.text.SimpleDateFormat

import static com.github.camellabs.iot.performance.MqttServer.getMqttPort
import static io.rhiot.utils.Networks.currentLocalNetworkIp

abstract class BaseMockMqttTest implements TestSpecification {

    static private final def CAMEL_ROUTE_RESET_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

    @Override
    boolean supportsHardwareKit(String kit) {
        kit.startsWith('RPI2')
    }

    @Override
    String testGroup() {
        'Mock sensor to external MQTT broker'
    }

    protected abstract int qos();

    @Override
    Map<String, Object> additionalProperties() {
        [camellabs_iot_gateway_mock_sensor                         : true,
         camellabs_iot_gateway_mock_sensor_number                  : 15,
         camellabs_iot_gateway_mock_sensor_period                  : 5,
         camellabs_iot_gateway_mock_sensor_consumer                : true,
         camellabs_iot_gateway_mock_sensor_consumer_number         : 20,
         camellabs_iot_gateway_mock_sensor_consumer_mqtt_broker_url: "tcp://${currentLocalNetworkIp().get()}:${mqttPort}",
         camellabs_iot_gateway_mock_sensor_consumer_mqtt_qos       : qos()]
    }

    @Override
    long processingTime(Device device) {
        def json = new ObjectMapper()
        def startedString = json.readValue(jolokiaRouteStartedUrl(device, 'mockSensorConsumer'), Map.class)['value'].
                toString().replaceAll(/\+\d\d:\d\d/, '')
        def started = CAMEL_ROUTE_RESET_FORMAT.parse(startedString)
        CAMEL_ROUTE_RESET_FORMAT.parse(new SshClient(device.address().hostAddress, 'pi', 'raspberry').command('date +%Y-%m-%dT%H:%M:%S').first()).time - started.time
    }

    // Helpers

    private URL jolokiaRouteStartedUrl(Device device, String routeId) {
        new URL("http://${device.address().hostAddress}:8778/jolokia/read/org.apache.camel:context=SingletonVertxCamelContext,type=routes,name=%22${routeId}%22/ResetTimestamp")
    }

}