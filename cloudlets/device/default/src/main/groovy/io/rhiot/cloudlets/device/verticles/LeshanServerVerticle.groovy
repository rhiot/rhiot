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

import com.github.camellabs.iot.cloudlet.device.client.VirtualDevice
import com.github.camellabs.iot.cloudlet.device.leshan.CachingClientRegistry
import com.github.camellabs.iot.cloudlet.device.leshan.InfinispanCacheProvider
import com.github.camellabs.iot.cloudlet.device.leshan.MongoDbClientRegistry
import io.rhiot.cloudlets.device.DeviceCloudlet
import io.rhiot.cloudlets.device.analytics.DeviceMetricsStore
import io.vertx.core.Future
import io.vertx.groovy.core.eventbus.Message
import io.vertx.lang.groovy.GroovyVerticle
import org.eclipse.leshan.core.node.LwM2mResource
import org.eclipse.leshan.core.request.ReadRequest
import org.eclipse.leshan.core.response.LwM2mResponse
import org.eclipse.leshan.core.response.ValueResponse
import org.eclipse.leshan.server.californium.LeshanServerBuilder
import org.eclipse.leshan.server.californium.impl.LeshanServer
import org.eclipse.leshan.server.client.Client
import org.eclipse.leshan.server.client.ClientUpdate
import org.infinispan.configuration.cache.Configuration
import org.infinispan.configuration.cache.ConfigurationBuilder
import org.infinispan.configuration.global.GlobalConfigurationBuilder
import org.infinispan.manager.DefaultCacheManager

import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.concurrent.ConcurrentHashMap

import static com.github.camellabs.iot.cloudlet.device.client.LeshanClientTemplate.createVirtualLeshanClientTemplate
import static com.github.camellabs.iot.cloudlet.device.leshan.DeviceDetail.allDeviceDetails
import static io.rhiot.steroids.Steroids.bean
import static io.rhiot.utils.Properties.intProperty
import static io.rhiot.utils.Properties.longProperty
import static io.rhiot.vertx.Vertxes.assertStringBody
import static io.rhiot.vertx.jackson.Jacksons.json
import static java.time.Instant.ofEpochMilli
import static java.time.LocalDateTime.ofInstant
import static java.util.concurrent.TimeUnit.DAYS
import static java.util.concurrent.TimeUnit.MINUTES
import static org.eclipse.leshan.ResponseCode.CONTENT
import static org.infinispan.configuration.cache.CacheMode.INVALIDATION_ASYNC

class LeshanServerVerticle extends GroovyVerticle {

    // Constants

    private static final def DEFAULT_DISCONNECTION_PERIOD = MINUTES.toMillis(1)

    static final def CHANNEL_DEVICES_LIST = 'devices.list'

    static final def CHANNEL_DEVICE_GET = 'device.get'

    static final def CHANNEL_DEVICES_DISCONNECTED = 'devices.disconnected'

    static final def CHANNEL_DEVICES_DEREGISTER = 'devices.delete'

    static final def CHANNEL_DEVICE_DEREGISTER = 'device.delete'

    static final def CHANNEL_DEVICE_DETAILS = 'device.details'

    static final def CHANNEL_DEVICE_HEARTBEAT_SEND = 'device.heartbeat.update'

    static final def CHANNEL_DEVICE_CREATE_VIRTUAL = 'device.create.virtual'

    static final def UNKNOWN_DISCONNECTED = 'unknown - device disconnected'

    // Collaborators

    final def LeshanServer leshanServer

    final def deviceMetricsStore = bean(DeviceMetricsStore.class).get()

    // Configuration

    final def lwm2mPort = intProperty('lwm2m_port', LeshanServerBuilder.PORT)

    final def disconnectionPeriod = longProperty('disconnectionPeriod', DEFAULT_DISCONNECTION_PERIOD)

    LeshanServerVerticle() {
        def cacheManager = new DefaultCacheManager(new GlobalConfigurationBuilder().transport().defaultTransport().build())
        Configuration builder = new ConfigurationBuilder().clustering().cacheMode(INVALIDATION_ASYNC).build();
        cacheManager.defineConfiguration("clients", builder);

        def clientRegistry = new CachingClientRegistry(new MongoDbClientRegistry(), new InfinispanCacheProvider(cacheManager))
        def leshanServerBuilder = new LeshanServerBuilder()
        leshanServerBuilder.setLocalAddress('0.0.0.0', lwm2mPort)
        leshanServer = leshanServerBuilder.setClientRegistry(clientRegistry).build()
    }

