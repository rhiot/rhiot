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

import com.pubnub.api.Pubnub;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.apache.camel.util.ObjectHelper;

@UriEndpoint(scheme = "pubnub", title = "PubNub", syntax = "pubnub:endpointType:channel", consumerClass = PubNubConsumer.class, label = "iot,messaging")
public class PubNubEndpoint extends DefaultEndpoint {
    private Pubnub pubnub;

    @UriPath(enums = "pubsub,presens")
    @Metadata(required = "true")
    private PubNubEndpointType endpointType;

    @UriPath(description = "The channel used for subscribing/publishing events")
    @Metadata(required = "true")
    private String channel;

    @UriParam()
    @Metadata(required = "true")
    private String publisherKey;

    @UriParam()
    @Metadata(required = "true")
    private String subscriberKey;

    @UriParam()
    private String secretKey;

    @UriParam(defaultValue = "false")
    private boolean ssl;

    @UriParam(description = "The uuid identifying the connection")
    private String uuid;

    @UriParam
    private String operation;

    public PubNubEndpoint(String uri, PubNubComponent component, PubNubEndpointType endpointType) {
        super(uri, component);
        this.endpointType = endpointType;
    }

    public Producer createProducer() throws Exception {
        return new PubNubProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        return new PubNubConsumer(this, processor);
    }

    public boolean isSingleton() {
        return true;
    }

    public PubNubEndpointType getEndpointType() {
        return endpointType;
    }

    public String getPublisherKey() {
        return publisherKey;
    }

    public void setPublisherKey(String publisherKey) {
        this.publisherKey = publisherKey;
    }

    public String getSubscriberKey() {
        return subscriberKey;
    }

    public void setSubscriberKey(String subscriberKey) {
        this.subscriberKey = subscriberKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public Pubnub getPubnub() {
        return pubnub;
    }

    void setPubnub(Pubnub pubnub) {
        this.pubnub = pubnub;
    }

    @Override
    protected void doStart() throws Exception {
        this.pubnub = getPubnub() != null ? getPubnub() : getInstance();
        super.doStart();
    }

    private Pubnub getInstance() {
        if (this.pubnub != null) {
            return this.pubnub;
        }
        Pubnub answer = null;
        if (ObjectHelper.isNotEmpty(getSecretKey())) {
            if (isSsl()) {
                answer = new Pubnub(getPublisherKey(), getSubscriberKey(), getSecretKey(), true);
            } else {
                answer = new Pubnub(getPublisherKey(), getSubscriberKey(), getSecretKey());
            }
        } else if (isSsl()) {
            answer = new Pubnub(getPublisherKey(), getSubscriberKey(), true);
        } else {
            answer = new Pubnub(getPublisherKey(), getSubscriberKey());
        }
        if (ObjectHelper.isNotEmpty(getUuid())) {
            answer.setUUID(getUuid());
        } else {
            String autoUUID = answer.uuid();
            setUuid(autoUUID);
            answer.setUUID(autoUUID);
        }
        return answer;
    }
}
