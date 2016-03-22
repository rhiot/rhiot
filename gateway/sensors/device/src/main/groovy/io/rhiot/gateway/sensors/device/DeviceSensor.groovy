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
package io.rhiot.gateway.sensors.device

import groovy.transform.CompileStatic
import io.rhiot.cloudplatform.connector.IoTConnector
import org.apache.camel.builder.RouteBuilder
import io.rhiot.cloudplatform.service.device.api.Device

@CompileStatic
class DeviceSensor extends RouteBuilder {

    private final IoTConnector connector

    private final String deviceId

    DeviceSensor(IoTConnector connector, String deviceId) {
        this.connector = connector
        this.deviceId = deviceId
    }

    @Override
    void configure() throws Exception {
        from('timer:register?delay=5000&repeatCount=1').process {
            connector.toBus('device.register', new Device(deviceId: deviceId))
        }
    }

}