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

import io.vertx.core.Future
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

import static com.github.camellabs.iot.cloudlet.device.leshan.DeviceDetail.allDeviceDetails
import static io.rhiot.utils.Properties.intProperty
import static io.rhiot.utils.Properties.longProperty
import static java.util.concurrent.TimeUnit.MINUTES
import static org.eclipse.leshan.ResponseCode.CONTENT
import static org.infinispan.configuration.cache.CacheMode.INVALIDATION_ASYNC

class LeshanServerVerticle {

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

    static def LeshanServer leshanServer

    // Configuration

    final def lwm2mPort = intProperty('lwm2m_port', LeshanServerBuilder.PORT)

    final def disconnectionPeriod = longProperty('disconnectionPeriod', DEFAULT_DISCONNECTION_PERIOD)

    LeshanServerVerticle() {
        def cacheManager = new DefaultCacheManager(new GlobalConfigurationBuilder().transport().defaultTransport().build())
        Configuration builder = new ConfigurationBuilder().clustering().cacheMode(INVALIDATION_ASYNC).build();
        cacheManager.defineConfiguration("clients", builder);

        def leshanServerBuilder = new LeshanServerBuilder()
        leshanServerBuilder.setLocalAddress('0.0.0.0', lwm2mPort)
        leshanServer = leshanServerBuilder.build()
    }

    void start(Future<Void> startFuture) throws Exception {
        vertx.runOnContext {
            leshanServer.start()

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

            startFuture.complete()
        }
    }

    // Helpers

    String readFromAnalytics(Client client, String resource, String metric) {
        def value = stringResponse(leshanServer.send(client, new ReadRequest(resource), 1000))
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

    def wrapIntoJsonResponse(message, String root, Object pojo) {
        def json = json().writeValueAsString(["${root}": pojo])
        message.reply(json)
    }

}