    @Override
    void start(Future<Void> startFuture) throws Exception {
        vertx.runOnContext {
            leshanServer.start()

            vertx.eventBus().consumer(CHANNEL_DEVICES_LIST) { msg ->
                wrapIntoJsonResponse(msg, 'devices', leshanServer.clientRegistry.allClients())
            }

            vertx.eventBus().consumer(CHANNEL_DEVICE_GET) { msg ->
                def deviceId = assertStringBody(msg, 'Expected device identifier.')
                deviceId.isPresent() && wrapIntoJsonResponse(msg, 'device', leshanServer.clientRegistry.get(deviceId.get()))
            }

            vertx.eventBus().consumer(CHANNEL_DEVICES_DISCONNECTED) { msg ->
                wrapIntoJsonResponse(msg, 'disconnectedDevices', disconnectedClients())
            }

            vertx.eventBus().consumer(CHANNEL_DEVICES_DEREGISTER) { msg ->
                leshanServer.clientRegistry.allClients().each {
                    client -> leshanServer.clientRegistry.deregisterClient(client.registrationId)
                }
                wrapIntoJsonResponse(msg, 'status', 'success')
            }

            vertx.eventBus().consumer(CHANNEL_DEVICE_DEREGISTER) { msg ->
                def deviceId = assertStringBody(msg, 'Expected device identifier.')
                if(deviceId.isPresent()) {
                    def client = leshanServer.clientRegistry.get(deviceId.get())
                    leshanServer.clientRegistry.deregisterClient(client.registrationId)
                    wrapIntoJsonResponse(msg, 'status', 'success')
                }
            }

            vertx.eventBus().consumer(CHANNEL_DEVICE_HEARTBEAT_SEND) { msg ->
                def deviceId = assertStringBody(msg, 'Expected device identifier.')
                if(deviceId.isPresent()) {
                    def client = leshanServer.clientRegistry.get(deviceId.get())
                    if (client == null) {
                        msg.fail(-1, "No device with id ${deviceId}.")
                    } else {
                        leshanServer.clientRegistry.updateClient(new ClientUpdate(client.registrationId, client.address, client.port, client.lifeTimeInSec, client.smsNumber,
                                client.bindingMode, client.objectLinks))
                        wrapIntoJsonResponse(msg, 'status', 'success')
                    }
                }
            }

            vertx.eventBus().consumer(CHANNEL_DEVICE_DETAILS) { msg ->
                def deviceId = assertStringBody(msg, 'Expected device identifier.')
                if(deviceId.isPresent()) {
                    def client = leshanServer.clientRegistry.get(deviceId.get())
                    if (client == null) {
                        msg.fail(0, "No client with ID ${deviceId.get()}.")
                    } else {
                        def results = new ConcurrentHashMap()
                        allDeviceDetails().parallelStream().each { detail ->
                            results[detail.metric()] = readFromAnalytics(client, detail.resource(), detail.metric())
                        }
                        wrapIntoJsonResponse(msg, 'deviceDetails', results)
                    }
                }
            }

            allDeviceDetails().parallelStream().each { details ->
                vertx.eventBus().consumer("client.${details.metric()}") { msg ->
                    def clientId = msg.body().toString()
                    def client = leshanServer.clientRegistry.get(clientId)
                    if (client == null) {
                        msg.fail(0, "No client with ID ${clientId}.")
                    } else {
                        def value = readFromAnalytics(client, details.resource(), details.metric())
                        wrapIntoJsonResponse(msg, details.metric(), value)
                    }
                }
            }

            vertx.eventBus().consumer(CHANNEL_DEVICE_CREATE_VIRTUAL) { msg ->
                def device = msg.body().asType(Map.class)
                createVirtualLeshanClientTemplate(device.clientId, lwm2mPort).connect().disconnect()
                def devicePrototype = new VirtualDevice()
                deviceMetricsStore.saveDeviceMetric(device.clientId, 'manufacturer', devicePrototype.manufacturer())
                deviceMetricsStore.saveDeviceMetric(device.clientId, 'modelNumber', devicePrototype.modelNumber())
                deviceMetricsStore.saveDeviceMetric(device.clientId, 'serialNumber', devicePrototype.serialNumber())
                deviceMetricsStore.saveDeviceMetric(device.clientId, 'firmwareVersion', devicePrototype.firmwareVersion())
                def client = leshanServer.clientRegistry.get(device.clientId)
                leshanServer.clientRegistry.updateClient(new ClientUpdate(client.registrationId, client.address, client.port, DAYS.toSeconds(365), client.smsNumber,
                        client.bindingMode, client.objectLinks))
                wrapIntoJsonResponse(msg, 'status', 'success')
            }

            DeviceCloudlet.@isStarted.countDown()
            startFuture.complete()
        }
    }

    // Helpers

    String readFromAnalytics(Client client, String resource, String metric) {
        def value = stringResponse(leshanServer.send(client, new ReadRequest(resource), 1000))
        if (value == null) {
            value = deviceMetricsStore.readDeviceMetric(client.endpoint, metric, String.class)
            if (value == null) {
                value = UNKNOWN_DISCONNECTED
            }
        } else {
            deviceMetricsStore.saveDeviceMetric(client.endpoint, metric, value)
        }
        value
    }

    private String stringResponse(LwM2mResponse response) {
        if(response == null) {
            return null
        }
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

    def wrapIntoJsonResponse(Message message, String root, Object pojo) {
        def json = json().writeValueAsString(["${root}": pojo])
        message.reply(json)
    }

}
