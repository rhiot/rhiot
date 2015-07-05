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
import com.mongodb.Mongo
import io.vertx.core.Future
import io.vertx.lang.groovy.GroovyVerticle
import org.eclipse.leshan.server.californium.LeshanServerBuilder

import static com.github.camellabs.iot.cloudlet.device.DeviceCloudlet.jackson

class LeshanServerVeritcle extends GroovyVerticle {

    final def mongo = new Mongo()

    final def leshanServer = new LeshanServerBuilder().setClientRegistry(new MongoDbClientRegistry(mongo)).build()

    @Override
    void start(Future<Void> startFuture) throws Exception {
        new Thread(){
            @Override
            void run() {
                leshanServer.start()

                vertx.eventBus().localConsumer('listClients', {
                    msg ->
                        def clients = leshanServer.clientRegistry.allClients()
                        def json = jackson.writeValueAsString([clients: clients])
                        msg.reply(json)
                })

                vertx.eventBus().localConsumer('deleteClients', {
                    msg ->
                        leshanServer.clientRegistry.allClients().each {
                            client -> leshanServer.clientRegistry.deregisterClient(client.registrationId) }
                        def json = jackson.writeValueAsString([Status: 'Success'])
                        msg.reply(json)
                })

                vertx.eventBus().localConsumer('getClient', {
                    msg ->
                        def client = leshanServer.clientRegistry.get(msg.body().toString())
                        def json = jackson.writeValueAsString([client: client])
                        msg.reply(json)
                })

                startFuture.complete()
            }
        }.start()
    }

}
