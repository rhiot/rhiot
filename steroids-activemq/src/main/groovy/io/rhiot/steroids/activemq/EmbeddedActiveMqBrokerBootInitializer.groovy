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

import io.rhiot.bootstrap.BootInitializer
import io.rhiot.bootstrap.Bootstrap
import io.rhiot.bootstrap.BootstrapAware
import org.apache.activemq.broker.BrokerService

import static io.rhiot.utils.Properties.booleanProperty
import static io.rhiot.utils.Properties.intProperty
import static io.rhiot.utils.Properties.stringProperty

public class EmbeddedActiveMqBrokerBootInitializer implements BootInitializer, BootstrapAware {

    // Constants

    public static final String DEFAULT_BROKER_NAME = 'embedded-rhiot-broker'

    // Collaborators

    private Bootstrap bootstrap

    private BrokerService brokerService

    // Life-cycle

    @Override
    void start() {
        def brokerUrl = externalBrokerUrl()
        if(brokerUrl == null) {
            brokerService = new BrokerService()
            brokerService.brokerName = DEFAULT_BROKER_NAME

            boolean isMqttEnabled = booleanProperty('MQTT_ENABLED', true)
            if (isMqttEnabled) {
                brokerService.addConnector("mqtt://0.0.0.0:${mqttPort()}")
            }

            boolean isAmqpEnabled = booleanProperty('AMQP_ENABLED', true)
            if (isAmqpEnabled) {
                brokerService.addConnector("amqp://0.0.0.0:${amqpPort()}?transport.transformer=jms")
            }

            brokerService.start()
        }
    }

    @Override
    void stop() {
        if(brokerService != null) {
            brokerService.stop()
        }
    }

    @Override
    int order() {
        1000
    }

    // Camel DSL

    static String mqtt(String topic) {
        "paho:${topic}?brokerUrl=tcp://localhost:${mqttPort()}"
    }

    static String mqttJmsBridge(String topic) {
        "jms:topic:${topic}?connectionFactory=#jmsConnectionFactory"
    }

    static String amqp(String channel) {
        "amqp:${channel}"
    }

    static String amqpByPrefix(String channelPrefix) {
        "amqp:${channelPrefix}.>"
    }

    static String amqpJmsBridge(String channel) {
        "jms:${channel}?connectionFactory=#jmsConnectionFactory"
    }

    // Helpers

    static String externalBrokerUrl() {
        stringProperty('BROKER_URL')
    }

    static int mqttPort() {
        intProperty('MQTT_PORT', 1883)
    }

    static int amqpPort() {
        intProperty('AMQP_PORT', 5672)
    }

    @Override
    void bootstrap(Bootstrap bootstrap) {
        this.bootstrap = bootstrap
    }

}