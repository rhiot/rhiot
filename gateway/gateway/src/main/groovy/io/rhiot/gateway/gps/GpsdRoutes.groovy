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

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.jackson.JacksonDataFormat
import org.apache.camel.processor.aggregate.AggregationStrategy
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY
import static io.rhiot.utils.Properties.stringProperty

/**
 * Camel route reading current position data from the GPSD socket.
 */
@Component
@ConditionalOnProperty(name = 'gps', havingValue = 'true')
public class GpsdRoutes extends RouteBuilder {

    // Configuration

    def gpsEndpoint = stringProperty('gps_endpoint', 'gpsd://gps?scheduled=true')

    def storeDirectory = stringProperty('gps_store_directory', '/var/rhiot/gps')

    def enrich = stringProperty('gps_enrich')

    // Collaborators

    def jackson = new JacksonDataFormat(new ObjectMapper(), null)

    // Constructors

    GpsdRoutes() {
        def  visibilityChecker = jackson.objectMapper.visibilityChecker.withFieldVisibility(ANY)
        jackson.objectMapper.visibilityChecker = visibilityChecker
    }

    // Routing

    @Override
    void configure() {
        new File(storeDirectory).mkdirs()
        def route = from(gpsEndpoint).routeId('gps')
        if(enrich != null) {
            route = route.pollEnrich(enrich, new MapEnrichingAggregationStrategy(jackson.objectMapper))
        }
        route.marshal(jackson).to("file://${storeDirectory}")
    }

    // Helpers

    private static class MapEnrichingAggregationStrategy implements AggregationStrategy {

        private final ObjectMapper jackson

        MapEnrichingAggregationStrategy(ObjectMapper jackson) {
            this.jackson = jackson
        }

        @Override
        Exchange aggregate(Exchange original, Exchange polled) {
            def body = jackson.convertValue(original.in.body, Map.class)
            original.in.body = body
            body.put('enriched', jackson.convertValue(polled.in.body, Map.class))
            original
        }

    }

}