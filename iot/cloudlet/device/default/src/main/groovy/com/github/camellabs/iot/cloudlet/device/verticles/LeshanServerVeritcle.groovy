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

import com.github.camellabs.iot.cloudlet.device.leshan.CachingClientRegistry
import com.github.camellabs.iot.cloudlet.device.leshan.InfinispanCacheProvider
import com.github.camellabs.iot.cloudlet.device.leshan.MongoDbClientRegistry
import com.mongodb.Mongo
import io.vertx.core.Future
import io.vertx.lang.groovy.GroovyVerticle
import org.eclipse.leshan.core.node.LwM2mResource
import org.eclipse.leshan.core.request.ReadRequest
import org.eclipse.leshan.core.response.LwM2mResponse
import org.eclipse.leshan.core.response.ValueResponse
import org.eclipse.leshan.server.californium.LeshanServerBuilder
import org.eclipse.leshan.server.californium.impl.LeshanServer
import org.infinispan.configuration.cache.Configuration
import org.infinispan.configuration.cache.ConfigurationBuilder
import org.infinispan.configuration.global.GlobalConfigurationBuilder
import org.infinispan.manager.DefaultCacheManager

import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

import static com.github.camellabs.iot.cloudlet.device.vertx.Vertxes.wrapIntoJsonResponse
import static com.github.camellabs.iot.vertx.PropertyResolver.intProperty
import static java.time.Instant.ofEpochMilli
import static java.time.LocalDateTime.ofInstant
import static org.eclipse.leshan.ResponseCode.CONTENT
import static org.infinispan.configuration.cache.CacheMode.INVALIDATION_ASYNC

class LeshanServerVeritcle extends GroovyVerticle {

    final def LeshanServer leshanServer

    final def disconnectionPeriod = intProperty('camellabs_iot_cloudlet_device_disconnectionPeriod', 60 * 1000)

    LeshanServerVeritcle() {
        def mongo = new Mongo()

        def cacheManager = new DefaultCacheManager(new GlobalConfigurationBuilder().transport().defaultTransport().build())
        Configuration builder = new ConfigurationBuilder().clustering().cacheMode(INVALIDATION_ASYNC).build();
        cacheManager.defineConfiguration("clients", builder);

        def clientRegistry = new CachingClientRegistry(new MongoDbClientRegistry(mongo), new InfinispanCacheProvider(cacheManager))
        leshanServer = new LeshanServerBuilder().setClientRegistry(clientRegistry).build()
    }

    @Override
    void start(Future<Void> startFuture) throws Exception {
        vertx.runOnContext {
            leshanServer.start()

            vertx.eventBus().localConsumer('listClients') { msg ->
                wrapIntoJsonResponse(msg, 'clients', leshanServer.clientRegistry.allClients())
            }

            vertx.eventBus().localConsumer('clients.disconnected') { msg ->
                wrapIntoJsonResponse(msg, 'disconnectedClients', disconnectedClients())
            }

            vertx.eventBus().localConsumer('deleteClients') { msg ->
                leshanServer.clientRegistry.allClients().each {
                    client -> leshanServer.clientRegistry.deregisterClient(client.registrationId)
                }
                wrapIntoJsonResponse(msg, 'Status', 'Success')
            }

            vertx.eventBus().localConsumer('getClient') { msg ->
                wrapIntoJsonResponse(msg, 'client', leshanServer.clientRegistry.get(msg.body().toString()))
            }

            vertx.eventBus().localConsumer('client.manufacturer') { msg ->
                def clientId = msg.body().toString()
                def client = leshanServer.clientRegistry.get(clientId)
                if (client == null) {
                    msg.fail(0, "No client with ID ${clientId}.")
                } else {
                    wrapIntoJsonResponse(msg, 'manufacturer', stringResponse(leshanServer.send(client, new ReadRequest('/3/0/0'))))
                }
            }

            startFuture.complete()
        }
    }

    // Helpers

    private String stringResponse(LwM2mResponse response) {
        if (response.code != CONTENT || !(response instanceof ValueResponse)) {
            return null
        }
        def content = response.asType(ValueResponse.class).content
        if (!(content instanceof LwM2mResource)) {
            return null
        }
        content.asType(LwM2mResource).value.value
    }

    private List<String> disconnectedClients() {
        leshanServer.clientRegistry.allClients().findAll { client ->
            def updated = ofInstant(ofEpochMilli(client.lastUpdate.time), ZoneId.systemDefault()).toLocalTime()
            updated.plus(disconnectionPeriod, ChronoUnit.MILLIS).isBefore(LocalTime.now())
        }.collect { client -> client.endpoint }
    }

}
