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

import io.vertx.core.Future
import io.vertx.lang.groovy.GroovyVerticle

import static com.github.camellabs.iot.cloudlet.device.vertx.Vertxes.*
import static io.vertx.core.http.HttpMethod.DELETE
import static io.vertx.core.http.HttpMethod.GET
import static io.vertx.groovy.ext.web.Router.router

class RestApiVerticle extends GroovyVerticle {

    @Override
    void start(Future<Void> startFuture) throws Exception {
        vertx.runOnContext {
            def http = vertx.createHttpServer()
            def router = router(vertx)

            router.route("/client").method(GET).handler { rc ->
                vertx.eventBus().send('listClients', null, { clients -> jsonResponse(rc, clients) })
            }

            router.route("/clients/disconnected").method(GET).handler { rc ->
                vertx.eventBus().send('clients.disconnected', null, { clients -> jsonResponse(rc, clients) })
            }

            router.route("/client").method(DELETE).handler { rc ->
                vertx.eventBus().send('deleteClients', null, { status -> jsonResponse(rc, status) })
            }

            router.route("/client/:clientId").method(GET).handler { rc ->
                vertx.eventBus().send('getClient', parameter(rc, 'clientId')) { client -> jsonResponse(rc, client) }
            }

            router.route("/client/:clientId/manufacturer").method(GET).handler { rc ->
                vertx.eventBus().send('client.manufacturer', parameter(rc, 'clientId')) { client ->
                    jsonResponse(rc, client)
                }
            }

            http.requestHandler(router.&accept).listen(intProperty('camellabs_iot_cloudlet_device_api_rest_port', 8080))

            startFuture.complete()
        }
    }

}
