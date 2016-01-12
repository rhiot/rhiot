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
package io.rhiot.cloudplatform.runtime.spring;

import org.apache.camel.component.amqp.AMQPComponent;
import org.apache.qpid.amqp_1_0.jms.impl.ConnectionFactoryImpl;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.net.MalformedURLException;

import static io.rhiot.utils.Properties.intProperty;
import static io.rhiot.utils.Properties.stringProperty;

/**
 * Allows to communicate with the Cloud Platform by loading modules (protocol adapters, services, payload encodings and
 * so forth).
 */
@SpringBootApplication(scanBasePackages = "io.rhiot")
public class CloudPlatform {

    private ConfigurableApplicationContext applicationContext;

    // Lifecycle

    public CloudPlatform start(String... args) {
        System.setProperty("camel.springboot.typeConversion", "false");
        applicationContext = new SpringApplicationBuilder(CloudPlatform.class).web(false).build().run(args);
        return this;
    }

    public CloudPlatform stop() {
        applicationContext.close();
        return this;
    }

    // Getters

    public ConfigurableApplicationContext applicationContext() {
        return applicationContext;
    }

    // Main entry point

    public static void main(String[] args) {
        new CloudPlatform().start(args);
    }

    @Bean
    AMQPComponent amqp() throws MalformedURLException {
        // Starting from Camel 2.16.1 (due to the CAMEL-9204) replace the code below with:
        // AMQPComponent.amqp10Component(uri)
        String amqpBrokerUrl = stringProperty("AMQP_SERVICE_HOST", "localhost");
        int amqpBrokerPort = intProperty("AMQP_SERVICE_PORT", 5672);
        ConnectionFactoryImpl connectionFactory = ConnectionFactoryImpl.createFromURL("amqp://guest:guest@" + amqpBrokerUrl + ":" + amqpBrokerPort);
        connectionFactory.setTopicPrefix("topic://");
        return new AMQPComponent(connectionFactory);
    }

}