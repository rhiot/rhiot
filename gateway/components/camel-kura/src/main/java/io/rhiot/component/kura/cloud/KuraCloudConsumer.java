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
package io.rhiot.component.kura.cloud;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.impl.DefaultConsumer;
import org.eclipse.kura.cloud.CloudClient;
import org.eclipse.kura.cloud.CloudClientListener;
import org.eclipse.kura.message.KuraPayload;

public class KuraCloudConsumer extends DefaultConsumer implements CloudClientListener {

    private CloudClient cloudClient;

    public KuraCloudConsumer(Endpoint endpoint, Processor processor, CloudClient cloudClient) {
        super(endpoint, processor);
        this.cloudClient = cloudClient;
    }

    @Override
    protected void doStart() throws Exception {
        cloudClient.addCloudClientListener(this);
        log.trace("Start Listening CloudClient");
    }

    @Override
    protected void doStop() throws Exception {
        cloudClient.removeCloudClientListener(this);
        log.trace("Stop Listening CloudClient");
    }

    @Override
    public void onControlMessageArrived(String deviceId, String appTopic, KuraPayload msg, int qos, boolean retain) {
        onInternalMessageArrived(deviceId, appTopic, msg, qos, retain, true);
    }

    @Override
    public void onMessageArrived(String deviceId, String appTopic, KuraPayload msg, int qos, boolean retain) {
        onInternalMessageArrived(deviceId, appTopic, msg, qos, retain, false);
    }

    private void onInternalMessageArrived(String deviceId, String appTopic, KuraPayload msg, int qos, boolean retain,
            boolean control) {
        Exchange exchange = ExchangeBuilder.anExchange(getEndpoint().getCamelContext()).withBody(msg)
                .withHeader(KuraCloudConstants.CAMEL_KURA_CLOUD_TOPIC, appTopic)
                .withHeader(KuraCloudConstants.CAMEL_KURA_CLOUD_DEVICEID, deviceId)
                .withHeader(KuraCloudConstants.CAMEL_KURA_CLOUD_QOS, qos)
                .withHeader(KuraCloudConstants.CAMEL_KURA_CLOUD_CONTROL, control)
                .withHeader(KuraCloudConstants.CAMEL_KURA_CLOUD_RETAIN, retain).build();
        exchange.setFromEndpoint(getEndpoint());
        try {
            getProcessor().process(exchange);
        } catch (Exception e) {
            exchange.setException(e);
        } finally {
            // log exception if an exception occurred and was not handled
            if (exchange.getException() != null) {
                getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
            }
        }
    }

    @Override
    public void onConnectionLost() {
        log.trace("Do Nothing");
    }

    @Override
    public void onConnectionEstablished() {
        log.trace("Do Nothing");
    }

    @Override
    public void onMessageConfirmed(int messageId, String appTopic) {
        log.trace("Do Nothing");
    }

    @Override
    public void onMessagePublished(int messageId, String appTopic) {
        log.trace("Do Nothing");
    }

}
