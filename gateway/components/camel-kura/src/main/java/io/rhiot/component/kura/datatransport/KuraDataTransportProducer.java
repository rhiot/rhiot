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
package io.rhiot.component.kura.datatransport;

import static io.rhiot.component.kura.datatransport.KuraDataTransportConstants.CAMEL_KURA_DATATRANSPORT_QOS;
import static io.rhiot.component.kura.datatransport.KuraDataTransportConstants.CAMEL_KURA_DATATRANSPORT_TOPIC;

import io.rhiot.component.kura.utils.KuraServiceFactory;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.eclipse.kura.data.DataTransportService;

public class KuraDataTransportProducer extends DefaultProducer {

    public KuraDataTransportProducer(KuraDataTransportEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        byte[] payload = exchange.getIn().getBody(byte[].class);

        DataTransportService dataTransportService = getEndpoint().getDataTransportService();

        if (dataTransportService == null) {
            dataTransportService = KuraServiceFactory.retrieveService(DataTransportService.class,
                    getEndpoint().getCamelContext().getRegistry());
        }

        String topic = exchange.getIn().getHeader(CAMEL_KURA_DATATRANSPORT_TOPIC, String.class);
        if (topic == null) {
            topic = getEndpoint().getTopic();
        }

        Integer qos = exchange.getIn().getHeader(CAMEL_KURA_DATATRANSPORT_QOS, Integer.class);
        if (qos == null) {
            qos = getEndpoint().getQos();
        }

        dataTransportService.publish(topic, payload, qos, getEndpoint().isRetain());
    }

    @Override
    public KuraDataTransportEndpoint getEndpoint() {
        return (KuraDataTransportEndpoint) super.getEndpoint();
    }

}
