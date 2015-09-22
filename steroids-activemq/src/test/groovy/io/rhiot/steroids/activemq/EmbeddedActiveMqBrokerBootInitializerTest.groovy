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
package io.rhiot.steroids.activemq

import io.rhiot.steroids.bootstrap.Bootstrap
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.impl.DefaultCamelContext
import org.junit.AfterClass
import org.junit.Test

class EmbeddedActiveMqBrokerBootInitializerTest {

    static def bootstrap = new Bootstrap().start()

    static def camel = new DefaultCamelContext()

    @AfterClass
    static void AfterClass() {
        bootstrap.stop()
        camel.stop()
    }

    @Test
    void shouldStartEmdeddedMqttBroker() {
        // Given
        camel.addRoutes(new RouteBuilder() {
            @Override
            void configure() {
                from('paho:test?brokerUrl=tcp://localhost:1883').to('mock:test')
            }
        })
        camel.start()
        def mock = camel.getEndpoint('mock:test', MockEndpoint.class)
        mock.setMinimumExpectedMessageCount(1)

        // When
        camel.createProducerTemplate().sendBody('paho:test?brokerUrl=tcp://localhost:1883', 'foo')

        // Then
        mock.assertIsSatisfied()
    }

}
