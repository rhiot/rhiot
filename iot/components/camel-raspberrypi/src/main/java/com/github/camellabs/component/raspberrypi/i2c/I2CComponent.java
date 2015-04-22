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
package com.github.camellabs.component.raspberrypi.i2c;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.camellabs.component.raspberrypi.RaspberryPiConstants;
import com.github.camellabs.component.raspberrypi.gpio.GPIOEndpoint;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactoryProvider;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.UriEndpointComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the component that manages {@link GPIOEndpoint}.
 */
public class I2CComponent extends UriEndpointComponent {

    private static final transient Logger LOG = LoggerFactory.getLogger(I2CComponent.class);

    public I2CComponent() {
        super(I2CEndpoint.class);
    }

    public I2CComponent(CamelContext context) {
        super(context, I2CEndpoint.class);
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Endpoint endpoint = null;
        Pattern regexPattern = Pattern.compile(RaspberryPiConstants.CAMEL_I2C_URL_PATTERN);

        Matcher match = regexPattern.matcher(remaining);
        if (match.matches()) {

            endpoint = new I2CEndpoint(uri, remaining, I2CFactory.getInstance(Integer.parseInt(match.group(RaspberryPiConstants.CAMEL_BUS_ID))));
            parameters.put(RaspberryPiConstants.CAMEL_BUS_ID, match.group(RaspberryPiConstants.CAMEL_BUS_ID));
            parameters.put(RaspberryPiConstants.CAMEL_DEVICE_ID, match.group(RaspberryPiConstants.CAMEL_DEVICE_ID));

            setProperties(endpoint, parameters);
        }

        return endpoint;
    }

    public void setProvider(I2CFactoryProvider provider) {
        I2CFactory.setFactory(provider);
    }

}
