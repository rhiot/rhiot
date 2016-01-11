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
package io.rhiot.cloudplatform.test

import io.rhiot.cloudplatform.CloudPlatform
import io.rhiot.cloudplatform.encoding.spi.PayloadEncoding
import org.apache.camel.CamelContext
import org.apache.camel.ProducerTemplate
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before

import static io.rhiot.utils.Networks.findAvailableTcpPort
import static io.rhiot.utils.Properties.restoreSystemProperties
import static io.rhiot.utils.Properties.setBooleanProperty
import static io.rhiot.utils.Properties.setIntProperty

abstract class DataStreamTest extends Assert {

    static private boolean dataStreamStarted

    protected static CloudPlatform cloudPlatform = new CloudPlatform()

    protected static CamelContext camelContext

    protected static ProducerTemplate producerTemplate

    protected static PayloadEncoding payloadEncoding


    @Before
    public void before() {
        setBooleanProperty('spring.activemq.broker.enabled', true)
        setBooleanProperty('spring.activemq.broker.amqpEnabled', true)
        def amqpPort = findAvailableTcpPort()
        setIntProperty('spring.activemq.broker.amqpPort', amqpPort)
        setIntProperty('AMQP_SERVICE_PORT', amqpPort)

        if(!dataStreamStarted) {
            beforeDataStreamStarted()
            cloudPlatform = cloudPlatform.start().asType(CloudPlatform.class)
            camelContext = cloudPlatform.applicationContext().getBean(CamelContext.class)
            producerTemplate = camelContext.createProducerTemplate()
            payloadEncoding = cloudPlatform.applicationContext().getBean(PayloadEncoding.class)
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
            cloudPlatform.stop()
        } finally {
            dataStreamStarted = false
            restoreSystemProperties()
        }
    }

    // Bus communication helpers

    protected def void toBus(String channel) {
        producerTemplate.sendBody("amqp:${channel}", null)
    }

    protected def void toBus(String channel, Object payload) {
        producerTemplate.sendBody("amqp:${channel}", payloadEncoding.encode(payload))
    }

    protected def void toBusAndWait(String channel) {
        def busResponse = producerTemplate.requestBody("amqp:${channel}", null, byte[].class)
        payloadEncoding.decode(busResponse)
    }

    protected def void toBusAndWait(String channel, Object payload) {
        def busResponse = producerTemplate.requestBody("amqp:${channel}", payloadEncoding.encode(payload), byte[].class)
        payloadEncoding.decode(busResponse)
    }

    protected def <T> T fromBus(String channel, Class<T> responseType) {
        def busResponse = producerTemplate.requestBody("amqp:${channel}", null, byte[].class)
        payloadEncoding.decode(busResponse) as T
    }

    protected def <T> T fromBus(String channel, Object payload, Class<T> responseType) {
        def busResponse = producerTemplate.requestBody("amqp:${channel}", payloadEncoding.encode(payload), byte[].class)
        payloadEncoding.decode(busResponse) as T
    }

}
