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
package io.rhiot.component.coap;

import java.net.URI;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

/**
 * Represents a CoAP endpoint.
 */
@UriEndpoint(scheme = "coap", title = "coap", syntax = "coap:uri", consumerClass = CoAPConsumer.class, label = "coap,protocol")
public class CoAPEndpoint extends DefaultEndpoint {
    @UriPath
    @Metadata(required = "true")
    private URI coapUri;

    private CoapClient client = null;

    @UriParam(description = "", enums = "GET,POST,POST,PUT")
    private Code coapMethod;

    @UriParam(defaultValue = "0 = TEXT_PLAIN", description = "cf. MediaTypeRegistry")
    private int coapMediaType = MediaTypeRegistry.TEXT_PLAIN;

    @UriParam(defaultValue = "1000L", description = "timeout")
    private long coapTimeout = 1000L;

    public CoAPEndpoint() {
    }

    public CoAPEndpoint(String endpointUri) {
        super(endpointUri);
    }

    public CoAPEndpoint(String uri, CoAPComponent component) {
        super(uri, component);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        return new CoAPConsumer(this, processor);
    }

    public Producer createProducer() throws Exception {

        return new CoAPProducer(this);
    }

    public CoapClient getClient() {
        return client;
    }

    public int getCoapMediaType() {
        return coapMediaType;
    }

    public Code getCoapMethod() {
        return coapMethod;
    }

    public long getCoapTimeout() {
        return coapTimeout;
    }

    public URI getCoapUri() {
        return coapUri;
    }

    public boolean isLenientProperties() {
        return true;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setClient(CoapClient client) {
        this.client = client;
    }

    public void setCoapMediaType(int coapMediaType) {
        this.coapMediaType = coapMediaType;
    }

    public void setCoapMethod(Code coapMethod) {
        this.coapMethod = coapMethod;
    }

    public void setCoapTimeout(long coapTimeout) {
        this.coapTimeout = coapTimeout;
    }

    public void setCoapUri(URI coapUri) {
        this.coapUri = coapUri;
    }

}
