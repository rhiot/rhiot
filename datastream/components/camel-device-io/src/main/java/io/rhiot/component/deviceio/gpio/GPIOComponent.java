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

package io.rhiot.component.deviceio.gpio;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.rhiot.component.deviceio.DeviceIOConstants;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.UriEndpointComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the component that manages {@link GPIOEndpoint}.
 */
public class GPIOComponent extends UriEndpointComponent {

    private static final transient Logger LOG = LoggerFactory.getLogger(GPIOComponent.class);

    public GPIOComponent() {
        super(GPIOEndpoint.class);
    }

    public GPIOComponent(CamelContext context) {
        super(context, GPIOEndpoint.class);
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Endpoint endpoint = null;
        Pattern regexPattern = Pattern.compile(DeviceIOConstants.CAMEL_GPIO_URL_PATTERN);

        Matcher match = regexPattern.matcher(remaining);
        if (match.matches()) {

            endpoint = new GPIOEndpoint(uri, remaining, this);
            parameters.put(DeviceIOConstants.CAMEL_GPIO_ID, match.group(DeviceIOConstants.CAMEL_GPIO_ID));
            setProperties(endpoint, parameters);

        }

        return endpoint;
    }

}
