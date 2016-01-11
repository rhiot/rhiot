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
package io.rhiot.cloudplatform

import io.rhiot.bootstrap.BeanRegistry
import io.rhiot.bootstrap.BootModule
import io.rhiot.bootstrap.BootstrapAware
import io.rhiot.bootstrap.classpath.ClasspathMapBeanRegistry
import io.rhiot.utils.WithLogger
import org.apache.camel.component.amqp.AMQPComponent
import org.apache.qpid.amqp_1_0.jms.impl.ConnectionFactoryImpl
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

import static io.rhiot.bootstrap.classpath.ClasspathBeans.beans
import static io.rhiot.utils.Properties.intProperty
import static io.rhiot.utils.Properties.stringProperty
import static java.lang.Runtime.runtime;

/**
 * Starts up Steroids framework, scans the classpath for the initializers and runs the latter.
 */
@SpringBootApplication(scanBasePackages = "io.rhiot")
class CloudPlatform implements WithLogger {

    public static ConfigurableApplicationContext applicationContext

    // Members

    private final BeanRegistry beanRegistry

    private final def initializers = beans(BootModule.class).
            sort{ first, second -> first.order() - second.order()}.asImmutable()

    // Constructors

    CloudPlatform(BeanRegistry beanRegistry) {
        this.beanRegistry = makeBootstrapAware(beanRegistry)
    }

    CloudPlatform() {
        this(new ClasspathMapBeanRegistry())
    }

    // Lifecycle

    CloudPlatform start(String... args) {
        System.setProperty("camel.springboot.typeConversion", "false")
        applicationContext = new SpringApplicationBuilder(CloudPlatform.class).web(false).build().run(args)
        log().debug('Starting Steroids Bootstrap: {}', getClass().name)
        initializers.each {
            if(it instanceof BootstrapAware) {
                it.bootstrap(this)
            }
        }
        initializers.each { it.start() }
        this
    }

    CloudPlatform stop() {
        log().debug('Stopping Steroids Bootstrap: {}', getClass().name)
        initializers.reverse().each { it.stop() }
        applicationContext.close()
        this
    }

    BeanRegistry beanRegistry() {
        beanRegistry
    }

    def <T extends BootModule> T initializer(Class<T> type) {
        initializers.find { type.isAssignableFrom(it.class) }
    }

    // Bootstrap awareness

    def <T> T makeBootstrapAware(T object) {
        if(object instanceof BootstrapAware) {
            object.asType(BootstrapAware.class).bootstrap(this)
        }
        object
    }

    // Main entry point

    public static void main(String[] args) {
        def bootstrap = new CloudPlatform().start(args)
        runtime.addShutdownHook(new Thread(){
            @Override
            void run() {
                bootstrap.stop()
            }
        })
    }

    @Bean
    AMQPComponent amqp() {
        // Starting from Camel 2.16.1 (due to the CAMEL-9204) replace the code below with:
        // AMQPComponent.amqp10Component(uri)
        def amqpBrokerUrl = stringProperty('AMQP_SERVICE_HOST', 'localhost')
        def amqpBrokerPort = intProperty('AMQP_SERVICE_PORT', 5672)
        def connectionFactory = ConnectionFactoryImpl.createFromURL("amqp://guest:guest@${amqpBrokerUrl}:${amqpBrokerPort}")
        connectionFactory.topicPrefix = 'topic://'
        new AMQPComponent(connectionFactory)
    }

}