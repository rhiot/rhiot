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
package io.rhiot.cloudplatform.camel.protonj;

import io.vertx.core.Vertx;
import io.vertx.proton.ProtonClient;
import io.vertx.proton.ProtonConnection;
import io.vertx.proton.ProtonSender;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.qpid.proton.amqp.messaging.AmqpValue;
import org.apache.qpid.proton.message.Message;

import static io.vertx.proton.ProtonHelper.message;
import static io.vertx.proton.ProtonHelper.tag;

public class ProtonjProducer extends DefaultProducer {

    public ProtonjProducer(ProtonjEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Vertx vertx = getEndpoint().getVertx();

        Object body = exchange.getIn().getBody();
        String fullAddress = getEndpoint().getAddress().replaceFirst("//", "");
        int i = fullAddress.indexOf('/');
        String path = i > 0 ? fullAddress.substring(i) : "";
        String path2 = path.replaceFirst("/", "");
        String coord = fullAddress.replaceFirst("amqp:", "");
        coord = coord.replaceFirst("/.*", "");
        String[] coords = coord.split(":");

        ProtonClient client = ProtonClient.create(vertx);
        client.connect(coords[0], Integer.parseInt(coords[1]), res -> {
            if (res.succeeded()) {
                ProtonConnection connection = res.result();
                ProtonSender sender = connection.createSender(path2);

                Message message = message("Hello World from client");
                message.setBody(new AmqpValue(body));

                // Can optionally add an openHandler or sendQueueDrainHandler
                // to await remote sender open completing or credit to send being
                // granted. But here we will just buffer the send immediately.
                sender.open();
                System.out.println("Sending message to server");
                sender.send(tag("m1"), message, delivery -> {
                    System.out.println("The message was received by the server");
                });
            } else {
                res.cause().printStackTrace();
            }
        });
    }

    @Override
    public ProtonjEndpoint getEndpoint() {
        return (ProtonjEndpoint) super.getEndpoint();
    }

}
