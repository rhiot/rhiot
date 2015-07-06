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

import com.github.camellabs.iot.cloudlet.device.leshan.MongoDbClientRegistry
import com.github.camellabs.iot.cloudlet.device.vertx.Vertxes
import com.mongodb.Mongo
import io.vertx.core.Future
import io.vertx.lang.groovy.GroovyVerticle
import org.eclipse.leshan.server.californium.LeshanServerBuilder

import static com.github.camellabs.iot.cloudlet.device.vertx.Vertxes.wrapIntoJsonResponse

class LeshanServerVeritcle extends GroovyVerticle {

    final def mongo = new Mongo()

    final def leshanServer = new LeshanServerBuilder().setClientRegistry(new MongoDbClientRegistry(mongo)).build()

    @Override
    void start(Future<Void> startFuture) throws Exception {
        vertx.runOnContext {
            leshanServer.start()

            vertx.eventBus().localConsumer('listClients') { msg ->
                wrapIntoJsonResponse(msg, 'clients', leshanServer.clientRegistry.allClients())
            }

            vertx.eventBus().localConsumer('deleteClients') { msg ->
                leshanServer.clientRegistry.allClients().each {
                        client -> leshanServer.clientRegistry.deregisterClient(client.registrationId) }
                wrapIntoJsonResponse(msg, 'Status', 'Success')
            }

            vertx.eventBus().localConsumer('getClient') { msg ->
                wrapIntoJsonResponse(msg, 'client', leshanServer.clientRegistry.get(msg.body().toString()))
            }

            startFuture.complete()
        }
    }

}
