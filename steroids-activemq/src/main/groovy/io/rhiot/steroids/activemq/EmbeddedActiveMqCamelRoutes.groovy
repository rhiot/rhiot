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
import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.camel.builder.RouteBuilder
import org.springframework.jms.connection.CachingConnectionFactory

import static io.rhiot.steroids.activemq.EmbeddedActiveMqBrokerBootInitializer.DEFAULT_BROKER_NAME
import static io.rhiot.steroids.camel.CamelBootInitializer.eventBus
import static io.rhiot.steroids.camel.CamelBootInitializer.registry

@Route
class EmbeddedActiveMqCamelRoutes extends RouteBuilder {

    @Override
    void configure() {
        def jmsConnectionFactory = new CachingConnectionFactory(new ActiveMQConnectionFactory("vm:${DEFAULT_BROKER_NAME}"))
        registry().put('jmsConnectionFactory', jmsConnectionFactory)
        from('jms:topic:>?connectionFactory=#jmsConnectionFactory').to(mqttEventBus())
    }

    static String mqttEventBus() {
        eventBus('mqtt')
    }

}
