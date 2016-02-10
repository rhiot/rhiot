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
package io.rhiot.component.kura.camelcloud;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.kura.KuraErrorCode;
import org.eclipse.kura.KuraException;
import org.eclipse.kura.cloud.CloudClient;
import org.eclipse.kura.cloud.CloudClientListener;
import org.eclipse.kura.message.KuraPayload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static io.rhiot.component.kura.camelcloud.KuraCloudClientConstants.*;
import static org.apache.camel.ServiceStatus.Started;

public class CamelCloudClient implements CloudClient {

    private final CamelContext camelContext;

    private final ProducerTemplate producerTemplate;

    private final String applicationId;

    public CamelCloudClient(CamelContext camelContext, String applicationId) {
        this.camelContext = camelContext;
        this.producerTemplate = camelContext.createProducerTemplate();
        this.applicationId = applicationId;
    }

    @Override
    public String getApplicationId() {
        return applicationId;
    }

    @Override
    public void release() {
    }

    @Override
    public boolean isConnected() {
        return camelContext.getStatus() == Started;
    }

    @Override
    public int publish(String topic, KuraPayload kuraPayload, int qos, boolean retain) throws KuraException {
        return publish(topic, kuraPayload, qos, retain, 5);
    }

    @Override
    public int publish(String topic, KuraPayload kuraPayload, int qos, boolean retain, int priority) throws KuraException {
        return doPublish(false, null, topic, kuraPayload, qos, retain, priority);
    }

    @Override
    public int publish(String s, byte[] bytes, int i, boolean b, int i1) throws KuraException {
        KuraPayload kuraPayload = new KuraPayload();
        kuraPayload.setBody(bytes);
        return publish(s, kuraPayload, i, b);
    }

    @Override
    public int controlPublish(String topic, KuraPayload payload, int i, boolean retain, int priority) throws KuraException {
        return doPublish(true, null, topic, payload, i, retain, priority);
    }

    @Override
    public int controlPublish(String s, String s1, KuraPayload kuraPayload, int i, boolean b, int i1) throws KuraException {
        return doPublish(true, s, s1, kuraPayload, i, b, i1);
    }

    @Override
    public int controlPublish(String deviceId, String topic, byte[] payload, int qos, boolean b, int priority) throws KuraException {
        KuraPayload kuraPayload = new KuraPayload();
        kuraPayload.setBody(payload);
        return doPublish(true, deviceId, topic, kuraPayload, qos, b, priority);
    }

    @Override
    public void subscribe(String topic, int qos) throws KuraException {
        doSubscribe(false, topic, qos);
    }

    @Override
    public void controlSubscribe(String topic, int qos) throws KuraException {
        doSubscribe(true, topic, qos);
    }

    @Override
    public void unsubscribe(String topic) throws KuraException {
        try {
            camelContext.stopRoute(topic);
            camelContext.removeRoute(topic);
        } catch (Exception e) {
            throw new KuraException(KuraErrorCode.INTERNAL_ERROR, e);
        }
    }

    @Override
    public void controlUnsubscribe(String topic) throws KuraException {
        unsubscribe(topic);
    }

    @Override
    public void addCloudClientListener(CloudClientListener cloudClientListener) {

    }

    @Override
    public void removeCloudClientListener(CloudClientListener cloudClientListener) {

    }

    @Override
    public List<Integer> getUnpublishedMessageIds() throws KuraException {
        return null;
    }

    @Override
    public List<Integer> getInFlightMessageIds() throws KuraException {
        return null;
    }

    @Override
    public List<Integer> getDroppedInFlightMessageIds() throws KuraException {
        return null;
    }

    // Helpers

    private int doPublish(boolean isControl, String deviceId, String topic, KuraPayload kuraPayload, int qos, boolean retain, int priority) throws KuraException {
        int kuraMessageId = Math.abs(new Random().nextInt());

        Map<String, Object> headers = new HashMap<>();
        headers.put(CAMEL_KURA_CLOUD_CONTROL, isControl);
        headers.put(CAMEL_KURA_CLOUD_MESSAGEID, kuraMessageId);
        headers.put(CAMEL_KURA_CLOUD_DEVICEID, deviceId);
        headers.put(CAMEL_KURA_CLOUD_QOS, qos);
        headers.put(CAMEL_KURA_CLOUD_RETAIN, retain);
        headers.put(CAMEL_KURA_CLOUD_PRIORITY, priority);

        producerTemplate.sendBodyAndHeaders(topic, kuraPayload, headers);
        return kuraMessageId;
    }

    private void doSubscribe(final boolean isControl, final String topic, final int qos) throws KuraException {
        try {
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(topic).
                            routeId(topic).
                            setHeader(CAMEL_KURA_CLOUD_CONTROL, constant(isControl)).
                            setHeader(CAMEL_KURA_CLOUD_QOS, constant(qos)).
                            to("seda:" + applicationId);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
