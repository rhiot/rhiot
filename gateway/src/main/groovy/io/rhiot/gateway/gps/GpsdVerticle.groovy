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
package io.rhiot.gateway.gps

import io.rhiot.component.gpsd.ClientGpsCoordinates
import io.rhiot.gateway.GatewayVerticle
import io.rhiot.utils.Properties
import io.rhiot.vertx.camel.GroovyCamelVerticle
import org.apache.camel.builder.RouteBuilder

/**
 * Camel route reading current position data from the GPSD socket.
 */
@GatewayVerticle(conditionProperty = 'camellabs_iot_gateway_gpsd')
public class GpsdVerticle extends GroovyCamelVerticle {

    def storeDirectory = Properties.stringProperty('camellabs_iot_gateway_gps_store_directory', '/var/camel-labs-iot-gateway/gps')

    @Override
    void start() throws Exception {
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            void configure() {
                from('gpsd://gps').routeId("gpsd").process { exchange ->
                    def coordinates = exchange.getIn().getBody(ClientGpsCoordinates.class);
                    exchange.getIn().setBody(coordinates.serialize());
                }.to("file://${storeDirectory}");
            }
        })
    }

}