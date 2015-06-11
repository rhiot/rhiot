/**
 * Licensed to the Camel Labs under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.camellabs.iot.gateway;

import com.github.camellabs.iot.component.gps.bu353.ClientGpsCoordinates;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.Date;
import java.util.UUID;

import static com.github.camellabs.iot.component.gps.bu353.ClientGpsCoordinates.deserialize;
import static org.apache.camel.Exchange.HTTP_METHOD;
import static org.apache.camel.model.dataformat.JsonLibrary.Jackson;

/**
 * Camel route reading current position data from the BU353 GPS device.
 */
@Component
@ConditionalOnProperty(value = "camellabs_iot_gateway_gps_cloudlet_sync", havingValue = "true")
public class GpsCloudletSyncRoutes extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("file://{{camellabs_iot_gateway_gps_store_directory:/var/camel-labs-iot-gateway/gps}}?sortBy=file:modified").
                onException(Exception.class).maximumRedeliveries(100000).useExponentialBackOff().end().
                process(exc -> {
                    ClientGpsCoordinates clientCoordinates = deserialize(exc.getIn().getBody(String.class));
                    ServerCoordinates serverCoordinates = new ServerCoordinates(InetAddress.getLocalHost().getHostName(), UUID.randomUUID().toString(), clientCoordinates.timestamp(), clientCoordinates.lat(), clientCoordinates.lng());
                    exc.getIn().setBody(serverCoordinates);
                }).
                marshal().json(Jackson).
                setHeader(HTTP_METHOD, constant("POST")).
                to("netty4-http:http://{{camellabs_iot_gateway_gps_cloudlet_address}}/api/document/save/GpsCoordinates");
    }

}

class ServerCoordinates {

    private final String client;

    private final String clientId;

    private final Date timestamp;

    private final double latitude;

    private final double longitude;

    public ServerCoordinates(String client, String clientId, Date timestamp, double latitude, double longitude) {
        this.client = client;
        this.clientId = clientId;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getClient() {
        return client;
    }

    public String getClientId() {
        return clientId;
    }

    public Date getTimestamp() {
        return timestamp;
    }


    public double getLatitude() {
        return latitude;
    }


    public double getLongitude() {
        return longitude;
    }

}