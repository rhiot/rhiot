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

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.UriEndpointComponent;

import java.util.Map;

/**
 * Represents the component that manages {@link LWM2MEndpoint}.
 */
public class LWM2MComponent extends UriEndpointComponent {

	private String host = "0.0.0.0";
	private Integer port = 5683;

	public LWM2MComponent() {
		super(LWM2MEndpoint.class);
	}

	public LWM2MComponent(CamelContext context) {
		super(context, LWM2MEndpoint.class);
	}

	protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
		final LWM2MEndpoint endpoint = new LWM2MEndpoint(uri, this);
		setProperties(endpoint, parameters);

		final String host = extractHostName(remaining);
		int port = extractPortNumber(remaining);
		endpoint.setHost(host);
		endpoint.setPort(port);
		return endpoint;
	}

	private String extractHostName(String remaining) {
		final int index = remaining.indexOf(":");
		if (index != -1) {
			return remaining.substring(0, index);
		} else {
			return host;
		}
	}

	private int extractPortNumber(String remaining) {
		final int index1 = remaining.indexOf(":");
		final int index2 = remaining.indexOf("/");

		if ((index1 != -1)) {
			final String result = remaining.substring(index1 + 1, index2 != -1 ? index2 : remaining.length());
			return Integer.parseInt(result);
		} else {
			return port;
		}
	}
}
