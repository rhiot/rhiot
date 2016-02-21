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
package io.rhiot.cloudplatform.connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rhiot.cloudplatform.encoding.spi.PayloadEncoding;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static io.rhiot.cloudplatform.connector.Header.arguments;

public class IoTConnector {

    private static final Logger LOG = LoggerFactory.getLogger(IoTConnector.class);

    // Collaborators

    private final PayloadEncoding payloadEncoding;

    private final ProducerTemplate producerTemplate;

    // Constructors

    public IoTConnector(PayloadEncoding payloadEncoding, ProducerTemplate producerTemplate) {
        this.payloadEncoding = payloadEncoding;
        this.producerTemplate = producerTemplate;
    }

    // Connector channels API

    public void toBus(String channel, Object payload, Header... headers) {
        producerTemplate.sendBodyAndHeaders("amqp:" + channel, encodedPayload(payload), arguments(headers));
    }

    public void toBus(String channel, Header... headers) {
        toBus(channel, null, headers);
    }

    public void toBusAndWait(String channel) {
        byte[] busResponse = producerTemplate.requestBody("amqp:" + channel, null, byte[].class);
        payloadEncoding.decode(busResponse);
    }

    public void toBusAndWait(String channel, Object payload, Header... headers) {
        Map<String, Object> collectedHeaders = new HashMap<>();
        for(Header header : headers) {
            collectedHeaders.put(header.key(), header.value());
        }
        byte[] busResponse = producerTemplate.requestBodyAndHeaders("amqp:" + channel, encodedPayload(payload), collectedHeaders, byte[].class);
        payloadEncoding.decode(busResponse);
    }

    public <T> T fromBus(String channel, Class<T> responseType, Header... headers) {
        byte[] busResponse = producerTemplate.requestBodyAndHeaders("amqp:" + channel, null, arguments(headers), byte[].class);
        return decodedPayload(busResponse, responseType);
    }

    public <T> T pollChannel(String channel, Class<T> responseType) {
        byte[] busResponse = producerTemplate.getCamelContext().createConsumerTemplate().receiveBody("amqp:" + channel, byte[].class);
        return decodedPayload(busResponse, responseType);
    }

    public  <T> T fromBus(String channel, Object payload, Class<T> responseType, Header... headers) {
        byte[] busResponse = producerTemplate.requestBodyAndHeaders("amqp:" + channel, encodedPayload(payload), arguments(headers), byte[].class);
        return decodedPayload(busResponse, responseType);
    }

    // Helpers

    private Object encodedPayload(Object payload) {
        LOG.debug("About to encode payload: {}", payload);
        if(payload == null || payload instanceof InputStream || payload.getClass() == byte[].class) {
            LOG.debug("Payload is null or binary - encoding skipped.");
            return payload;
        }
        return payloadEncoding.encode(payload);
    }

    private <T> T decodedPayload(byte[] payload, Class<T> responseType) {
        Object decodedResponse = payload;
        if(responseType != byte[].class) {
            decodedResponse = payloadEncoding.decode(payload);
        }
        if(decodedResponse != null && responseType.isAssignableFrom(decodedResponse.getClass())) {
            return (T) decodedResponse;
        } else {
            return new ObjectMapper().convertValue(decodedResponse, responseType);
        }
    }

}