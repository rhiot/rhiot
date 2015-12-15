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

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriPath;

import org.eclipse.leshan.client.resource.LwM2mInstanceEnabler;

/**
 * Represents a LWM2M endpoint.
 */
@UriEndpoint(scheme = "lwm2m", title = "LWM2M", syntax = "lwm2m:host:port", label = "LWM2M")
public class LWM2MEndpoint extends DefaultEndpoint {
	@UriPath(defaultValue = "0.0.0.0")
	private String host;

	@UriPath(defaultValue = "5683")
	private Integer port;

	@UriPath
	private Class<? extends LwM2mInstanceEnabler> deviceClass;

	@UriPath
	private LWM2MObjectEnablersFactory objectEnablersFactory;

	public LWM2MEndpoint(String uri, LWM2MComponent component) {
		super(uri, component);
	}

	public Producer createProducer() throws Exception {
		return new LWM2MProducer(this);
	}

	public Consumer createConsumer(Processor processor) throws Exception {
		throw new IllegalArgumentException("consumer is not implemented yet");
	}

	public boolean isSingleton() {
		return true;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getHost() {
		return host;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Integer getPort() {
		return port;
	}

	public void setObjectEnablersFactory(LWM2MObjectEnablersFactory objectEnablersFactory) {
		this.objectEnablersFactory = objectEnablersFactory;
	}

	public LWM2MObjectEnablersFactory getObjectEnablersFactory() {
		return objectEnablersFactory;
	}

	public void setDeviceClass(Class<? extends LwM2mInstanceEnabler> deviceClass) {
		this.deviceClass = deviceClass;
	}

	public Class<? extends LwM2mInstanceEnabler> getDeviceClass() {
		return deviceClass;
	}
}
