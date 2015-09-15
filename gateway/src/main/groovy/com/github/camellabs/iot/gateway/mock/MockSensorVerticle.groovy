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
package com.github.camellabs.iot.gateway.mock

import com.github.camellabs.iot.gateway.GatewayVerticle
import com.github.camellabs.iot.vertx.camel.GroovyCamelVerticle

import static io.rhiot.utils.Uuids.uuid
import static io.rhiot.utils.Properties.intProperty

/**
 * Verticle emulating device sensor. It generate random UUID events on the given basis.
 */
@GatewayVerticle(conditionProperty = 'camellabs_iot_gateway_mock_sensor')
class MockSensorVerticle extends GroovyCamelVerticle {

    def sourcesNumber = intProperty('camellabs_iot_gateway_mock_sensor_number', 10)

    def period = intProperty('camellabs_iot_gateway_mock_sensor_period', 10)

    @Override
    void start() {
        for (int i = 1; i <= sourcesNumber; i++) {
            vertx.setPeriodic(period) {
                vertx.eventBus().publish('mockSensor', uuid())
            }
        }
    }

}