/**
 * Licensed to the Camel Labs under one or more
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
package com.github.camellabs.component.pubnub;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.pubnub.api.Callback;
import com.pubnub.api.PubnubError;

import org.apache.camel.CamelException;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangeTimedOutException;
import org.apache.camel.impl.DefaultProducer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The PubNub producer.
 */
public class PubNubProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(PubNubProducer.class);
    private PubNubEndpoint endpoint;

    public PubNubProducer(PubNubEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    public void process(final Exchange exchange) throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        Callback callback = new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                LOG.trace("PubNub response {}", message);
                exchange.getIn().setHeader(PubNubConstants.CHANNEL, channel);
                if (exchange.getPattern().isOutCapable()) {
                    exchange.getOut().copyFrom(exchange.getIn());
                    exchange.getOut().setBody(message);
                }
                latch.countDown();
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                exchange.setException(new CamelException(error.toString()));
                latch.countDown();
            }
        };

        Operation operation = getOperation(exchange);
        LOG.trace("Executing {} operation", operation);
        switch (operation) {
        case PUBLISH: {
            String channel = exchange.getIn().getHeader(PubNubConstants.CHANNEL, String.class);
            channel = channel != null ? channel : endpoint.getChannel();
            Object body = exchange.getIn().getBody();
            LOG.trace("Sending message [{}] to channel [{}]", body, channel);
            if (body.getClass().isAssignableFrom(JSONObject.class)) {
                endpoint.getPubnub().publish(channel, (JSONObject)body, callback);
            } else if (body.getClass().isAssignableFrom(JSONArray.class)) {
                endpoint.getPubnub().publish(channel, (JSONArray)body, callback);
            } else {
                endpoint.getPubnub().publish(channel, exchange.getIn().getMandatoryBody(String.class), callback);
            }
            break;
        }
        case GET_HISTORY: {
            endpoint.getPubnub().history(endpoint.getChannel(), false, callback);
            break;
        }
        case GET_STATE: {
            endpoint.getPubnub().getState(endpoint.getChannel(), endpoint.getUuid(), callback);
            break;
        }
        case HERE_NOW: {
            endpoint.getPubnub().hereNow(endpoint.getChannel(), true, true, callback);
            break;
        }
        case SET_STATE: {
            JSONObject state = exchange.getIn().getMandatoryBody(JSONObject.class);
            String uuid = exchange.getIn().getHeader(PubNubConstants.UUID, String.class);
            endpoint.getPubnub().setState(endpoint.getChannel(), uuid != null ? uuid : endpoint.getUuid(), state, callback);
            break;
        }
        case WHERE_NOW: {
            String uuid = exchange.getIn().getHeader(PubNubConstants.UUID, String.class);
            endpoint.getPubnub().whereNow(uuid != null ? uuid : endpoint.getUuid(), callback);
            break;
        }
        default:
            throw new UnsupportedOperationException(operation.toString());
        }
        boolean done = latch.await(10, TimeUnit.SECONDS);
        if (!done) {
            exchange.setException(new ExchangeTimedOutException(exchange, 10 * 1000));
            // count down to indicate timeout
            latch.countDown();
        }
    }

    private Operation getOperation(Exchange exchange) {
        String operation = exchange.getIn().getHeader(PubNubConstants.OPERATION, String.class);
        if (operation == null) {
            operation = endpoint.getOperation();
        }
        return operation != null ? Operation.valueOf(operation) : Operation.PUBLISH;
    }

    private enum Operation {
        HERE_NOW, WHERE_NOW, GET_STATE, SET_STATE, GET_HISTORY, PUBLISH;
    }
}
