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
package io.rhiot.datastream.engine.test

import io.rhiot.bootstrap.BeanRegistry
import io.rhiot.datastream.engine.DataStream
import io.rhiot.datastream.engine.encoding.PayloadEncoding
import org.apache.camel.CamelContext
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before

import static io.rhiot.steroids.activemq.EmbeddedActiveMqBrokerBootInitializer.amqp
import static io.rhiot.utils.Properties.restoreSystemProperties

abstract class DataStreamTest extends Assert {

    static private boolean dataStreamStarted

    protected static DataStream dataStream = new DataStream()

    protected static BeanRegistry beanRegistry

    protected static CamelContext camelContext

    protected static PayloadEncoding payloadEncoding


    @Before
    public void before() {
        beforeDataStreamStarted()
        if(!dataStreamStarted) {
            dataStream = dataStream.start().asType(DataStream.class)
            beanRegistry = dataStream.beanRegistry()
            camelContext = beanRegistry.bean(CamelContext.class).get()
            payloadEncoding = beanRegistry.bean(PayloadEncoding.class).get()
            dataStreamStarted = true
        }
        afterDataStreamStarted()
    }

    protected void beforeDataStreamStarted() {
    }

    protected void afterDataStreamStarted() {
    }

    @AfterClass
    public static void after() {
        try {
            dataStream.stop()
        } finally {
            restoreSystemProperties()
        }
    }

    protected def void toBus(String channel) {
        camelContext.createProducerTemplate().sendBody(amqp(channel), null)
    }

    protected def void toBus(String channel, Object payload) {
        camelContext.createProducerTemplate().sendBody(amqp(channel), payloadEncoding.encode(payload))
    }

    protected def <T> T fromBus(String channel, Class<T> responseType) {
        def busResponse = camelContext.createProducerTemplate().requestBody(amqp(channel), null, byte[].class)
        payloadEncoding.decode(busResponse) as T
    }

    protected def <T> T fromBus(String channel, Object payload, Class<T> responseType) {
        def busResponse = camelContext.createProducerTemplate().requestBody(amqp(channel), payloadEncoding.encode(payload), byte[].class)
        payloadEncoding.decode(busResponse) as T
    }

}
