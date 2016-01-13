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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rhiot.cloudplatform.encoding.spi.PayloadEncoding;
import org.apache.camel.NoTypeConversionAvailableException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.qpid.amqp_1_0.jms.Destination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class ServiceBinding extends RouteBuilder {

    // Static collaborators

    private static final Logger LOG = LoggerFactory.getLogger(ServiceBinding.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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
            String channel = exchange.getIn().getHeader("JMSDestination", Destination.class).getAddress();
            String rawChannel = channel.substring(channel.lastIndexOf('/') + 1);
            String[] channelParts = rawChannel.split("\\.");
            String service = channelParts[0];
            String operation = channelParts[1];
            exchange.setProperty(TARGET_PROPERTY, "bean:" + service + "?method=" + operation + "&multiParameterArray=true");

            List<Object> arguments = new LinkedList<>(asList(channelParts).subList(2, channelParts.length));
            byte[] incomingPayload = exchange.getIn().getBody(byte[].class);
            if (incomingPayload != null && incomingPayload.length > 0) {
                Object payload = payloadEncoding.decode(incomingPayload);
                arguments.add(payload);
            }

            Class beanType = getContext().getRegistry().lookupByName(service).getClass();
            Method operationMethod = asList(beanType.getDeclaredMethods()).stream().
                    filter(method -> method.getName().equals(operation)).findAny().get();

            exchange.getIn().setBody(convertArguments(arguments, operationMethod));
        }).toD(format("${property.%s}", TARGET_PROPERTY)).process(it -> it.getIn().setBody(payloadEncoding.encode(it.getIn().getBody())));
    }

    // Helpers

    protected List<?> convertArguments(List<?> arguments, Method operation) {
        Class[] parameterTypes = operation.getParameterTypes();
        List<Object> convertedArguments = new ArrayList<>(arguments.size());
        for (int i = 0; i < arguments.size(); i++) {
            try {
                convertedArguments.add(getContext().getTypeConverter().mandatoryConvertTo(parameterTypes[i], arguments.get(i)));
            } catch (NoTypeConversionAvailableException e) {
                convertedArguments.add(OBJECT_MAPPER.convertValue(arguments.get(i), parameterTypes[i]));
            }
        }
        return convertedArguments;
    }

}
