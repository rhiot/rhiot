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

import io.rhiot.steroids.bootstrap.BootInitializer
import io.rhiot.utils.Properties
import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.activemq.broker.BrokerService
import org.springframework.jms.connection.CachingConnectionFactory

import static io.rhiot.steroids.activemq.EmbeddedActiveMqBrokerBootInitializer.getDEFAULT_BROKER_NAME
import static io.rhiot.steroids.camel.CamelBootInitializer.registry
import static io.rhiot.utils.Properties.booleanProperty;

public class EmbeddedActiveMqBrokerBootInitializer implements BootInitializer {

    final static def DEFAULT_BROKER_NAME = 'embedded-rhiot-broker'

    private BrokerService brokerService

    @Override
    void start() {
        brokerService = new BrokerService()
        brokerService.brokerName = DEFAULT_BROKER_NAME
        boolean isMqttEnabled = booleanProperty('MQTT_ENABLED', true)
        if(isMqttEnabled) {
            int mqttPort = Properties.intProperty('MQTT_PORT', 1883)
            brokerService.addConnector("mqtt://0.0.0.0:${mqttPort}")
        }
        brokerService.start()
    }

    @Override
    void stop() {
        brokerService.stop()
    }

    @Override
    int order() {
        1000
    }

    static String mqtt(String topic) {
        int port = Properties.intProperty('MQTT_PORT', 1883)
        "paho:${topic}?brokerUrl=tcp://localhost:${port}"
    }

    static String mqttJmsBridge(String topic) {
        if(!registry().containsKey('jmsConnectionFactory')) {
            def jmsConnectionFactory = new CachingConnectionFactory(new ActiveMQConnectionFactory("vm:${DEFAULT_BROKER_NAME}"))
            registry().put('jmsConnectionFactory', jmsConnectionFactory)
        }
        "jms:topic:${topic}?connectionFactory=#jmsConnectionFactory"
    }


}