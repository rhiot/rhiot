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

import com.google.common.truth.Truth;
import org.apache.activemq.broker.BrokerService;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.apache.camel.component.amqp.AMQPComponent.amqp10Component;

@Ignore
public class VertxProtonProducerInOutTest extends CamelTestSupport {

    @BeforeClass
    public static void beforeClass() throws Exception {
        BrokerService broker = new BrokerService();
        broker.setPersistent(false);
        broker.addConnector("amqp://0.0.0.0:10000");
        broker.start();
    }

    String destination = UUID.randomUUID().toString();

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        context.addComponent("amqp", amqp10Component("amqp://localhost:10000"));

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("amqp:" + destination).
                        setBody().constant("bar").
                        toD("amqp:${header.CamelVertxProton.replyTo}");
            }
        };
    }

    // Messages fixtures

    String message = randomUUID().toString();

    // Tests

    @Test
    public void shouldInOutOnBrokerQueue() throws InterruptedException {
        String receivedMessage = template.requestBody("vertx-proton:localhost:10000/" + destination, message, String.class);
        Truth.assertThat(receivedMessage).isEqualTo("bar");
    }

    @Test
    public void shouldInOutManyOnTheSameBrokerQueue() throws InterruptedException {
        String receivedMessage = template.requestBody("vertx-proton:localhost:10000/" + destination, message, String.class);
        Truth.assertThat(receivedMessage).isEqualTo("bar");
        receivedMessage = template.requestBody("vertx-proton:localhost:10000/" + destination, message, String.class);
        Truth.assertThat(receivedMessage).isEqualTo("bar");
        receivedMessage = template.requestBody("vertx-proton:localhost:10000/" + destination, message, String.class);
        Truth.assertThat(receivedMessage).isEqualTo("bar");
    }

}
