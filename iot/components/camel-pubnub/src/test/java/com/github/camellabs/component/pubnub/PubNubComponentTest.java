/**
 * Licensed to the Camel Labs under one or more
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
package com.github.camellabs.component.pubnub;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.json.JSONObject;
import org.junit.Test;

public class PubNubComponentTest extends CamelTestSupport {
    private PubNubEndpoint endpoint;

    @Test
    public void testPubNub() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.expectedHeaderReceived("CamelPubNubChannel", "someChannel");
        mock.expectedBodiesReceived("{\"hi\":\"there\"}");
        JSONObject jo = new JSONObject();
        jo.put("hi", "there");
        template.sendBody("direct:publish", jo);
        assertMockEndpointsSatisfied();
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = super.createCamelContext();
        PubNubComponent component = new PubNubComponent(context);
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("subscriberKey", "dummy");
        parameters.put("publisherKey", "dummy");
        endpoint = (PubNubEndpoint)component.createEndpoint("pubnub", "pubsub:someChannel", parameters);
        endpoint.setPubnub(new PubNubMock("dummy", "dummy"));
        context.addComponent("pubnub", component);
        return context;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from(endpoint).log("body ${body}").to("mock:result");
                from("direct:publish").to(endpoint);
            }
        };
    }
}
