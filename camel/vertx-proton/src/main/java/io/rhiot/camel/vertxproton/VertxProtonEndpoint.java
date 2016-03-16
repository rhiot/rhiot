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
package io.rhiot.camel.vertxproton;

import io.vertx.core.Vertx;
import io.vertx.proton.ProtonClient;
import io.vertx.proton.ProtonConnection;
import io.vertx.proton.ProtonSender;
import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.qpid.proton.amqp.messaging.AmqpValue;
import org.apache.qpid.proton.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.vertx.proton.ProtonHelper.message;
import static io.vertx.proton.ProtonHelper.tag;

public class VertxProtonEndpoint extends DefaultEndpoint {

    private final static Logger LOG = LoggerFactory.getLogger(VertxProtonEndpoint.class);

    private Vertx vertx;

    private ProtonClient protonClient;

    private String address;

    private AmqpAddress addressParser;

    private ReplyToGenerationStrategy replyToGenerationStrategy;

    public VertxProtonEndpoint(String endpointUri, String address, Component component) {
        super(endpointUri, component);
        this.address = address;
        this.addressParser = new AmqpAddress(address);
    }

    @Override
    public Producer createProducer() throws Exception {
        return new VertxProtonProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return addressParser().isServer() ?
                new VertxProtonServerConsumer(this, processor) :
                new VertxProtonClientConsumer(this, processor);
    }

    // Helpers

    public ProtonSender sender(ProtonConnection protonConnection, String path) {
        return protonConnection.createSender(path).open();
    }

    public void send(ProtonConnection protonConnection, String path, Object payload) {
        ProtonSender sender = null;
        try {
            sender = sender(protonConnection, path);
            Message message = message();
            message.setBody(new AmqpValue(payload));
            sender.send(tag("m1"), message, delivery -> {
                LOG.debug("Message has been delivered to path {}.", path);
            });
        } finally {
            if(sender != null) {
                sender.close();
            }
        }
    }

    // Read-only getters

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public VertxProtonComponent getComponent() {
        return (VertxProtonComponent) super.getComponent();
    }

    public AmqpAddress addressParser() {
        return addressParser;
    }

    // Getters & setters

    public Vertx getVertx() {
        if(vertx != null) {
            return vertx;
        }

        if(getComponent().getVertx() != null) {
            vertx = getComponent().getVertx();
        } else {
            vertx = Vertx.vertx();
        }
        return vertx;
    }

    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }

    public ProtonClient getProtonClient() {
        if(protonClient != null) {
            return protonClient;
        }

        if(getComponent().getProtonClient() != null) {
            protonClient = getComponent().getProtonClient();
        } else {
            protonClient = ProtonClient.create(getVertx());
        }
        return protonClient;
    }

    public void setProtonClient(ProtonClient protonClient) {
        this.protonClient = protonClient;
    }

    public String getAddress() {
        if(address != null && !address.isEmpty()) {
            return address;
        } else if(getComponent().getAddress() != null) {
            return getComponent().getAddress();
        }
        throw new IllegalStateException("No AMQP address specified.");
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ReplyToGenerationStrategy getReplyToGenerationStrategy() {
        if(replyToGenerationStrategy != null) {
            return replyToGenerationStrategy;
        }

        if(getComponent().getReplyToGenerationStrategy() != null) {
            replyToGenerationStrategy = getComponent().getReplyToGenerationStrategy();
        } else {
            replyToGenerationStrategy = new UuidReplyToGenerationStrategy();
        }
        return replyToGenerationStrategy;
    }

    public void setReplyToGenerationStrategy(ReplyToGenerationStrategy replyToGenerationStrategy) {
        this.replyToGenerationStrategy = replyToGenerationStrategy;
    }

}
