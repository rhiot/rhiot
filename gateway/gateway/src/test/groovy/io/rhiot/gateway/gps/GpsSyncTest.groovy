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

import com.google.common.truth.Truth
import io.rhiot.gateway.test.GatewayTest
import io.vertx.core.json.Json
import org.apache.camel.component.mock.MockEndpoint
import org.junit.Test

import static com.google.common.io.Files.createTempDir
import static io.rhiot.cloudplatform.schema.gps.GpsCoordinates.gpsCoordinates
import static io.rhiot.utils.Properties.setBooleanProperty
import static io.rhiot.utils.Properties.setStringProperty
import static java.lang.Boolean.parseBoolean
import static java.lang.System.getenv
import static org.junit.Assume.assumeFalse

class GpsSyncTest extends GatewayTest {

    static def gpsCoordinatesStore = createTempDir()

    @Override
    protected void doBefore() {
        assumeFalse(parseBoolean(getenv('IS_TRAVIS')))

        // Gateway GPS store fixtures
        setBooleanProperty('gps', true)
        setStringProperty('gps_endpoint', 'seda:gps')
        setStringProperty('gps_store_directory', gpsCoordinatesStore.getAbsolutePath());

        // Cloudlet synchronization fixtures
        setBooleanProperty('gps_cloudlet_sync', true)
        setStringProperty('gps_cloudlet_endpoint', 'mock:gps')
    }

    @Test
    void shouldSendGpsData() {
        // Given
        def coordinates = gpsCoordinates(new Date(), 10.0, 20.0)
        def dataStreamConsumerMock = camelContext.getEndpoint('mock:gps', MockEndpoint.class)
        dataStreamConsumerMock.reset()
        dataStreamConsumerMock.setExpectedMessageCount(1)

        // When
        camelContext.createProducerTemplate().sendBody('seda:gps', coordinates)

        // Then
        dataStreamConsumerMock.assertIsSatisfied()
    }

    @Test
    void shouldSendClientId() {
        // Given
        def coordinates = gpsCoordinates(new Date(), 10.0, 20.0)
        def dataStreamConsumerMock = camelContext.getEndpoint('mock:gps', MockEndpoint.class)
        dataStreamConsumerMock.reset()
        dataStreamConsumerMock.setExpectedMessageCount(1)

        // When
        camelContext.createProducerTemplate().sendBody('seda:gps', coordinates)

        // Then
        dataStreamConsumerMock.assertIsSatisfied()
        def rawCoordinates = dataStreamConsumerMock.exchanges.first().in.getBody(String.class)
        Truth.assertThat(Json.decodeValue(rawCoordinates, Map.class).payload.clientId).isNotNull()
    }

}

