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

import static java.util.Collections.singletonList;

import java.util.List;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.eclipse.kura.net.wifi.WifiAccessPoint;

@UriEndpoint(scheme = "kura-wifi", title = "Kura WiFi", consumerClass = KuraWifiConsumer.class, label = "iot,raspberrypi,kura", syntax = "kura-wifi:interface/ssid")
public class KuraWifiEndpoint extends DefaultEndpoint {

    @UriParam(defaultValue = "*")
    private String networkInterface = "*";

    @UriParam(defaultValue = "*")
    private String ssid = "*";

    @UriParam
    AccessPointsProvider accessPointsProvider;

    public KuraWifiEndpoint(String endpointUri, KuraWifiComponent component) {
        super(endpointUri, component);
        accessPointsProvider = new KuraAccessPointsProvider(component.getCamelContext().getRegistry());
    }

    public List<WifiAccessPoint> wifiAccessPoints() {
        List<WifiAccessPoint> wifiAccessPoints = getAccessPointsProvider().accessPoints(getNetworkInterface());
        if (!getSsid().equals("*")) {
            for (WifiAccessPoint accessPoint : wifiAccessPoints) {
                if (accessPoint.getSSID().equals(getSsid())) {
                    return singletonList(accessPoint);
                }
            }
        }
        return wifiAccessPoints;
    }

    @Override
    public Producer createProducer() throws Exception {
        return new KuraWifiProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return new KuraWifiConsumer(this, processor);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    // Configuration getters and setters

    public String getNetworkInterface() {
        return networkInterface;
    }

    public void setNetworkInterface(String networkInterface) {
        this.networkInterface = networkInterface;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    // Collaborators getters and setters

    public AccessPointsProvider getAccessPointsProvider() {
        return accessPointsProvider;
    }

    public void setAccessPointsProvider(AccessPointsProvider accessPointsProvider) {
        this.accessPointsProvider = accessPointsProvider;
    }

}