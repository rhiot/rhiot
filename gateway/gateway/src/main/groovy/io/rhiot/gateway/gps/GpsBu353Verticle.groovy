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

import io.rhiot.component.gps.bu353.ClientGpsCoordinates
import org.apache.camel.builder.RouteBuilder
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

import static io.rhiot.utils.Properties.stringProperty

/**
 * Camel route reading current position data from the BU353 GPS device.
 *
 * @deprecated Use @GpsdVericle instead.
 */
@Deprecated
@Component
@ConditionalOnProperty(name = 'camellabs_iot_gateway_gps_bu353', havingValue = 'true')
public class GpsBu353Verticle extends RouteBuilder {

    def storeDirectory = stringProperty('camellabs_iot_gateway_gps_store_directory', '/var/camel-labs-iot-gateway/gps')

    @Override
    void configure() throws Exception {
        from('gps-bu353://gps').routeId("gps-bu353").process { exchange ->
            def coordinates = exchange.in.getBody(ClientGpsCoordinates.class)
            exchange.getIn().setBody(coordinates.serialize());
        }.to("file://${storeDirectory}")
    }

}