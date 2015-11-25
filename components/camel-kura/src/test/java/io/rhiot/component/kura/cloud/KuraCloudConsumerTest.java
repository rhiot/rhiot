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

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.rhiot.component.kura.cloud.KuraCloudConstants;
import io.rhiot.component.kura.cloud.KuraCloudConsumer;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.eclipse.kura.KuraException;
import org.eclipse.kura.cloud.CloudClient;
import org.eclipse.kura.cloud.CloudClientListener;
import org.eclipse.kura.cloud.CloudService;
import org.eclipse.kura.message.KuraPayload;
import org.junit.BeforeClass;
import org.junit.Test;

public class KuraCloudConsumerTest extends CamelTestSupport {

    static CloudService cloudService = mock(CloudService.class);

    static CloudClient cloudClient = mock(CloudClient.class);

    @EndpointInject(uri = "mock:result")
    MockEndpoint mockResult;

    @EndpointInject(uri = "mock:result-control")
    MockEndpoint mockResultControl;

    @BeforeClass
    public static void before() throws KuraException {
        given(cloudService.newCloudClient(anyString())).willReturn(cloudClient);

    }

    @Test
    public void controlReceiveKuraPayloadToTopic() throws KuraException, InterruptedException {
        // Given
        KuraPayload kuraPayload = new KuraPayload();
        KuraCloudConsumer consumer = (KuraCloudConsumer) context().getRoute("kura-cloud-test-route-control")
                .getConsumer();

        // When
        consumer.onControlMessageArrived("deviceIdControl", "appTopicControl", kuraPayload, 0, true);

        // Then
        verify(cloudClient, atLeastOnce()).addCloudClientListener((CloudClientListener) anyObject());

        mockResultControl.expectedHeaderReceived(KuraCloudConstants.CAMEL_KURA_CLOUD_CONTROL, true);
        mockResultControl.expectedHeaderReceived(KuraCloudConstants.CAMEL_KURA_CLOUD_DEVICEID, "deviceIdControl");
        mockResultControl.expectedHeaderReceived(KuraCloudConstants.CAMEL_KURA_CLOUD_TOPIC, "appTopicControl");
        mockResultControl.setExpectedMessageCount(1);
        mockResultControl.assertIsSatisfied();
    }

    @Test
    public void receiveKuraPayloadToTopic() throws KuraException, InterruptedException {
        // Given
        KuraPayload kuraPayload = new KuraPayload();
        KuraCloudConsumer consumer = (KuraCloudConsumer) context().getRoute("kura-cloud-test-route").getConsumer();

        // When
        consumer.onMessageArrived("deviceId", "appTopic", kuraPayload, 0, true);

        // Then
        verify(cloudClient, atLeastOnce()).addCloudClientListener((CloudClientListener) anyObject());

        mockResult.expectedHeaderReceived(KuraCloudConstants.CAMEL_KURA_CLOUD_CONTROL, false);
        mockResult.expectedHeaderReceived(KuraCloudConstants.CAMEL_KURA_CLOUD_DEVICEID, "deviceId");
        mockResult.setExpectedMessageCount(1);
        mockResult.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                from("kura-cloud:app/topic").id("kura-cloud-test-route").to("mock:result");
                from("kura-cloud:app/topicControl").id("kura-cloud-test-route-control").to("mock:result-control");

            }
        };
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        registry.bind("cloudService", cloudService);
        return registry;
    }

}