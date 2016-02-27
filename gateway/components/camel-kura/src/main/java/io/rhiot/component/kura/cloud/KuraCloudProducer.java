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

import static io.rhiot.component.kura.cloud.KuraCloudConstants.CAMEL_KURA_CLOUD_PRIORITY;
import static io.rhiot.component.kura.cloud.KuraCloudConstants.CAMEL_KURA_CLOUD_QOS;
import static io.rhiot.component.kura.cloud.KuraCloudConstants.CAMEL_KURA_CLOUD_TOPIC;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultProducer;
import org.eclipse.kura.cloud.CloudClient;
import org.eclipse.kura.message.KuraPayload;

public class KuraCloudProducer extends DefaultProducer {

    private KuraCloudEndpoint endpoint;

    // Visible for testing
    CloudClient cloudClient;

    public KuraCloudProducer(KuraCloudEndpoint endpoint, CloudClient cloudClient) {
        super(endpoint);
        this.endpoint = endpoint;
        this.cloudClient = cloudClient;
    }

    protected boolean resolveRetain(Message message) {
        Boolean ret = message.getHeader(KuraCloudConstants.CAMEL_KURA_CLOUD_RETAIN, Boolean.class);
        if (ret == null) {
            ret = endpoint.isRetain();
        }
        return ret;
    }

    protected boolean resolveControl(Message message) {
        Boolean ret = message.getHeader(KuraCloudConstants.CAMEL_KURA_CLOUD_CONTROL, Boolean.class);
        if (ret == null) {
            ret = endpoint.isControl();
        }
        return ret;
    }

    protected String resolveDeviceId(Message message) {
        String ret = message.getHeader(KuraCloudConstants.CAMEL_KURA_CLOUD_DEVICEID, String.class);
        if (ret == null) {
            ret = endpoint.getDeviceId();
        }
        return ret;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Message in = exchange.getIn();
        Object body = in.getBody();

        String topic = firstNotNull(in.getHeader(CAMEL_KURA_CLOUD_TOPIC, String.class), getEndpoint().getTopic());
        int qos = firstNotNull(in.getHeader(CAMEL_KURA_CLOUD_QOS, Integer.class), getEndpoint().getQos());
        int priority = firstNotNull(in.getHeader(CAMEL_KURA_CLOUD_PRIORITY, Integer.class),
                getEndpoint().getPriority());
        ;
        boolean retain = resolveRetain(in);
        boolean control = resolveControl(in);
        String deviceId = resolveDeviceId(in);

        if (body != null) {
            if (control) {
                if (deviceId != null) {
                    if (body instanceof KuraPayload) {
                        cloudClient.controlPublish(deviceId, topic, (KuraPayload) body, qos, retain, priority);
                    } else if (body instanceof byte[]) {
                        cloudClient.controlPublish(deviceId, topic, (byte[]) body, qos, retain, priority);
                    } else {
                        cloudClient.controlPublish(deviceId, topic, in.getBody(String.class).getBytes(), qos, retain,
                                priority);
                    }
                } else {
                    cloudClient.controlPublish(topic, (KuraPayload) body, qos, retain, priority);
                }
            } else {
                if (body instanceof KuraPayload) {
                    cloudClient.publish(topic, (KuraPayload) body, qos, retain, priority);
                } else if (body instanceof byte[]) {
                    cloudClient.publish(topic, (byte[]) body, qos, retain, priority);
                } else {
                    cloudClient.publish(topic, in.getBody(String.class).getBytes(), qos, retain, priority);
                }
            }
        }
    }

    @Override
    public KuraCloudEndpoint getEndpoint() {
        return (KuraCloudEndpoint) super.getEndpoint();
    }

    // Helpers

    private <T> T firstNotNull(T first, T second) {
        return first != null ? first : second;
    }

}
