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

import com.google.common.truth.Truth;
import io.rhiot.cloudplatform.runtime.spring.test.CloudPlatformTest;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static io.rhiot.hono.connector.Header.arguments;

public class CameraServiceConfigurationTest extends CloudPlatformTest {

    @Test
    public void shouldRecognizePlate() {
        InputStream image = getClass().getResourceAsStream("/h786poj.jpg");
        List<Map<String, Object>> plateMatches = connector.fromBus("camera.recognizePlate", image, List.class, arguments("eu"));
        Truth.assertThat(plateMatches.get(0).get("plateNumber")).isEqualTo("H786P0J");
    }

}
