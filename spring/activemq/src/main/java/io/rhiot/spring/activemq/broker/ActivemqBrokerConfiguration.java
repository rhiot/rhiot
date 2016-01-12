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
package io.rhiot.spring.activemq.broker;

import org.apache.activemq.broker.BrokerService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActivemqBrokerConfiguration {

    @Bean
    ActivemqBrokerProperties activemqBrokerProperties() {
        return new ActivemqBrokerProperties();
    }

    @ConditionalOnProperty(name = "spring.activemq.broker.enabled", havingValue = "true")
    @Bean(initMethod = "start", destroyMethod = "stop")
    BrokerService brokerService(ActivemqBrokerProperties brokerProperties) throws Exception {
        BrokerService broker = new BrokerService();
        broker.setBrokerName(brokerProperties.getName());
        if(brokerProperties.isAmqpEnabled()) {
            broker.addConnector("amqp://0.0.0.0:" + brokerProperties.getAmqpPort() + "?transport.transformer=jms");
        }
        if(brokerProperties.isMqttEnabled()) {
            broker.addConnector("mqtt://0.0.0.0:" + brokerProperties.getMqttPort());
        }
        return broker;
    }

}