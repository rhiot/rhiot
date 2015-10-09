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

import io.rhiot.steroids.camel.Route
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.amqp.AMQPComponent
import org.apache.qpid.amqp_1_0.jms.impl.ConnectionFactoryImpl

import static io.rhiot.steroids.activemq.EmbeddedActiveMqBrokerBootInitializer.amqpPort
import static io.rhiot.steroids.activemq.EmbeddedActiveMqBrokerBootInitializer.externalBrokerUrl

@Route
class CamelAmqpConfiguration extends RouteBuilder {

    @Override
    void configure() {
        def brokerUrl = externalBrokerUrl() ?: 'localhost'

        // Starting from Camel 2.16.1 (due to the CAMEL-9204) replace the code below with:
        // AMQPComponent.amqp10Component(uri)
        def connectionFactory = ConnectionFactoryImpl.createFromURL("amqp://guest:guest@${brokerUrl}:${amqpPort()}")
        connectionFactory.topicPrefix = 'topic://'
        def amqpComponent = new AMQPComponent(connectionFactory)

        context.addComponent('amqp', amqpComponent)
    }

}
