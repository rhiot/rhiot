/**
 * Licensed to the Rhiot under one or more
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
package io.rhiot.gateway.gps

import com.fasterxml.jackson.databind.ObjectMapper
import io.rhiot.gateway.GatewayVerticle
import io.rhiot.utils.Uuids
import io.rhiot.vertx.camel.GroovyCamelVerticle;
import org.apache.camel.builder.RouteBuilder;

import static io.rhiot.utils.Properties.stringProperty;
import static org.apache.camel.Exchange.HTTP_METHOD;
import static org.apache.camel.model.dataformat.JsonLibrary.Jackson;

/**
 * Camel route reading current position data from the BU353 GPS device.
 */
@GatewayVerticle(conditionProperty = 'camellabs_iot_gateway_gps_cloudlet_sync')
class GpsCloudletSyncRoutes extends GroovyCamelVerticle {

    def storeDirectory = stringProperty('camellabs_iot_gateway_gps_store_directory', '/var/camel-labs-iot-gateway/gps')

    def cloudletAddress = stringProperty('camellabs_iot_gateway_gps_cloudlet_address')

    @Override
    void start() throws Exception {
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            void configure() throws Exception {
                from("file://${storeDirectory}?sortBy=file:modified").
                        onException(Exception.class).maximumRedeliveries(100000).useExponentialBackOff().end().
                        process { exc ->
                            def clientCoordinates = new ObjectMapper().readValue(exc.in.getBody(String.class), Map.class)
                            def serverCoordinates = [:]
                            serverCoordinates.client = InetAddress.getLocalHost().getHostName()
                            serverCoordinates.clientId = Uuids.uuid()
                            serverCoordinates.timestamp = clientCoordinates.timestamp
                            serverCoordinates.latitude = clientCoordinates.lat
                            serverCoordinates.longitude = clientCoordinates.lng
                            if(serverCoordinates.enrich != null) {
                                serverCoordinates.enrich = clientCoordinates.enrich
                            }
                            exc.in.setBody(serverCoordinates);
                        }.
                        marshal().json(Jackson).
                        setHeader(HTTP_METHOD, constant("POST")).
                        to("netty4-http:http://${cloudletAddress}/api/document/save/GpsCoordinates");
            }
        })
    }

}
