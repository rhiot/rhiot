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
package io.rhiot.cloudplatform.service.binding;

import io.rhiot.cloudplatform.encoding.spi.PayloadEncoding;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.qpid.amqp_1_0.jms.impl.QueueImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.rhiot.cloudplatform.service.binding.OperationBinding.operationBinding;
import static java.lang.String.format;

/**
 * Service binding is a general purpose backend service which can be used to bind communication coming through IoT
 * Connector to the POJO service bean.
 */
public class ServiceBinding extends RouteBuilder {

    // Static collaborators

    private static final Logger LOG = LoggerFactory.getLogger(ServiceBinding.class);

    // Constants

    private static final String TARGET_PROPERTY = "target";

    // Member collaborators

    protected final PayloadEncoding payloadEncoding;

    // Member configuration

    protected final String serviceChannel;

    // Constructors

    public ServiceBinding(PayloadEncoding payloadEncoding, String serviceChannel) {
        this.payloadEncoding = payloadEncoding;
        this.serviceChannel = serviceChannel;
    }

    @Override
    public void configure() throws Exception {
        for(int i = 0; i < 20; i++) {
            String fromChannel = format("amqp:%s.>", serviceChannel);
            LOG.debug("Starting route consuming from channel: {}", fromChannel);

            from(fromChannel).process(exchange -> {
                Message message = exchange.getIn();
                String channel = message.getHeader("JMSDestination", QueueImpl.class).getQueueName();
                byte[] incomingPayload = message.getBody(byte[].class);
                OperationBinding operationBinding = operationBinding(payloadEncoding, channel, incomingPayload, message.getHeaders(), getContext().getRegistry());
                exchange.setProperty(TARGET_PROPERTY, "bean:" + operationBinding.service() + "?method=" + operationBinding.operation() + "&multiParameterArray=true");
                exchange.setProperty("RETURN_TYPE", operationBinding.operationMethod().getReturnType());

                message.setBody(new Camels().convert(getContext(), operationBinding.arguments(), operationBinding.operationMethod().getParameterTypes()));
            }).toD(format("${property.%s}", TARGET_PROPERTY)).process(it -> {
                Class returnType = it.getProperty("RETURN_TYPE", Class.class);
                if (Void.TYPE == returnType) {
                    it.getIn().setBody(null);
                }
                if (byte[].class == returnType) {
                    it.getIn().setBody(it.getIn().getBody());
                    log.debug("Binary return type. No conversion performed.");
                } else {
                    it.getIn().setBody(payloadEncoding.encode(it.getIn().getBody()));
                }
            });
        }
    }

}
