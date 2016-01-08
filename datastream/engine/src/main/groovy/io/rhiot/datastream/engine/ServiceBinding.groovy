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
package io.rhiot.datastream.engine

import com.fasterxml.jackson.databind.ObjectMapper
import io.rhiot.datastream.engine.encoding.PayloadEncoding

import io.rhiot.bootstrap.BootstrapAware
import org.apache.camel.NoTypeConversionAvailableException
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.jms.JmsMessage
import org.springframework.beans.factory.annotation.Autowired

import java.lang.reflect.Method

import static io.rhiot.steroids.activemq.EmbeddedActiveMqBrokerBootInitializer.amqpByPrefix

class ServiceBinding extends RouteBuilder implements BootstrapAware {

    protected final String serviceChannel

    protected CloudPlatform bootstrap

    @Autowired
    PayloadEncoding payloadEncoding

    ServiceBinding(String serviceChannel) {
        this.serviceChannel = serviceChannel
    }

    @Override
    void configure() {
        if(payloadEncoding == null) {
            payloadEncoding = bootstrap.beanRegistry().bean(PayloadEncoding.class).get()
        }

        from(amqpByPrefix(serviceChannel)).
                process {
                    def channel = it.getIn(JmsMessage.class).jmsMessage.properties._to.toString()
                    def rawChannel = channel.substring(channel.lastIndexOf('/') + 1)
                    def channelParts = rawChannel.split(/\./)
                    def service = channelParts[0]
                    def operation = channelParts[1]
                    it.setProperty('target', "bean:${service}?method=${operation}&multiParameterArray=true")

                    def arguments = []
                    for(int i = 2; i < channelParts.length;i++) {
                        arguments.add(channelParts[i])
                    }
                    def incomingPayload = it.in.getBody(byte[].class)
                    if(incomingPayload != null && incomingPayload.length > 0) {
                        def payload = payloadEncoding.decode(incomingPayload)
                        arguments.add(payload)
                    }

                    def beanType = context.registry.lookupByName(service).getClass()
                    def beanOperation = beanType.declaredMethods.find{ it.name == operation }
                    it.in.body = convertArguments(arguments, beanOperation).toArray()
                }.recipientList().exchangeProperty('target').
                process{ it.in.body = payloadEncoding.encode(it.in.body) }
    }

    @Override
    void bootstrap(CloudPlatform bootstrap) {
        this.bootstrap = bootstrap
    }

    // Helpers

    protected List<?> convertArguments(List<?> arguments, Method operation) {
        def convertedArguments = []
        arguments.eachWithIndex{ argument, i ->
            try {
                convertedArguments << context.typeConverter.mandatoryConvertTo(operation.parameterTypes[i], arguments[i])
            } catch (NoTypeConversionAvailableException e) {
                convertedArguments << new ObjectMapper().convertValue(arguments[i], operation.parameterTypes[i])
            }
        }
        convertedArguments.asImmutable()
    }

}
