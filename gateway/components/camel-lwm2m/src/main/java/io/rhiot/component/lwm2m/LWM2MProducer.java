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
import org.eclipse.leshan.client.resource.LwM2mInstanceEnabler;
import org.eclipse.leshan.client.resource.ObjectEnabler;
import org.eclipse.leshan.client.resource.ObjectsInitializer;
import org.eclipse.leshan.core.request.UplinkRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
		Object body = exchange.getIn().getBody();
		LOG.debug("About to send uplink request {}", body);
		if (!(body instanceof UplinkRequest)) {
			throw new IllegalArgumentException("Invalid body type, it should be a subtype of " + UplinkRequest.class);
		}

		Object response = null;
		if (isOneShotClient(exchange)) {
			LeshanClient oneShotClient = createClient(getHost(exchange), getPort(exchange), getDeviceClass(exchange), getObjectEnablersFactory(exchange));
			oneShotClient.start();
			response = oneShotClient.send((UplinkRequest) body);
			oneShotClient.stop();
		} else {
			response = client.send((UplinkRequest) body);
		}
		exchange.getOut().setBody(response);
	}

	private boolean isOneShotClient(Exchange exchange) {
		final Map<String, Object> headers = exchange.getIn().getHeaders();
		return headers.containsKey(LWM2MConstants.HOST)
				|| headers.containsKey(LWM2MConstants.PORT)
				|| headers.containsKey(LWM2MConstants.DEVICE_CLASS)
				|| headers.containsKey(LWM2MConstants.OBJECT_ENABLERS_FACTORY);
	}

	protected LeshanClient createClient(String host, int port, Class<? extends LwM2mInstanceEnabler> deviceClass, LWM2MObjectEnablersFactory objectEnablersFactory) {
		InetSocketAddress serverAddress = new InetSocketAddress(host, port);
		List<ObjectEnabler> enablers = null;
		if (objectEnablersFactory != null) {
			enablers = objectEnablersFactory.getObjectEnablers();
		} else {
			ObjectsInitializer objectsInitializer = new ObjectsInitializer();
			if (deviceClass != null) {
				objectsInitializer.setClassForObject(3, deviceClass);
			}
			enablers = objectsInitializer.create(3, 6);
		}
		return new LeshanClient(serverAddress, new ArrayList<>(enablers));
	}

	protected LeshanClient createDefaultClient() {
		return createClient(endpoint.getHost(), endpoint.getPort(), endpoint.getDeviceClass(), endpoint.getObjectEnablersFactory());
	}

	private String getHost(Exchange exchange) {
		String ret = exchange.getIn().getHeader(LWM2MConstants.HOST, String.class);
		if (ret == null) {
			ret = endpoint.getHost();
		}
		return ret;
	}

	private int getPort(Exchange exchange) {
		Integer ret = exchange.getIn().getHeader(LWM2MConstants.PORT, Integer.class);
		if (ret == null) {
			ret = endpoint.getPort();
		}
		return ret;
	}

	private Class<? extends LwM2mInstanceEnabler> getDeviceClass(Exchange exchange) {
		Class<? extends LwM2mInstanceEnabler> ret = exchange.getIn().getHeader(LWM2MConstants.DEVICE_CLASS, Class.class);
		if (ret == null) {
			ret = endpoint.getDeviceClass();
		}
		return ret;
	}

	private LWM2MObjectEnablersFactory getObjectEnablersFactory(Exchange exchange) {
		LWM2MObjectEnablersFactory ret = exchange.getIn().getHeader(LWM2MConstants.HOST, LWM2MObjectEnablersFactory.class);
		if (ret == null) {
			ret = endpoint.getObjectEnablersFactory();
		}
		return ret;
	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();
		this.client = createDefaultClient();
		client.start();
	}

	@Override
	protected void doStop() throws Exception {
		client.stop();
		super.doStop();
	}
}
