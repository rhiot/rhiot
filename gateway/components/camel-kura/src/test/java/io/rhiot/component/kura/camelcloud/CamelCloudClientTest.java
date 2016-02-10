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

import com.google.common.truth.Truth;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.eclipse.kura.KuraException;
import org.eclipse.kura.cloud.CloudClient;
import org.eclipse.kura.cloud.CloudService;
import org.eclipse.kura.message.KuraPayload;
import org.junit.Test;

import java.util.Random;

import static io.rhiot.component.kura.camelcloud.KuraCloudClientConstants.*;

public class CamelCloudClientTest extends CamelTestSupport {

    Random random = new Random();

    CamelCloudService cloudService;

    CloudClient cloudClient;

    @EndpointInject(uri = "mock:test")
    MockEndpoint mockEndpoint;

    KuraPayload kuraPayload;

    int qos = random.nextInt();

    int priority = random.nextInt();

    @Override
    protected void doPostSetup() throws Exception {
        cloudService = new DefaultCamelCloudService(context);
        cloudService.registerBaseEndpoint("applicationId", "seda:%s");
        cloudClient = cloudService.newCloudClient("applicationId");
        kuraPayload = new KuraPayload();
        kuraPayload.setBody("foo".getBytes());
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("seda:start").to("mock:test");
            }
        };
    }

    // Tests

    @Test
    public void shouldPassMessageId() throws KuraException, InterruptedException {
        mockEndpoint.setExpectedCount(1);
        cloudClient.publish("start", kuraPayload, 0, true, 0);
        mockEndpoint.assertIsSatisfied();
        int messageId = mockEndpoint.getExchanges().get(0).getIn().getHeader(CAMEL_KURA_CLOUD_MESSAGEID, int.class);
        Truth.assertThat(messageId).isGreaterThan(0);
    }

    @Test
    public void shouldPassQos() throws KuraException, InterruptedException {
        mockEndpoint.expectedHeaderReceived(CAMEL_KURA_CLOUD_QOS, qos);
        cloudClient.publish("start", kuraPayload, qos, true, priority);
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    public void shouldPassRetain() throws KuraException, InterruptedException {
        mockEndpoint.expectedHeaderReceived(CAMEL_KURA_CLOUD_RETAIN, true);
        cloudClient.publish("start", kuraPayload, qos, true, 0);
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    public void shouldPassPriority() throws KuraException, InterruptedException {
        mockEndpoint.expectedHeaderReceived(CAMEL_KURA_CLOUD_PRIORITY, priority);
        cloudClient.publish("start", kuraPayload, 0, true, priority);
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    public void shouldPassDefaultPriority() throws KuraException, InterruptedException {
        mockEndpoint.expectedHeaderReceived(CAMEL_KURA_CLOUD_PRIORITY, 5);
        cloudClient.publish("start", kuraPayload, 0, true);
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    public void shouldPassControlFlag() throws KuraException, InterruptedException {
        mockEndpoint.expectedHeaderReceived(CAMEL_KURA_CLOUD_CONTROL, true);
        cloudClient.controlPublish("start", kuraPayload, qos, true, priority);
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    public void shouldNotPassControlFlag() throws KuraException, InterruptedException {
        mockEndpoint.expectedHeaderReceived(CAMEL_KURA_CLOUD_CONTROL, false);
        cloudClient.publish("start", kuraPayload, qos, true, priority);
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    public void shouldSubscribe() throws KuraException, InterruptedException {
        cloudClient.subscribe("subscribe", qos);
        template.sendBody("seda:subscribe", "foo");
        String received = consumer.receiveBody("seda:applicationId", String.class);
        Truth.assertThat(received).isEqualTo("foo");
    }

    @Test
    public void shouldUnsubscribe() throws KuraException, InterruptedException {
        cloudClient.subscribe("subscribe", qos);
        cloudClient.unsubscribe("subscribe");
        template.sendBody("seda:subscribe", "foo");
        String received = consumer.receiveBodyNoWait("seda:applicationId", String.class);
        Truth.assertThat(received).isNull();
    }

    @Test
    public void shouldPassQosToSubscribed() throws KuraException, InterruptedException {
        cloudClient.subscribe("subscribe", qos);
        template.sendBody("seda:subscribe", "foo");
        Exchange received = consumer.receive("seda:applicationId");
        Truth.assertThat(received.getIn().getHeader(CAMEL_KURA_CLOUD_QOS)).isEqualTo(qos);
    }

}
