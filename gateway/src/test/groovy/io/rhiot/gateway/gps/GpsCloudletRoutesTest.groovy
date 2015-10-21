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

import io.rhiot.component.gpsd.ClientGpsCoordinates
import io.rhiot.gateway.Gateway
import org.apache.camel.component.mock.MockEndpoint
import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test

import static com.google.common.io.Files.createTempDir
import static io.rhiot.steroids.camel.CamelBootInitializer.camelContext
import static io.rhiot.utils.Properties.restoreSystemProperties
import static io.rhiot.utils.Properties.setBooleanProperty
import static io.rhiot.utils.Properties.setStringProperty

@Ignore
public class GpsCloudletRoutesTest extends Assert {

    static def gateway = new Gateway()

    static def gpsCoordinatesStore = createTempDir()

    @BeforeClass
    static void beforeClass() {
        restoreSystemProperties()

        // Gateway GPS store fixtures
        setBooleanProperty('gps', true)
        setStringProperty('gps_endpoint', 'seda:gps')
        setStringProperty("gps_store_directory", gpsCoordinatesStore.getAbsolutePath());

        // Cloudlet synchronization fixtures
        setBooleanProperty('gps_cloudlet_sync', true)
        setStringProperty('gps_cloudlet_endpoint', 'mock:gps')

        gateway.start()
    }

    @AfterClass
    static void afterClass() {
        gateway.stop()
    }

    @Test
    void shouldSendGpsData() {
        // Given
        def coordinates = new ClientGpsCoordinates(new Date(), 10.0, 20.0)
        def cloudletMock = camelContext().getEndpoint('mock:gps', MockEndpoint.class)
        cloudletMock.setExpectedMessageCount(1)

        // When
        camelContext().createProducerTemplate().sendBody('seda:gps', coordinates)

        // Then
        cloudletMock.assertIsSatisfied()
    }

}

