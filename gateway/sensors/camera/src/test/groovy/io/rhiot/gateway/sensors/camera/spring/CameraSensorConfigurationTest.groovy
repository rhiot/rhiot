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
package io.rhiot.gateway.sensors.camera.spring

import com.google.common.collect.ImmutableMap
import io.rhiot.cloudplatform.runtime.spring.test.CloudPlatformTest
import org.junit.BeforeClass
import org.junit.Test

import java.util.concurrent.Callable

import static com.google.common.truth.Truth.assertThat
import static com.jayway.awaitility.Awaitility.await
import static io.rhiot.cloudplatform.connector.Header.arguments
import static io.rhiot.gateway.camel.webcam.WebcamHelper.webcamPresent
import static org.junit.Assume.assumeTrue

class CameraSensorConfigurationTest extends CloudPlatformTest {

    @BeforeClass
    static void beforeClass() {
        assumeTrue(isWebcamPresent())
    }

    @Test
    void shouldSendSensorDataToConnector() {
        // Given
        def query = ImmutableMap.of('query', ImmutableMap.of('deviceId', 'myDevice'))

        // When
        await().until((Callable<Boolean>) { !connector.fromBus("document.findByQuery", query, List.class, arguments("CameraImage")).isEmpty() })

        // Then
        def imageMetadata = connector.fromBus("document.findByQuery", query, List.class, arguments("CameraImage"));
        assertThat(imageMetadata.size()).isGreaterThan(0)
    }

}