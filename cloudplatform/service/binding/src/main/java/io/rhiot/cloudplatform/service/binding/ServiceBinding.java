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
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class ServiceBinding extends RouteBuilder {

    protected final String serviceChannel;

    @Autowired
    protected PayloadEncoding payloadEncoding;

    public ServiceBinding(String serviceChannel) {
        this.serviceChannel = serviceChannel;
    }

    @Override
    public void configure() throws Exception {
        from("amqp:" + serviceChannel + ".>").
                process( it -> {
                    String  channel = it.getIn().getHeader("JMSDestination", org.apache.qpid.amqp_1_0.jms.Destination.class).getAddress();
                    String rawChannel = channel.substring(channel.lastIndexOf('/') + 1);
                    String[] channelParts = rawChannel.split("\\.");
                    String service = channelParts[0];
                    String operation = channelParts[1];
                    it.setProperty("target", "bean:" + service + "?method=" + operation + "&multiParameterArray=true");

                    List<Object> arguments = new LinkedList<>();
                    for (int i = 2; i < channelParts.length; i++) {
                        arguments.add(channelParts[i]);
                    }
                    byte[] incomingPayload = it.getIn().getBody(byte[].class);
                    if (incomingPayload != null && incomingPayload.length > 0) {
                        Object payload = payloadEncoding.decode(incomingPayload);
                        arguments.add(payload);
                    }

                    Class beanType = getContext().getRegistry().lookupByName(service).getClass();
                    Method method = null;
                    for(Method m : beanType.getDeclaredMethods()) {
                        if(m.getName().equals(operation)) {
                            method = m;
                            break;
                        }
                    }
                    it.getIn().setBody(convertArguments(arguments, method));
                }).recipientList().exchangeProperty("target").
                process( it -> it.getIn().setBody(payloadEncoding.encode(it.getIn().getBody())));
    }

    // Helpers

    protected List<?> convertArguments(List<?> arguments, Method operation) {
        List convertedArguments = new LinkedList<>();
        for(int i = 0; i < arguments.size(); i++) {
            try {
                convertedArguments.add(getContext().getTypeConverter().mandatoryConvertTo(operation.getParameterTypes()[i], arguments.get(i)));
            } catch (NoTypeConversionAvailableException e) {
                convertedArguments.add(new ObjectMapper().convertValue(arguments.get(i), operation.getParameterTypes()[i]));
            }
        }
        return convertedArguments;
    }

}
