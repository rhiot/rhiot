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
package io.rhiot.component.deviceio.i2c;

import io.rhiot.component.deviceio.DeviceIOConstants;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.UriEndpointComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the component that manages {@code I2CEndpoint}.
 */
public class I2CComponent extends UriEndpointComponent {

    private static final transient Logger LOG = LoggerFactory.getLogger(I2CComponent.class);

    public I2CComponent() {
        super(I2CEndpoint.class);
    }

    public I2CComponent(CamelContext context) {
        super(context, I2CEndpoint.class);
    }

    public I2CComponent(CamelContext context, Class<? extends Endpoint> endpointClass) {
        super(context, endpointClass);
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {

        Endpoint endpoint = null;
        Pattern regexPattern = Pattern.compile(DeviceIOConstants.CAMEL_I2C_URL_PATTERN);

        Matcher match = regexPattern.matcher(remaining);
        if (match.matches()) {
            endpoint = new I2CEndpoint(uri, this, remaining, parameters);

            int busId = Integer.decode(match.group(DeviceIOConstants.CAMEL_I2C_BUS_ID));
            int deviceId = Integer.decode(match.group(DeviceIOConstants.CAMEL_I2C_DEVICE_ID));

            parameters.put(DeviceIOConstants.CAMEL_I2C_BUS_ID, busId);
            parameters.put(DeviceIOConstants.CAMEL_I2C_DEVICE_ID, deviceId);

            setProperties(endpoint, parameters);
        }

        return endpoint;
    }
}
