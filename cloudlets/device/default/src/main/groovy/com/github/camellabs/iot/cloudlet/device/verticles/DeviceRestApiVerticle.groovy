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
package com.github.camellabs.iot.cloudlet.device.verticles

import com.github.camellabs.iot.cloudlet.device.DeviceCloudlet
import com.github.camellabs.iot.cloudlet.device.vertx.BaseRestApiVerticle
import io.vertx.core.Handler
import io.vertx.groovy.core.buffer.Buffer

import static com.github.camellabs.iot.cloudlet.device.verticles.LeshanServerVeritcle.CHANNEL_DEVICES_DISCONNECTED
import static com.github.camellabs.iot.cloudlet.device.verticles.LeshanServerVeritcle.CHANNEL_DEVICE_DELETE
import static com.github.camellabs.iot.cloudlet.device.verticles.LeshanServerVeritcle.CHANNEL_DEVICE_HEARTBEAT_SEND
import static io.vertx.core.http.HttpMethod.POST

class DeviceRestApiVerticle extends BaseRestApiVerticle {

    {
        restApi { verticle ->
            get('/device', 'listDevices')
            get('/device/disconnected', CHANNEL_DEVICES_DISCONNECTED)
            delete('/device', 'deleteClients')
            get('/client/:deviceId', 'getClient')
            delete('/device/:deviceId', CHANNEL_DEVICE_DELETE)
            get('/device/:deviceId/heartbeat', CHANNEL_DEVICE_HEARTBEAT_SEND)
            get('/client/:deviceId/manufacturer', 'client.manufacturer')
            get('/client/:deviceId/model', 'client.model')
            get('/client/:deviceId/serial', 'client.serial')

            router.route('/client').method(POST).handler { rc ->
                rc.request().bodyHandler(new Handler<Buffer>() {
                    @Override
                    void handle(Buffer event) {
                        vertx.eventBus().send('clients.create.virtual', event.toString('utf-8'), { status -> jsonResponse(rc, status) })
                    }
                })
            }

            DeviceCloudlet.@isStarted.countDown()
        }
    }

}
