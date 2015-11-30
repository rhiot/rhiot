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
import io.rhiot.component.gpsd.ClientGpsCoordinates;
import io.rhiot.gateway.test.GatewayTest
import org.junit.Test

import java.util.concurrent.Callable

import static com.google.common.truth.Truth.assertThat
import static com.jayway.awaitility.Awaitility.await
import static io.rhiot.steroids.camel.CamelBootInitializer.camelContext
import static io.rhiot.utils.Properties.setStringProperty;
import static com.google.common.io.Files.createTempDir;
import static io.rhiot.utils.Properties.setBooleanProperty;

public class GpsRoutesTest extends GatewayTest {

    static def gpsCoordinatesStore = createTempDir()

    @Override
    protected void doBefore() {
        // Gateway GPS store fixtures
        setBooleanProperty('gps', true)
        setStringProperty('gps_endpoint', 'seda:gps')
        setStringProperty('gps_enrich', 'seda:enrich')
        setStringProperty("gps_store_directory", gpsCoordinatesStore.getAbsolutePath());
    }

    @Test
    public void shouldSaveEnrichedGpsData() {
        def coordinates = new ClientGpsCoordinates(new Date(), 10.0, 20.0)
        camelContext().createProducerTemplate().sendBody('seda:gps', coordinates)
        camelContext().createProducerTemplate().sendBody('seda:enrich', new EnrichingData(extraData: 'foo'))

        // Then
        await().until((Callable<Boolean>) {gpsCoordinatesStore.listFiles().length > 0})
        def savedCoordinates = new ObjectMapper().readValue(gpsCoordinatesStore.listFiles().first(), Map.class)
        assertThat(savedCoordinates.enriched.extraData).isEqualTo('foo')
        assertThat(savedCoordinates.lat).isEqualTo(coordinates.lat())
        assertThat(savedCoordinates.lng).isEqualTo(coordinates.lng())
    }

}

class EnrichingData {

    String extraData

}