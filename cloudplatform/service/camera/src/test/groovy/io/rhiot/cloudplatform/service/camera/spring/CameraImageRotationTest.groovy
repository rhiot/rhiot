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
package io.rhiot.cloudplatform.service.camera.spring;

import io.rhiot.cloudplatform.runtime.spring.test.CloudPlatformTest
import org.junit.Test

import java.util.concurrent.Callable

import static com.google.common.truth.Truth.assertThat;
import static com.jayway.awaitility.Awaitility.await;
import static io.rhiot.cloudplatform.connector.Header.arguments
import static io.rhiot.cloudplatform.service.binary.spring.BinaryServiceConfiguration.DEFAULT_IMAGES_DIRECTORY
import static io.rhiot.utils.Properties.setIntProperty;
import static io.rhiot.utils.Uuids.uuid;
import static io.rhiot.utils.process.Processes.canExecuteCommand;
import static java.lang.Boolean.parseBoolean;
import static java.lang.System.getenv;
import static java.lang.Thread.sleep;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

public class CameraImageRotationTest extends CloudPlatformTest {

    // Messages fixtures

    def imagesDirectory = new File(DEFAULT_IMAGES_DIRECTORY)

    def deviceId = uuid()

    def image = getClass().getResourceAsStream('/h786poj.jpg')

    // Configuration fixtures

    @Override
    protected void beforeCloudPlatformStarted() {
        setIntProperty('camera.rotation.storageQuota', 0)
        setIntProperty('camera.rotation.initialDelay', 15000)
    }

    // Tests

    @Test
    void shouldRotateCameraImages() {
        assumeTrue(canExecuteCommand('docker', 'version'))
        assumeFalse(parseBoolean(getenv('IS_TRAVIS')))

        // Given
        imagesDirectory.listFiles().each { it.delete() }

        // When
        connector.toBusAndWait("camera.process", image, arguments(deviceId, "eu"));
        sleep(5000);
        assertThat(imagesDirectory.list().toList()).isNotEmpty();

        // Then
        def query = ['query': ['deviceId': deviceId]]
        await().until(new Callable<Boolean>() {
            @Override
            Boolean call() throws Exception {
                connector.fromBus("document.findByQuery", query, List.class, arguments("CameraImage")).isEmpty()
            }
        });
        def imageMetadata = connector.fromBus("document.findByQuery", query, List.class, arguments("CameraImage"));
        assertThat(imageMetadata).hasSize(0);
        assertThat(imagesDirectory.list().toList()).isEmpty()
    }

}