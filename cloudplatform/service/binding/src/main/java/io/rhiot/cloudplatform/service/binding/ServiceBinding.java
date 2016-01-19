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
import org.apache.qpid.amqp_1_0.jms.Destination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

import static io.rhiot.cloudplatform.service.binding.Camels.convert;
import static io.rhiot.cloudplatform.service.binding.OperationBinding.operationBinding;
import static java.lang.String.format;
import static java.util.Arrays.asList;

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

    protected final DestinationBinding destinationBinding;

    protected final PayloadEncoding payloadEncoding;

    // Member configuration

    protected final String serviceChannel;

    // Constructors

    public ServiceBinding(DestinationBinding destinationBinding, PayloadEncoding payloadEncoding, String serviceChannel) {
        this.destinationBinding = destinationBinding;
        this.payloadEncoding = payloadEncoding;
        this.serviceChannel = serviceChannel;
    }

    @Override
    public void configure() throws Exception {
        String fromChannel = destinationBinding.destination(serviceChannel);
        LOG.debug("Starting route consuming from channel: {}", fromChannel);

        from(fromChannel).process(exchange -> {
            Message message = exchange.getIn();
            String channel = message.getHeader("JMSDestination", Destination.class).getAddress();
            byte[] incomingPayload = message.getBody(byte[].class);
            OperationBinding operationBinding = operationBinding(payloadEncoding, channel, incomingPayload, message.getHeaders());
            exchange.setProperty(TARGET_PROPERTY, "bean:" + operationBinding.service() + "?method=" + operationBinding.operation() + "&multiParameterArray=true");

            Class beanType = getContext().getRegistry().lookupByName(operationBinding.service()).getClass();
            LOG.debug("Detected service bean type {} for operation: {}", beanType, operationBinding);
            Method operationMethod = asList(beanType.getDeclaredMethods()).stream().
                    filter(method -> method.getName().equals(operationBinding.operation())).findAny().get();

            message.setBody(convert(getContext(), operationBinding.arguments(), operationMethod.getParameterTypes()));
        }).toD(format("${property.%s}", TARGET_PROPERTY)).process(it -> it.getIn().setBody(payloadEncoding.encode(it.getIn().getBody())));
    }

}
