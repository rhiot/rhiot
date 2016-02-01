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
package io.rhiot.cloudplatform.service.binary.spring;

import com.google.common.truth.Truth;
import io.rhiot.cloudplatform.runtime.spring.test.CloudPlatformTest;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static io.rhiot.cloudplatform.connector.Header.arguments;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class BinaryServiceConfigurationTest extends CloudPlatformTest {

    String binaryId = randomAlphabetic(10);

    // Tests

    @Test
    public void shouldStoreBinary() {
        // Given
        byte[] data = "foo".getBytes();

        // When
        connector.toBusAndWait("binary.store", data, arguments(binaryId));

        // Then
        Truth.assertThat(Arrays.asList(new File("/tmp/rhiot/binary").list())).contains(binaryId);
    }

    @Test
    public void shouldReadBinary() {
        // Given
        byte[] data = "foo".getBytes();
        connector.toBusAndWait("binary.store", data, arguments(binaryId));

        // When
        byte[] receivedData = connector.fromBus("binary.read", byte[].class, arguments(binaryId));

        // Then
        Truth.assertThat(new String(receivedData)).isEqualTo("foo");
    }

}