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
package io.rhiot.cloudplatform.camel.vertxproton;

import io.vertx.core.Vertx;
import io.vertx.proton.ProtonConnection;
import io.vertx.proton.ProtonServer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.qpid.proton.amqp.messaging.AmqpValue;
import org.apache.qpid.proton.amqp.messaging.Section;
import org.apache.qpid.proton.message.Message;

import static io.vertx.proton.ProtonHelper.message;
import static io.vertx.proton.ProtonHelper.tag;

public class ProtonjConsumer extends DefaultConsumer {

    public ProtonjConsumer(ProtonjEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }

    @Override
    protected void doStart() throws Exception {
        AmqpAddress address = getEndpoint().addressParser();

        if (address.host().startsWith("~")) {
            Vertx vertx = getEndpoint().getVertx();
            ProtonServer server = ProtonServer.create(vertx)
                    .connectHandler(this::serverHandler)
                    .listen(address.port(), (res) -> {
                        if (res.succeeded()) {
                            System.out.println("Listening on: " + res.result().actualPort());
                        } else {
                            res.cause().printStackTrace();
                        }
                    });
        } else {
            ProtonConnection connection = getEndpoint().protonConnection();
            connection.createReceiver(address.path())
                    .handler((delivery, msg) -> {
                        Section body = msg.getBody();
                        if (body instanceof AmqpValue) {
                            AmqpValue amqpValue = (AmqpValue) body;
                            Exchange exchange = ExchangeBuilder.anExchange(getEndpoint().getCamelContext()).withBody(amqpValue.getValue()).build();
                            try {
                                getProcessor().process(exchange);
                                if (msg.getReplyTo() != null) {
                                    getEndpoint().send(msg.getReplyTo(), exchange.getIn().getBody());
                                }
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    })
                    .flow(10)  // Prefetch up to 10 messages. The client will replenish credit as deliveries are settled.
                    .open();

        }
        super.doStart();
    }

    @Override
    public ProtonjEndpoint getEndpoint() {
        return (ProtonjEndpoint) super.getEndpoint();
    }

    private void serverHandler(ProtonConnection connection) {
        connection.openHandler(res -> {
            System.out.println("Client connected: " + connection.getRemoteContainer());
        }).closeHandler(c -> {
            System.out.println("Client closing amqp connection: " + connection.getRemoteContainer());
            connection.close();
            connection.disconnect();
        }).disconnectHandler(c -> {
            System.out.println("Client socket disconnected: " + connection.getRemoteContainer());
            connection.disconnect();
        }).open();
        connection.sessionOpenHandler(session -> session.open());

        connection.receiverOpenHandler(receiver -> {
            receiver
                    .setTarget(receiver.getRemoteTarget())
                    .handler((delivery, msg) -> {

                        String address = msg.getAddress();
                        if (address == null) {
                            address = receiver.getRemoteTarget().getAddress();
                        }

                        Section body = msg.getBody();
                        if (body instanceof AmqpValue) {
                            AmqpValue bodyx = (AmqpValue) body;
                            Exchange exchange = ExchangeBuilder.anExchange(getEndpoint().getCamelContext()).withBody(bodyx.getValue()).build();
                            try {
                                getProcessor().process(exchange);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    })
                    .flow(10)
                    .open();
        });

        connection.senderOpenHandler(sender -> {
            System.out.println("Sending to client from: " + sender.getRemoteSource().getAddress());
            sender.setSource(sender.getRemoteSource()).open();

            Message m = message("Hello World from Server!");
            sender.send(tag("m1"), m, delivery -> {
                System.out.println("The message was received by the client.");
            });
        });


    }


}
