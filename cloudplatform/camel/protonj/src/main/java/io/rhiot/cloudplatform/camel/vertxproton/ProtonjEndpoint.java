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
package io.rhiot.cloudplatform.camel.vertxproton;

import io.vertx.core.Vertx;
import io.vertx.proton.ProtonClient;
import io.vertx.proton.ProtonConnection;
import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ProtonjEndpoint extends DefaultEndpoint {

    private Vertx vertx;

    private ProtonClient protonClient;

    private String address;

    private AmqpAddress addressParser;

    private ReplyToGenerationStrategy replyToGenerationStrategy;

    private ProtonConnection protonConnection;

    private CountDownLatch connectionResolved = new CountDownLatch(1);

    public ProtonjEndpoint(String endpointUri, String address, Component component) {
        super(endpointUri, component);
        this.address = address;
        this.addressParser = new AmqpAddress(address);
    }

    @Override
    public Producer createProducer() throws Exception {
        return new ProtonjProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return new ProtonjConsumer(this, processor);
    }

    // Life-cycle

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        if(!addressParser.isServer()) {
            getProtonClient().connect(addressParser().host(), addressParser.port(), result -> {
                if (result.succeeded()) {
                    protonConnection = result.result().open();
                    connectionResolved.countDown();
                } else {
                    connectionResolved.countDown();
                    getExceptionHandler().handleException("Cannot connect to AMQP server.", result.cause());
                }
            });
        }
        Thread.sleep(2000);
    }

    // Read-only getters

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public ProtonjComponent getComponent() {
        return (ProtonjComponent) super.getComponent();
    }

    public AmqpAddress addressParser() {
        return addressParser;
    }

    public ProtonConnection protonConnection() {
        try {
            connectionResolved.await(10, SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return protonConnection;
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
