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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rhiot.cloudplatform.encoding.spi.PayloadEncoding;
import org.apache.camel.ProducerTemplate;

import java.util.HashMap;
import java.util.Map;

public class IoTConnector {

    private final PayloadEncoding payloadEncoding;

    private final ProducerTemplate producerTemplate;

    public IoTConnector(PayloadEncoding payloadEncoding, ProducerTemplate producerTemplate) {
        this.payloadEncoding = payloadEncoding;
        this.producerTemplate = producerTemplate;
    }

    public void toBus(String channel) {
        producerTemplate.sendBody("amqp:" + channel, null);
    }

    public void toBus(String channel, Object payload) {
        producerTemplate.sendBody("amqp:" + channel, payloadEncoding.encode(payload));
    }

    public void toBusAndWait(String channel) {
        byte[] busResponse = producerTemplate.requestBody("amqp:" + channel, null, byte[].class);
        payloadEncoding.decode(busResponse);
    }

    public void toBusAndWait(String channel, Object payload) {
        byte[] busResponse = producerTemplate.requestBody("amqp:" + channel, payloadEncoding.encode(payload), byte[].class);
        payloadEncoding.decode(busResponse);
    }

    public <T> T fromBus(String channel, Class<T> responseType, Header... headers) {
        Map<String, Object> collectedHeaders = new HashMap<>();
        for(Header header : headers) {
            collectedHeaders.put(header.key(), header.value());
        }
        byte[] busResponse = producerTemplate.requestBodyAndHeaders("amqp:" + channel, null, collectedHeaders, byte[].class);
        Object decodedResponse = payloadEncoding.decode(busResponse);
        if(decodedResponse != null && responseType.isAssignableFrom(decodedResponse.getClass())) {
           return (T) decodedResponse;
        } else {
            return new ObjectMapper().convertValue(decodedResponse, responseType);
        }
    }

    public  <T> T fromBus(String channel, Object payload, Class<T> responseType, Header... headers) {
        Map<String, Object> collectedHeaders = new HashMap<>();
        for(Header header : headers) {
            collectedHeaders.put(header.key(), header.value());
        }
        byte[] busResponse = producerTemplate.requestBodyAndHeaders("amqp:" + channel, payloadEncoding.encode(payload), collectedHeaders, byte[].class);
        return (T) payloadEncoding.decode(busResponse);
    }

}