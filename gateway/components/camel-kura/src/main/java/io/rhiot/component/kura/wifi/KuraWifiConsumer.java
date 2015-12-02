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
package io.rhiot.component.kura.wifi;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.impl.DefaultScheduledPollConsumer;
import org.eclipse.kura.net.wifi.WifiAccessPoint;

import java.util.List;

/**
 * Kura WiFi Consumer periodically scans the access points available for the
 * device.
 */
public class KuraWifiConsumer extends DefaultScheduledPollConsumer {

    public KuraWifiConsumer(DefaultEndpoint defaultEndpoint, Processor processor) {
        super(defaultEndpoint, processor);
    }

    @Override
    protected int poll() throws Exception {
        List<WifiAccessPoint> wifiAccessPoints = getEndpoint().wifiAccessPoints();
        Exchange exchange = ExchangeBuilder.anExchange(getEndpoint().getCamelContext()).withBody(wifiAccessPoints)
                .build();
        getProcessor().process(exchange);
        return wifiAccessPoints.isEmpty() ? 0 : 1;
    }

    @Override
    public KuraWifiEndpoint getEndpoint() {
        return (KuraWifiEndpoint) super.getEndpoint();
    }

}
