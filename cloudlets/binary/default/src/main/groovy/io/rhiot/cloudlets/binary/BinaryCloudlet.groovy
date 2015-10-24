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
package io.rhiot.cloudlets.binary

import io.rhiot.cloudlets.binary.store.BinaryStore
import io.rhiot.steroids.Steroids
import io.rhiot.steroids.camel.CamelBootstrap
import org.apache.camel.Exchange


import static io.rhiot.steroids.activemq.EmbeddedActiveMqBrokerBootInitializer.mqttJmsBridge
import static io.rhiot.steroids.camel.CamelBootInitializer.eventBus

class BinaryCloudlet extends CamelBootstrap {

    def store = Steroids.bean(BinaryStore.class).get()

    @Override
    void configure() {
        from(mqttJmsBridge('device.binary.>')).
            transform { Exchange exc ->
                def destination = exc.in.getHeader('JmsDestination', String.class)
                def id = destination.substring(destination.lastIndexOf('.') + 1)
                store.storeData(id, exc.in.getBody(InputStream.class))
                id
            }.to(eventBus('device.binary') + '?pubSub=true')
    }

}