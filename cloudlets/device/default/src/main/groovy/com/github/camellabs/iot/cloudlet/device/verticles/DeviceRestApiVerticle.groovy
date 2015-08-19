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

import com.github.camellabs.iot.cloudlet.device.vertx.BaseRestApiVerticle
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.groovy.core.buffer.Buffer

import static com.github.camellabs.iot.cloudlet.device.verticles.LeshanServerVeritcle.CHANNEL_DEVICES_DISCONNECTED
import static com.github.camellabs.iot.cloudlet.device.vertx.BaseRestApiVerticle.*
import static com.github.camellabs.iot.vertx.PropertyResolver.intProperty
import static io.vertx.core.http.HttpMethod.DELETE
import static io.vertx.core.http.HttpMethod.GET
import static io.vertx.core.http.HttpMethod.POST

class DeviceRestApiVerticle extends BaseRestApiVerticle {

    @Override
    void start(Future<Void> startFuture) {
        super.start(startFuture)
        vertx.runOnContext {
            get('/device', 'listDevices')
            get('/device/disconnected', CHANNEL_DEVICES_DISCONNECTED)
            delete('/client', 'deleteClients')
            get('/client/:clientId', 'getClient')
            get('/client/:clientId/manufacturer', 'client.manufacturer')
            get('/client/:clientId/model', 'client.model')
            get('/client/:clientId/serial', 'client.serial')

            router.route('/client').method(POST).handler { rc ->
                rc.request().bodyHandler(new Handler<Buffer>(){
                    @Override
                    void handle(Buffer event) {
                        vertx.eventBus().send('clients.create.virtual', event.toString('utf-8'), { status -> jsonResponse(rc, status) })
                    }
                })
            }

            http.requestHandler(router.&accept).listen(intProperty('camellabs_iot_cloudlet_device_api_rest_port', 15000))

            startFuture.complete()
        }
    }

}
