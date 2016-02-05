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

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.qpid.proton.amqp.messaging.AmqpValue;
import org.apache.qpid.proton.amqp.messaging.ApplicationProperties;
import org.apache.qpid.proton.amqp.messaging.Section;
import org.apache.qpid.proton.message.Message;
import org.apache.qpid.proton.messenger.Messenger;

public class ProtonjConsumer extends DefaultConsumer {

    public ProtonjConsumer(ProtonjEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }

    @Override
    protected void doStart() throws Exception {
        new Thread(){
            @Override
            public void run() {
                try {
                    // TODO support multiple address subscroption
                    Messenger messenger = getEndpoint().getMessenger();
                    messenger.subscribe(getEndpoint().getAddress());
                    while (true) {
                        messenger.recv();
                        while (messenger.incoming() > 0) {
                            Message msg = messenger.get();
                            Section body = msg.getBody();
                            AmqpValue amqpValue = (AmqpValue) body;
                            Exchange exchange = ExchangeBuilder.anExchange(getEndpoint().getCamelContext()).withBody(amqpValue.getValue()).build();
                            getProcessor().process(exchange);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        super.doStart();
    }

    @Override
    public ProtonjEndpoint getEndpoint() {
        return (ProtonjEndpoint) super.getEndpoint();
    }

}
