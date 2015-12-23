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

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.eclipse.kura.data.DataTransportService;

import java.util.Set;

import static io.rhiot.component.kura.datatransport.DataTransportConstants.CAMEL_KURA_DATATRANSPORT_TOPIC;

public class DataTransportProducer extends DefaultProducer {

    public DataTransportProducer(DataTransportEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        DataTransportService dataTransportService = getEndpoint().getDataTransportService();
        if(dataTransportService == null) {
            Set<DataTransportService> dataTransportServices =
                    getEndpoint().getCamelContext().getRegistry().findByType(DataTransportService.class);
            if(dataTransportServices.size() == 1) {
                dataTransportService = dataTransportServices.iterator().next();
            } else {
                throw new IllegalStateException("No DataTransportService found in a registry.");
            }
        }

        byte[] payload = exchange.getIn().getBody(byte[].class);

        String topic = exchange.getIn().getHeader(CAMEL_KURA_DATATRANSPORT_TOPIC, String.class);
        if(topic == null) {
            topic = getEndpoint().getTopic();
        }

        dataTransportService.publish(topic, payload, getEndpoint().getQos(), getEndpoint().isRetain());
    }

    @Override
    public DataTransportEndpoint getEndpoint() {
        return (DataTransportEndpoint) super.getEndpoint();
    }

}
