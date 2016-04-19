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

import com.google.common.collect.ImmutableMap;
import com.google.common.truth.Truth;
import io.rhiot.cloudplatform.runtime.spring.test.CloudPlatformTest;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static io.rhiot.cloudplatform.connector.Header.arguments;
import static io.rhiot.utils.Uuids.uuid;
import static io.rhiot.utils.process.Processes.canExecuteCommand;
import static java.lang.Boolean.parseBoolean;
import static java.lang.System.getenv;
import static java.lang.Thread.sleep;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

public class OpenalprImageProcessorDisablingTest extends CloudPlatformTest {

    String deviceId = uuid();

    InputStream image = getClass().getResourceAsStream("/h786poj.jpg");

    @Override
    protected void beforeCloudPlatformStarted() {
        System.setProperty("camera.initialDelay", 120000 + "");
        System.setProperty("camera.processor.openalpr.enabled", false + "");
    }

    // Tests

    @Test
    public void shouldDisableOpenalprProcessor() throws InterruptedException {
        assumeTrue(canExecuteCommand("docker", "version"));
        assumeFalse(parseBoolean(getenv("IS_TRAVIS")));

        // When
        connector.toBusAndWait("camera.process", image, arguments(deviceId, "eu"));
        sleep(5000);

        // Then
        Map<String, Object> query = ImmutableMap.of("query", ImmutableMap.of("deviceId", deviceId));
        List<Map<String, Object>> imageMetadata = connector.fromBus("document.findByQuery", query, List.class, arguments("CameraImage"));
        Truth.assertThat(imageMetadata).hasSize(1);
        Truth.assertThat(((List<Map<String, Object>>)imageMetadata.get(0).get("plateMatches"))).isNull();
    }

}