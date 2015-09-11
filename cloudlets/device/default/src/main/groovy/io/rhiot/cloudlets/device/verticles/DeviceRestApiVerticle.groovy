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
package io.rhiot.cloudlets.device.verticles

import io.rhiot.cloudlets.device.DeviceCloudlet
import io.rhiot.vertx.web.BaseRestApiVerticle
import io.vertx.core.Handler
import io.vertx.groovy.core.buffer.Buffer

import static com.github.camellabs.iot.cloudlet.device.leshan.DeviceDetail.allDeviceDetails
import static io.vertx.core.http.HttpMethod.POST

class DeviceRestApiVerticle extends BaseRestApiVerticle {

    {
        restApi { verticle ->
            get('/device', 'listDevices')
            get('/device/disconnected', LeshanServerVeritcle.CHANNEL_DEVICES_DISCONNECTED)
            delete('/device', 'deleteClients')
            get('/device/:deviceId', 'getClient')
            delete('/device/:deviceId', LeshanServerVeritcle.CHANNEL_DEVICE_DELETE)
            get('/device/:deviceId/heartbeat', LeshanServerVeritcle.CHANNEL_DEVICE_HEARTBEAT_SEND)
            get('/device/:deviceId/details', 'device.details')
            allDeviceDetails().parallelStream().each { details ->
                get("/device/:deviceId/${details.metric()}", "client.${details.metric()}")
            }

            router.route('/device').method(POST).handler { rc ->
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
