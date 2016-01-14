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
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

class OperationBinding {

    private final String service;

    private final String operation;

    private final List<?> arguments;

    private OperationBinding(String service, String operation, List<?> arguments) {
        this.service = service;
        this.operation = operation;
        this.arguments = arguments;
    }

    static OperationBinding operationBinding(PayloadEncoding payloadEncoding, String channel, byte[] incomingPayload, Map<String, Object> headers) {
        String rawChannel = channel.substring(channel.lastIndexOf('/') + 1);
        String[] channelParts = rawChannel.split("\\.");
        String service = channelParts[0];
        String operation = channelParts[1];

        List<Object> arguments = new LinkedList<>(asList(channelParts).subList(2, channelParts.length));

        for(Map.Entry<String, Object> header : headers.entrySet()) {
            if(header.getKey().startsWith("RHIOT_ARG")) {
                arguments.add(header.getValue());
            }
        }

        if (incomingPayload != null && incomingPayload.length > 0) {
            Object payload = payloadEncoding.decode(incomingPayload);
            arguments.add(payload);
        }
        return new OperationBinding(service, operation, arguments);
    }

    public String service() {
        return service;
    }

    public String operation() {
        return operation;
    }

    public List<?> arguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}