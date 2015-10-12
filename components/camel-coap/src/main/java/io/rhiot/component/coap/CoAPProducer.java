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
package io.rhiot.component.coap;

import java.net.URI;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultProducer;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The coap producer.
 */
public class CoAPProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(CoAPProducer.class);
    private CoAPEndpoint endpoint;
    private CoapClient client;

    public CoAPProducer(CoAPEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;

    }

    public CoAPProducer(CoAPEndpoint endpoint, CoapClient client) {
        super(endpoint);
        this.endpoint = endpoint;
        this.client = client;
    }

    public void process(Exchange exchange) throws Exception {

        CoapResponse response = null;

        Code method = extractMethod(exchange);

        int mediaType = extractMediaType(exchange);

        if (client == null) {
            client = createClient(exchange);
        } else {
            client.setURI(createURI(exchange).toString());
        }

        client.setTimeout(extractTimeOut(exchange));

        switch (method) {
        case GET:
            response = client.get();
            break;
        case DELETE:
            response = client.delete();
            break;
        case POST:
            byte[] bodyPost = exchange.getIn().getBody(byte[].class);
            response = client.post(bodyPost, mediaType);
            break;
        case PUT:
            byte[] bodyPut = exchange.getIn().getBody(byte[].class);
            response = client.put(bodyPut, mediaType);
            break;

        }

        if (response != null) {
            populateResponse(exchange, response);
        }
    }

    private CoapClient createClient(Exchange exchange) {
        CoapClient ret = null;

        ret = new CoapClient(createURI(exchange));

        return ret;
    }

    private URI createURI(Exchange exchange) {
        URI ret = exchange.getIn().getHeader(CoAPConstants.COAP_URI, URI.class);
        // Always use CoAP mediaType from header else default from endpoint
        if (ret == null) {
            ret = endpoint.getCoapUri();
        }

        return ret;
    }

    private int extractMediaType(Exchange exchange) {
        Integer ret = exchange.getIn().getHeader(CoAPConstants.COAP_MEDIA_TYPE, Integer.class);
        // Always use CoAP mediaType from header else default from endpoint
        if (ret == null) {
            ret = endpoint.getCoapMediaType();
        }

        return ret;
    }

    private long extractTimeOut(Exchange exchange) {
        Long ret = exchange.getIn().getHeader(CoAPConstants.COAP_TIMEOUT, Long.class);
        // Always use CoAP timeOut from header else default from endpoint
        if (ret == null) {
            ret = endpoint.getCoapTimeout();
        }

        return ret;
    }

    private Code extractMethod(Exchange exchange) {
        Code ret = exchange.getIn().getHeader(CoAPConstants.COAP_METHOD, Code.class);
        // Always use CoAP method from header else default from endpoint
        if (ret == null) {
            ret = endpoint.getCoapMethod();
        }
        if (ret == null) {
            Object body = exchange.getIn().getBody();
            if (body == null) {
                ret = Code.GET;
            } else {
                ret = Code.POST;
            }
        }
        return ret;
    }

    private void populateResponse(Exchange exchange, CoapResponse response) {

        Message answer = exchange.getOut();

        answer.setHeader(CoAPConstants.COAP_RESPONSE_CODE, response.getCode());
        answer.setHeader(CoAPConstants.COAP_RESPONSE_OPTIONS, response.getOptions());
        answer.setBody(response.getPayload());
    }
}
