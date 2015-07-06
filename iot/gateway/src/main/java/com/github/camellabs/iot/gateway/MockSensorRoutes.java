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

@Component
@ConditionalOnProperty(value = "camellabs_iot_gateway_mock_sensor", havingValue = "true")
public class MockSensorRoutes extends RouteBuilder {

    @Value("${camellabs_iot_gateway_mock_sensor_number:10}")
    private int sourcesNumber;

    @Override
    public void configure() throws Exception {
        for (int i = 1; i <= sourcesNumber; i++) {
            String sourceId = "mockSensor-" + i;
            from("timer:" + sourceId + "?period={{camellabs_iot_gateway_mock_sensor_period:10}}").routeId(sourceId).
                    transform().groovy("UUID.randomUUID().toString()").
                    to("seda:mockSensor?blockWhenFull=true");
        }
    }

}