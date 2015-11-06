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
package io.rhiot.cloudlets.device.verticles

import io.rhiot.cloudlets.device.DeviceCloudlet
import io.rhiot.vertx.web.BaseRestApiVerticle

import static com.github.camellabs.iot.cloudlet.device.leshan.DeviceDetail.allDeviceDetails
import static LeshanServerVerticle.CHANNEL_DEVICES_DEREGISTER
import static LeshanServerVerticle.CHANNEL_DEVICES_DISCONNECTED
import static LeshanServerVerticle.CHANNEL_DEVICES_LIST
import static LeshanServerVerticle.CHANNEL_DEVICE_CREATE_VIRTUAL
import static LeshanServerVerticle.CHANNEL_DEVICE_DEREGISTER
import static LeshanServerVerticle.CHANNEL_DEVICE_DETAILS
import static LeshanServerVerticle.CHANNEL_DEVICE_GET
import static LeshanServerVerticle.CHANNEL_DEVICE_HEARTBEAT_SEND

class DeviceRestApiVerticle extends BaseRestApiVerticle {

    {
        restApi { verticle ->
            get('/device', CHANNEL_DEVICES_LIST)
            get('/device/disconnected', CHANNEL_DEVICES_DISCONNECTED)
            get('/device/:deviceId/heartbeat', CHANNEL_DEVICE_HEARTBEAT_SEND)
            get('/device/:deviceId', CHANNEL_DEVICE_GET)
            delete('/device', CHANNEL_DEVICES_DEREGISTER)
            delete('/device/:deviceId', CHANNEL_DEVICE_DEREGISTER)
            get('/device/:deviceId/details', CHANNEL_DEVICE_DETAILS)
            allDeviceDetails().parallelStream().each { details ->
                get("/device/:deviceId/${details.metric()}", "client.${details.metric()}")
            }
            post('/device', CHANNEL_DEVICE_CREATE_VIRTUAL)

            DeviceCloudlet.@isStarted.countDown()
        }
    }

}
