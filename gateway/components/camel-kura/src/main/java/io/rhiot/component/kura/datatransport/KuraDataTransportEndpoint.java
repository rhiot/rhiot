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

import static io.rhiot.component.kura.datatransport.KuraDataTransportConstants.DEFAULT_QOS;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.eclipse.kura.data.DataTransportService;

@UriEndpoint(scheme = "kura-datatransport", title = "Kura DataTransport", label = "iot,kura,cloud", syntax = "kura-datatransport:topic")
public class KuraDataTransportEndpoint extends DefaultEndpoint {

    @UriParam(defaultValue = "")
    private String topic;

    @UriParam(defaultValue = "0", description = "QoS of the message, per Uri or per Message")
    private int qos = DEFAULT_QOS;

    @UriParam(defaultValue = "false", description = "Message retain, per Uri or per Message")
    private boolean retain = false;

    @UriParam(description = "From URI or OSGi registry")
    private DataTransportService dataTransportService;

    public KuraDataTransportEndpoint(String endpointUri, Component component, String topic) {
        super(endpointUri, component);
        this.topic = topic;
    }

    @Override
    public Producer createProducer() throws Exception {
        return new KuraDataTransportProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public boolean isRetain() {
        return retain;
    }

    public void setRetain(boolean retain) {
        this.retain = retain;
    }

    public DataTransportService getDataTransportService() {
        return dataTransportService;
    }

    public void setDataTransportService(DataTransportService dataTransportService) {
        this.dataTransportService = dataTransportService;
    }

}