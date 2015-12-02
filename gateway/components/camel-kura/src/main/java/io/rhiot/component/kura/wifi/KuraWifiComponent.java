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

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.UriEndpointComponent;

public class KuraWifiComponent extends UriEndpointComponent {

    public KuraWifiComponent() {
        super(KuraWifiEndpoint.class);
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        String[] interfaceAndSsid = remaining.split("/");
        String networkInterface = interfaceAndSsid[0];
        String ssid = interfaceAndSsid[1];
        KuraWifiEndpoint kuraWifiEndpoint = new KuraWifiEndpoint(uri, this);
        kuraWifiEndpoint.setNetworkInterface(networkInterface);
        kuraWifiEndpoint.setSsid(ssid);
        setProperties(kuraWifiEndpoint, parameters);
        return kuraWifiEndpoint;
    }

}
