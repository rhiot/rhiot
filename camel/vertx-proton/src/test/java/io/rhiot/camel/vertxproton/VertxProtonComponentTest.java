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

import com.google.common.collect.ImmutableMap;
import com.google.common.truth.Truth;
import org.apache.activemq.broker.BrokerService;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static io.rhiot.camel.vertxproton.VertxProtonConstants.CAMEL_VERTX_PROTON_PATH;
import static java.util.UUID.randomUUID;
import static org.apache.camel.component.amqp.AMQPComponent.amqp10Component;
import static org.apache.camel.test.AvailablePortFinder.getNextAvailable;

public class VertxProtonComponentTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:peer2peer")
    MockEndpoint mockEndpoint;

    @EndpointInject(uri = "mock:mytopic")
    MockEndpoint topicMockEndpoint;

    int peerConsumerPort = getNextAvailable();

    @BeforeClass
    public static void beforeClass() throws Exception {
        BrokerService broker = new BrokerService();
        broker.setBrokerName(randomUUID().toString());
        broker.setPersistent(false);
        broker.addConnector("amqp://0.0.0.0:9999");
        broker.start();
    }

    String destination = UUID.randomUUID().toString();

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        context.addComponent("amqp", amqp10Component("amqp://localhost:9999"));

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("vertx-proton:amqp://~0.0.0.0:" + peerConsumerPort).to("mock:peer2peer");

                from("amqp:topic:mytopic").to("mock:mytopic");

                from("vertx-proton:amqp://0.0.0.0:9999/inout").setBody().constant("bar");
            }
        };
    }

    // Messages fixtures

    String message = randomUUID().toString();

    // Peer2peer tests

    @Test
    public void shouldReceivePeer2PeerMessage() throws InterruptedException {
        mockEndpoint.expectedBodiesReceived(message);
        template.sendBody("vertx-proton:amqp://0.0.0.0:" + peerConsumerPort, message);
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    public void shouldReceivePeer2PeerMessages() throws InterruptedException {
        mockEndpoint.expectedBodiesReceived(message, message);
        template.sendBody("vertx-proton:amqp://0.0.0.0:" + peerConsumerPort, message);
        template.sendBody("vertx-proton:amqp://0.0.0.0:" + peerConsumerPort, message);
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    public void shouldReceivePeer2PeerMapMessage() throws InterruptedException {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put(message, message);
        mockEndpoint.expectedBodiesReceived(payload);
        template.sendBody("vertx-proton:amqp://0.0.0.0:" + peerConsumerPort, payload);
        mockEndpoint.assertIsSatisfied();
    }

    // Peer2broker tests

    @Test
    public void shouldSendMessageToBrokerQueue() throws InterruptedException {
        template.sendBody("vertx-proton:localhost:9999/" + destination, message);
        String receivedMessage = consumer.receiveBody("amqp:" + destination, String.class);
        Truth.assertThat(receivedMessage).isEqualTo(message);
    }

    @Test
    public void shouldReceiveMessageFromBrokerQueue() throws InterruptedException {
        template.sendBody("amqp:" + destination, message);
        String receivedMessage = consumer.receiveBody("vertx-proton:localhost:9999/" + destination, String.class);
        Truth.assertThat(receivedMessage).isEqualTo(message);
    }

    @Test
    public void shouldInOutOnBrokerQueue() throws InterruptedException {
        String receivedMessage = template.requestBody("vertx-proton:localhost:9999/inout", "foo", String.class);
        Truth.assertThat(receivedMessage).isEqualTo("bar");
    }

    @Test
    public void shouldSendMessageToBrokerTopic() throws InterruptedException {
        // Given
        topicMockEndpoint.expectedBodiesReceived(message);

        // When
        template.sendBody("vertx-proton:amqp://localhost:9999/topic://mytopic", message);

        // Then
        topicMockEndpoint.assertIsSatisfied();
    }

    // Data types tests

    @Test
    public void shouldReceiveMapFromJmsBridge() throws InterruptedException {
        Map<String, String> mapMessage = ImmutableMap.of("foo", "bar");
        template.sendBody("amqp:" + destination, mapMessage);
        Map receivedMessage = consumer.receiveBody("vertx-proton:localhost:9999/" + destination, Map.class);
        Truth.assertThat(receivedMessage).isEqualTo(mapMessage);
    }

    @Test
    public void shouldReceivePathFromJmsBridge() throws InterruptedException {
        template.sendBody("amqp:" + destination, message);
        Exchange exchange = consumer.receive("vertx-proton:localhost:9999/" + destination);
        Truth.assertThat(exchange.getIn().getHeader(CAMEL_VERTX_PROTON_PATH)).isEqualTo(destination);
    }

}
