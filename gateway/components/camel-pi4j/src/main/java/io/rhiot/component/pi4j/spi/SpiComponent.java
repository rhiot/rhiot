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
package io.rhiot.component.pi4j.spi;

import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiFactory;
import io.rhiot.component.pi4j.Pi4jConstants;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.UriEndpointComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpiComponent extends UriEndpointComponent {

    private static final transient Logger LOG = LoggerFactory.getLogger(SpiComponent.class);

    public SpiComponent() {
        super(SpiEndpoint.class);
    }

    public SpiComponent(CamelContext context) {
        super(context, SpiEndpoint.class);
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Endpoint endpoint = null;
        Pattern regexPattern = Pattern.compile(Pi4jConstants.CAMEL_SPI_URL_PATTERN);

        Matcher match = regexPattern.matcher(remaining);
        if (match.matches()) {

            int channelId = Integer.decode(match.group(Pi4jConstants.CAMEL_CHANNEL_ID));

            endpoint = new SpiEndpoint(uri, this, remaining, SpiFactory.getInstance(SpiChannel.getByNumber(channelId)), parameters);
            parameters.put(Pi4jConstants.CAMEL_CHANNEL_ID, channelId);

            setProperties(endpoint, parameters);
        }

        return endpoint;
    }
}
