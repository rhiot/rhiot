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
package io.rhiot.cloudplatform.encoding.json.spring;

import com.google.common.truth.Truth;
import io.rhiot.cloudplatform.encoding.spi.PayloadEncoding;
import io.rhiot.cloudplatform.runtime.spring.test.CloudPlatformTest;
import org.junit.Test;

public class JsonPayloadEncodingConfigurationTest extends CloudPlatformTest {

    @Test
    public void shouldDecodePayload() {
        // Given
        String payload = "payload";
        byte[] encodedPayload = payloadEncoding.encode(payload);

        // When
        String decodedPayload = (String) payloadEncoding.decode(encodedPayload);

        // Then
        Truth.assertThat(decodedPayload).isEqualTo(payload);
    }

    @Test
    public void shouldDecodeNullPayload() {
        // Given
        byte[] encodedPayload = payloadEncoding.encode(null);

        // When
        String decodedPayload = (String) payloadEncoding.decode(encodedPayload);

        // Then
        Truth.assertThat(decodedPayload).isNull();
    }

}