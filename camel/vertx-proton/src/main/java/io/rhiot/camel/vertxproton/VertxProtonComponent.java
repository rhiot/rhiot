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

import io.vertx.core.Vertx;
import io.vertx.proton.ProtonClient;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import java.util.Map;

public class VertxProtonComponent extends DefaultComponent {

    private Vertx vertx;

    private ProtonClient protonClient;

    private String address;

    private ReplyToGenerationStrategy replyToGenerationStrategy;

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        VertxProtonEndpoint endpoint = new VertxProtonEndpoint(uri, remaining, this);
        setProperties(endpoint, parameters);
        return endpoint;
    }

    // Getters and setters


    public Vertx getVertx() {
        return vertx;
    }

    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }

    public ProtonClient getProtonClient() {
        return protonClient;
    }

    public void setProtonClient(ProtonClient protonClient) {
        this.protonClient = protonClient;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ReplyToGenerationStrategy getReplyToGenerationStrategy() {
        return replyToGenerationStrategy;
    }

    public void setReplyToGenerationStrategy(ReplyToGenerationStrategy replyToGenerationStrategy) {
        this.replyToGenerationStrategy = replyToGenerationStrategy;
    }

}