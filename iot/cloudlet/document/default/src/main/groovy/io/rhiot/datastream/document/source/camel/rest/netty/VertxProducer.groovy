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
package io.rhiot.datastream.document.source.camel.rest.netty

import io.rhiot.datastream.engine.JsonWithHeaders
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.eventbus.Message
import org.apache.camel.AsyncCallback
import org.apache.camel.AsyncProcessor
import org.apache.camel.Exchange
import org.apache.camel.util.AsyncProcessorHelper

class VertxProducer implements AsyncProcessor {

    private final Vertx vertx

    VertxProducer(Vertx vertx) {
        this.vertx = vertx
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        AsyncProcessorHelper.process(this, exchange);
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        JsonWithHeaders operation = exchange.getIn().getBody(JsonWithHeaders.class);
        vertx.eventBus().send("document", operation.getJson(), operation.deliveryOptions(), new Handler<AsyncResult<Message<Object>>>() {
            @Override
            public void handle(AsyncResult<Message<Object>> event) {
                exchange.getOut().setBody(event.result().body());
                callback.done(false);
            }
        });
        return false;
    }

}