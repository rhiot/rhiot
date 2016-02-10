package io.rhiot.component.kura.cloudclient;

import com.google.common.truth.Truth;
import io.rhiot.component.kura.cloud.KuraCloudConstants;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.eclipse.kura.KuraException;
import org.eclipse.kura.message.KuraPayload;
import org.junit.Test;

import java.util.Random;

import static io.rhiot.component.kura.cloudclient.KuraCloudClientConstants.*;

public class CamelCloudClientTest extends CamelTestSupport {

    Random random = new Random();

    CamelCloudClient cloudClient;

    @EndpointInject(uri = "mock:test")
    MockEndpoint mockEndpoint;

    KuraPayload kuraPayload;

    int qos = random.nextInt();

    int priority = random.nextInt();

    @Override
    protected void doPostSetup() throws Exception {
        cloudClient = new CamelCloudClient(context, "applicationId");
        kuraPayload = new KuraPayload();
        kuraPayload.setBody("foo".getBytes());
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start").to("mock:test");
            }
        };
    }

    @Test
    public void shouldPassMessageId() throws KuraException, InterruptedException {
        mockEndpoint.setExpectedCount(1);
        cloudClient.publish("direct:start", kuraPayload, 0, true, 0);
        mockEndpoint.assertIsSatisfied();
        int messageId = mockEndpoint.getExchanges().get(0).getIn().getHeader(CAMEL_KURA_CLOUD_MESSAGEID, int.class);
        Truth.assertThat(messageId).isGreaterThan(0);
    }

    @Test
    public void shouldPassQos() throws KuraException, InterruptedException {
        mockEndpoint.expectedHeaderReceived(CAMEL_KURA_CLOUD_QOS, qos);
        cloudClient.publish("direct:start", kuraPayload, qos, true, priority);
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    public void shouldPassRetain() throws KuraException, InterruptedException {
        mockEndpoint.expectedHeaderReceived(CAMEL_KURA_CLOUD_RETAIN, true);
        cloudClient.publish("direct:start", kuraPayload, qos, true, 0);
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    public void shouldPassPriority() throws KuraException, InterruptedException {
        mockEndpoint.expectedHeaderReceived(CAMEL_KURA_CLOUD_PRIORITY, priority);
        cloudClient.publish("direct:start", kuraPayload, 0, true, priority);
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    public void shouldPassDefaultPriority() throws KuraException, InterruptedException {
        mockEndpoint.expectedHeaderReceived(CAMEL_KURA_CLOUD_PRIORITY, 5);
        cloudClient.publish("direct:start", kuraPayload, 0, true);
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    public void shouldSubscribe() throws KuraException, InterruptedException {
        cloudClient.subscribe("direct:subscribe", qos);
        template.sendBody("direct:subscribe", "foo");
        String received = consumer.receiveBody("seda:applicationId", String.class);
        Truth.assertThat(received).isEqualTo("foo");
    }

    @Test
    public void shouldUnsubscribe() throws KuraException, InterruptedException {
        cloudClient.subscribe("seda:subscribe", qos);
        cloudClient.unsubscribe("seda:subscribe");
        template.sendBody("seda:subscribe", "foo");
        String received = consumer.receiveBodyNoWait("seda:applicationId", String.class);
        Truth.assertThat(received).isNull();
    }

    @Test
    public void shouldPassQosToSubscribed() throws KuraException, InterruptedException {
        cloudClient.subscribe("direct:subscribe", qos);
        template.sendBody("direct:subscribe", "foo");
        Exchange received = consumer.receive("seda:applicationId");
        Truth.assertThat(received.getIn().getHeader(CAMEL_KURA_CLOUD_QOS)).isEqualTo(qos);
    }

}
