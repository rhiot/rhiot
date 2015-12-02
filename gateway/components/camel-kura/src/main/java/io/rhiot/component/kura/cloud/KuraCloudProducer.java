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

import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultProducer;
import org.eclipse.kura.cloud.CloudClient;
import org.eclipse.kura.message.KuraPayload;
import org.eclipse.kura.system.SystemService;

public class KuraCloudProducer extends DefaultProducer {

    private KuraCloudEndpoint endpoint = null;
    private CloudClient cloudClient = null;

    public KuraCloudProducer(KuraCloudEndpoint endpoint, CloudClient cloudClient) {
        super(endpoint);
        this.endpoint = endpoint;
        this.cloudClient = cloudClient;
    }

    protected String resolveTopic(Message message) {
        if (message.getHeaders().containsKey(KuraCloudConstants.CAMEL_KURA_CLOUD_TOPIC)) {
            return message.getHeader(KuraCloudConstants.CAMEL_KURA_CLOUD_TOPIC, String.class);
        } else {
            return endpoint.getTopic();
        }
    }

    protected int resolveQos(Message message) {
        if (message.getHeaders().containsKey(KuraCloudConstants.CAMEL_KURA_CLOUD_QOS)) {
            return message.getHeader(KuraCloudConstants.CAMEL_KURA_CLOUD_QOS, Integer.class);
        } else {
            return endpoint.getQos();
        }
    }

    protected boolean resolveRetain(Message message) {
        if (message.getHeaders().containsKey(KuraCloudConstants.CAMEL_KURA_CLOUD_RETAIN)) {
            return message.getHeader(KuraCloudConstants.CAMEL_KURA_CLOUD_RETAIN, Boolean.class);
        } else {
            return endpoint.isRetain();
        }
    }

    protected boolean resolveControl(Message message) {
        if (message.getHeaders().containsKey(KuraCloudConstants.CAMEL_KURA_CLOUD_CONTROL)) {
            return message.getHeader(KuraCloudConstants.CAMEL_KURA_CLOUD_CONTROL, Boolean.class);
        } else {
            return endpoint.isControl();
        }
    }

    protected boolean resolveIncludeDeviceId(Message message) {
        if (message.getHeaders().containsKey(KuraCloudConstants.CAMEL_KURA_CLOUD_INCLUDE_DEVICEID)) {
            return message.getHeader(KuraCloudConstants.CAMEL_KURA_CLOUD_INCLUDE_DEVICEID, Boolean.class);
        } else {
            return endpoint.isControl();
        }
    }

    protected String resolveDeviceId(Exchange exchange, boolean includedeviceId) {
        String ret = null;

        if (includedeviceId) {
            Set<SystemService> systemServiceSet = exchange.getContext().getRegistry().findByType(SystemService.class);

            SystemService systemService = null;
            if (systemServiceSet.size() == 1) {
                for (SystemService systemServiceIt : systemServiceSet) {
                    systemService = systemServiceIt;
                }
            } else {
                throw new IllegalArgumentException("Unable to retrieve a SystemService");
            }
            ret = systemService.getSerialNumber();

            if (ret == null || ret.length() == 0) {
                throw new IllegalArgumentException("deviceId must be non-null and non-empty");
            }

        }
        return ret;
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        Message in = exchange.getIn();
        Object body = in.getBody();

        String topic = resolveTopic(in);
        int qos = resolveQos(in);
        int priority = resolveQos(in);
        boolean retain = resolveRetain(in);
        boolean control = resolveControl(in);
        boolean includedeviceId = resolveIncludeDeviceId(in);
        String deviceId = resolveDeviceId(exchange, includedeviceId);

        if (body != null) {

            if (control) {
                if (deviceId == null) {
                    cloudClient.controlPublish(topic, (KuraPayload) body, qos, retain, priority);
                } else {
                    if (body instanceof KuraPayload) {
                        cloudClient.controlPublish(deviceId, topic, (KuraPayload) body, qos, retain, priority);
                    } else if (body instanceof byte[]) {
                        cloudClient.controlPublish(deviceId, topic, (byte[]) body, qos, retain, priority);
                    } else {
                        cloudClient.controlPublish(deviceId, topic, in.getBody(byte[].class), qos, retain, priority);
                    }
                }
            } else {
                if (body instanceof KuraPayload) {
                    cloudClient.publish(topic, (KuraPayload) body, qos, retain, priority);
                } else if (body instanceof byte[]) {
                    cloudClient.publish(topic, (byte[]) body, qos, retain, priority);
                } else {
                    cloudClient.publish(topic, in.getBody(byte[].class), qos, retain, priority);
                }
            }
        }
    }

    @Override
    public KuraCloudEndpoint getEndpoint() {
        return (KuraCloudEndpoint) super.getEndpoint();
    }

}
