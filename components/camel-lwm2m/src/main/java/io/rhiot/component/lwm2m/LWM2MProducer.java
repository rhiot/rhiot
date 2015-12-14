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

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

import org.eclipse.leshan.client.californium.LeshanClient;
import org.eclipse.leshan.client.resource.LwM2mObjectEnabler;
import org.eclipse.leshan.client.resource.ObjectEnabler;
import org.eclipse.leshan.client.resource.ObjectsInitializer;
import org.eclipse.leshan.core.request.UplinkRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * The LWM2M producer.
 */
public class LWM2MProducer extends DefaultProducer {

	private static final Logger LOG = LoggerFactory.getLogger(LWM2MProducer.class);

	private LWM2MEndpoint endpoint;

	private LeshanClient client;

	public LWM2MProducer(LWM2MEndpoint endpoint) {
		super(endpoint);
		this.endpoint = endpoint;
	}

	public void process(Exchange exchange) throws Exception {
		final Object body = exchange.getIn().getBody();
		if (!(body instanceof UplinkRequest)) {
			throw new IllegalArgumentException("Invalid body type, it should be a subtype of " + UplinkRequest.class);
		}
		final Object response = client.send((UplinkRequest) body);
		exchange.getOut().setBody(response);
	}

	protected LeshanClient createClient() {
		final List<ObjectEnabler> enablers = new ObjectsInitializer().create(3, 6);
		final InetSocketAddress serverAddress = new InetSocketAddress(endpoint.getHost(), endpoint.getPort());
		client = new LeshanClient(serverAddress, new ArrayList<LwM2mObjectEnabler>(enablers));
		return client;
	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();
		this.client = createClient();
		client.start();
	}

	@Override
	protected void doStop() throws Exception {
		client.stop();
		super.doStop();
	}

}
