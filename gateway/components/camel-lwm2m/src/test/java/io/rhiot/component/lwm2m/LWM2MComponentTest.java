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
package io.rhiot.component.lwm2m;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;

import org.junit.Assert;
import org.junit.Test;

import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.client.resource.ObjectEnabler;
import org.eclipse.leshan.client.resource.ObjectsInitializer;
import org.eclipse.leshan.core.request.DeregisterRequest;
import org.eclipse.leshan.core.request.RegisterRequest;
import org.eclipse.leshan.core.response.LwM2mResponse;
import org.eclipse.leshan.core.response.RegisterResponse;

import java.util.List;

public class LWM2MComponentTest extends CamelTestSupport {

	@Produce(uri = "direct:start")
	ProducerTemplate producerTemplate;

	@Test
	public void testRegistrationDefaultClient() throws Exception {
		MockEndpoint mock = getMockEndpoint("mock:result");
		mock.expectedMinimumMessageCount(2);

		// register
		RegisterRequest registerRequest = new RegisterRequest("myDeviceDefault");
		producerTemplate.sendBody(registerRequest);

		RegisterResponse registerResponse = mock.getExchanges().get(0).getIn().getBody(RegisterResponse.class);
		Assert.assertEquals(ResponseCode.CREATED, registerResponse.getCode());

		// deregister
		DeregisterRequest deregisterRequest = new DeregisterRequest(registerResponse.getRegistrationID());
		producerTemplate.sendBody(deregisterRequest);

		LwM2mResponse deregisterResponse = mock.getExchanges().get(1).getIn().getBody(LwM2mResponse.class);
		Assert.assertEquals(ResponseCode.DELETED, deregisterResponse.getCode());

		assertMockEndpointsSatisfied();
	}

	@Test
	public void testRegistrationOneShotClient() throws Exception {
		MockEndpoint mock = getMockEndpoint("mock:result");
		mock.expectedMinimumMessageCount(2);

		// register
		RegisterRequest request = new RegisterRequest("myDeviceOneShot");
		LWM2MObjectEnablersFactory objectEnablersFactory = new LWM2MObjectEnablersFactory() {
			@Override
			public List<ObjectEnabler> getObjectEnablers() {
				ObjectsInitializer objectsInitializer = new ObjectsInitializer();
				return objectsInitializer.create(3, 6);
			}
		};
		producerTemplate.sendBodyAndHeader(request, LWM2MConstants.OBJECT_ENABLERS_FACTORY, objectEnablersFactory);

		RegisterResponse registerResponse = mock.getExchanges().get(0).getIn().getBody(RegisterResponse.class);
		Assert.assertEquals(ResponseCode.CREATED, registerResponse.getCode());

		// deregister
		DeregisterRequest deregisterRequest = new DeregisterRequest(registerResponse.getRegistrationID());
		producerTemplate.sendBody(deregisterRequest);

		LwM2mResponse deregisterResponse = mock.getExchanges().get(1).getIn().getBody(LwM2mResponse.class);
		Assert.assertEquals(ResponseCode.DELETED, deregisterResponse.getCode());

		assertMockEndpointsSatisfied();
	}

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			public void configure() {
				from("direct://start")
						.to("lwm2m:leshan.eclipse.org:5683")
						.to("mock:result");
			}
		};
	}
}
