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
package io.rhiot.gateway.sensors.camera

import io.rhiot.cloudplatform.connector.IoTConnector
import org.apache.camel.builder.RouteBuilder

import static io.rhiot.cloudplatform.connector.Header.arguments

class CameraSensor extends RouteBuilder {

    private final IoTConnector connector

    private final String deviceId

    CameraSensor(IoTConnector connector, String deviceId) {
        this.connector = connector
        this.deviceId = deviceId
    }

    @Override
    void configure() {
        from('webcam:camera-sensor?motion=true&format=jpg&motionInterval=250').process {
            connector.toBus('camera.process', it.in.body, arguments(deviceId, 'eu'))
        }
    }

}