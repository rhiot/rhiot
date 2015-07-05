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
package com.github.camellabs.iot.cloudlet.device

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.groovy.core.Vertx

import static com.github.camellabs.iot.cloudlet.device.vertx.Vertxes.jsonResponse
import static com.github.camellabs.iot.cloudlet.device.vertx.Vertxes.parameter
import static io.vertx.core.http.HttpMethod.DELETE
import static io.vertx.core.http.HttpMethod.GET
import static io.vertx.groovy.ext.web.Router.router

class DeviceCloudlet {

    final def vertx = Vertx.vertx()

    static final def jackson = new ObjectMapper()

    DeviceCloudlet start() {
        def http = vertx.createHttpServer()
        def router = router(vertx)

        router.route("/client").method(GET).handler({ rc ->
            vertx.eventBus().send('listClients', null, { clients -> jsonResponse(rc, clients) })
        })

        router.route("/client").method(DELETE).handler({ rc ->
            vertx.eventBus().send('deleteClients', null, { status -> jsonResponse(rc, status) })
        })

        router.route("/client/:clientId").method(GET).handler({ rc ->
            vertx.eventBus().send('getClient', parameter(rc, 'clientId'), { client -> jsonResponse(rc, client) })
        })

        http.requestHandler(router.&accept).listen(8080)

        vertx.deployVerticle("groovy:${LeshanServerVeritcle.class.name}")

        return this
    }

    public static void main(String[] args) {
        new DeviceCloudlet().start()
    }

}
