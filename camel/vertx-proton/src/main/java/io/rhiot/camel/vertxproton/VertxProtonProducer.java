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

import com.google.common.collect.ImmutableMap;
import io.vertx.proton.ProtonConnection;
import io.vertx.proton.ProtonSender;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.qpid.proton.amqp.messaging.AmqpValue;
import org.apache.qpid.proton.amqp.messaging.ApplicationProperties;
import org.apache.qpid.proton.amqp.messaging.Section;
import org.apache.qpid.proton.message.Message;

import java.util.concurrent.CountDownLatch;

import static io.rhiot.camel.vertxproton.VertxProtonConstants.CAMEL_VERTX_PROTON_REPLYTO;
import static io.vertx.proton.ProtonHelper.message;
import static io.vertx.proton.ProtonHelper.tag;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.camel.ExchangePattern.InOut;

public class VertxProtonProducer extends DefaultProducer {

    public VertxProtonProducer(VertxProtonEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        boolean isInOut = exchange.getPattern() == InOut;
        CountDownLatch responseReceived = new CountDownLatch(1);

        getEndpoint().getProtonClient().connect(getEndpoint().addressParser().host(), getEndpoint().addressParser().port(), result -> {
            if (result.succeeded()) {
                ProtonConnection connection = result.result().open();
                String path = getEndpoint().addressParser().path();
                ProtonSender sender = connection.createSender(path).open();

                Message message = message();
                String replyTo = getEndpoint().getReplyToGenerationStrategy().generateReplyTo(exchange, path);
                if (isInOut) {
                    message.setReplyTo(replyTo);
                    message.setApplicationProperties(new ApplicationProperties(
                            ImmutableMap.of(CAMEL_VERTX_PROTON_REPLYTO, replyTo)
                    ));
                }
                Object body = exchange.getIn().getBody();
                message.setBody(new AmqpValue(body));
                sender.send(tag("m1"), message, delivery -> {
                    log.debug("Message has been delivered to path {}.", path);
                });

                if (isInOut) {
                    connection.createReceiver(replyTo)
                            .handler((delivery, msg) -> {
                                Section responseBody = msg.getBody();
                                if (responseBody instanceof AmqpValue) {
                                    AmqpValue amqpValue = (AmqpValue) responseBody;
                                    Object responsePayload = amqpValue.getValue();
                                    log.debug("Received response {} from path {}.", responsePayload, replyTo);
                                    exchange.getOut().setBody(responsePayload);
                                    responseReceived.countDown();
                                }
                            })
                            .flow(10)  // Prefetch up to 10 messages. The client will replenish credit as deliveries are settled.
                            .open();
                }
            }

        });
        if (isInOut) {
            try {
                responseReceived.await(30, SECONDS);
            } catch (InterruptedException e) {
                getEndpoint().getExceptionHandler().handleException("Timed out while waiting for response.", e);
            }
        }
    }

    // Getters & setters

    @Override
    public VertxProtonEndpoint getEndpoint() {
        return (VertxProtonEndpoint) super.getEndpoint();
    }

}
