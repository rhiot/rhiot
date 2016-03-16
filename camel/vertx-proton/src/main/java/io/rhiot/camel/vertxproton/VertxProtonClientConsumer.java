/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rhiot.camel.vertxproton;

import io.vertx.proton.ProtonConnection;
import org.apache.camel.*;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.qpid.proton.amqp.messaging.AmqpValue;
import org.apache.qpid.proton.amqp.messaging.Section;

import static io.rhiot.camel.vertxproton.VertxProtonConstants.CAMEL_VERTX_PROTON_PATH;
import static io.rhiot.camel.vertxproton.VertxProtonConstants.CAMEL_VERTX_PROTON_REPLYTO;
import static org.apache.camel.ExchangePattern.InOut;
import static org.apache.camel.builder.ExchangeBuilder.anExchange;

public class VertxProtonClientConsumer extends DefaultConsumer {

    public VertxProtonClientConsumer(VertxProtonEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        AmqpAddress address = getEndpoint().addressParser();
        getEndpoint().getProtonClient().connect(getEndpoint().addressParser().host(), getEndpoint().addressParser().port(), result -> {
            if (result.succeeded()) {
                ProtonConnection connection = result.result().open();
                connection.createReceiver(address.path())
                        .handler((delivery, msg) -> {
                            Section body = msg.getBody();
                            if (body instanceof AmqpValue) {
                                AmqpValue amqpValue = (AmqpValue) body;
                                ExchangeBuilder exchangeBuilder = anExchange(getEndpoint().getCamelContext()).
                                        withBody(amqpValue.getValue()).
                                        withHeader(CAMEL_VERTX_PROTON_PATH, address.path());
                                if (msg.getApplicationProperties() != null) {
                                    for (Object key : msg.getApplicationProperties().getValue().keySet()) {
                                        exchangeBuilder.withHeader((String) key, msg.getApplicationProperties().getValue().get(key));
                                    }
                                }
                                if (msg.getReplyTo() != null) {
                                    exchangeBuilder.
                                            withPattern(InOut).
                                            withHeader(CAMEL_VERTX_PROTON_REPLYTO, msg.getReplyTo());
                                }
                                Exchange exchange = exchangeBuilder.build();
                                exchange.setFromEndpoint(getEndpoint());
                                try {
                                    getProcessor().process(exchange);
                                    if (msg.getReplyTo() != null) {
                                        getEndpoint().send(connection, msg.getReplyTo(), exchange.getIn().getBody());
                                    }
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }).flow(10).open();
            } else {
                getExceptionHandler().handleException("Cannot connect to AMQP server.", result.cause());
            }
        });
    }

    @Override
    public VertxProtonEndpoint getEndpoint() {
        return (VertxProtonEndpoint) super.getEndpoint();
    }

}
