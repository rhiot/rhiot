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
import java.util.*;

import static io.rhiot.cloudplatform.service.binding.Camels.convert;
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

    protected final PayloadEncoding payloadEncoding;

    // Member configuration

    protected final String serviceChannel;

    // Constructors

    public ServiceBinding(PayloadEncoding payloadEncoding, String serviceChannel) {
        this.serviceChannel = serviceChannel;
        this.payloadEncoding = payloadEncoding;
    }

    @Override
    public void configure() throws Exception {
        String fromChannel = format("amqp:%s.>", serviceChannel);
        LOG.debug("Starting route consuming from channel: {}", fromChannel);

        from(fromChannel).process(exchange -> {
            Message message = exchange.getIn();

            String channel = message.getHeader("JMSDestination", Destination.class).getAddress();
            String rawChannel = channel.substring(channel.lastIndexOf('/') + 1);
            String[] channelParts = rawChannel.split("\\.");
            String service = channelParts[0];
            String operation = channelParts[1];
            exchange.setProperty(TARGET_PROPERTY, "bean:" + service + "?method=" + operation + "&multiParameterArray=true");

            List<Object> arguments = new LinkedList<>(asList(channelParts).subList(2, channelParts.length));

            for(Map.Entry<String, Object> header : message.getHeaders().entrySet()) {
                if(header.getKey().startsWith("RHIOT_ARG")) {
                    arguments.add(header.getValue());
                }
            }

            byte[] incomingPayload = message.getBody(byte[].class);
            if (incomingPayload != null && incomingPayload.length > 0) {
                Object payload = payloadEncoding.decode(incomingPayload);
                arguments.add(payload);
            }

            Class beanType = getContext().getRegistry().lookupByName(service).getClass();
            Method operationMethod = asList(beanType.getDeclaredMethods()).stream().
                    filter(method -> method.getName().equals(operation)).findAny().get();

            message.setBody(convert(getContext(), arguments, operationMethod.getParameterTypes()));
        }).toD(format("${property.%s}", TARGET_PROPERTY)).process(it -> it.getIn().setBody(payloadEncoding.encode(it.getIn().getBody())));
    }

}
