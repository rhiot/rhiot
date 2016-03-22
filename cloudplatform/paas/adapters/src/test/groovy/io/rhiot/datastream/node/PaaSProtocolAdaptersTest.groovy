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
package io.rhiot.datastream.node

import io.rhiot.cloudplatform.runtime.spring.test.CloudPlatformTest
import io.rhiot.utils.leshan.client.LeshanClientTemplate
import io.rhiot.cloudplatform.service.device.api.Device
import org.junit.Test
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

import static com.google.common.truth.Truth.assertThat
import static io.rhiot.utils.Uuids.uuid
import static io.rhiot.cloudplatform.service.device.api.DeviceConstants.*

@Configuration
class PaaSProtocolAdaptersTest extends CloudPlatformTest {

    // AMQP tests

    @Test
    void smokeTestMongoDocumentStreamConsumer() {
        connector.fromBus('document.save.doc', [foo: 'bar'], String.class)
        def count = connector.fromBus('document.count.doc', int.class)
        assertThat(count).isEqualTo(1)
    }

    // REST tests

    @Test
    void shouldExecuteRESToperation() {
        // Given
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForLocation('http://localhost:8080/document/save/restDoc', payloadEncoding.encode([foo: 'bar']))

        // When
        def countResponse = restTemplate.getForObject('http://localhost:8080/document/count/restDoc', byte[].class)
        int count = (int) payloadEncoding.decode(countResponse)

        // Then
        assertThat(count).isEqualTo(1)
    }

    // Leshan tests

    @Test
    void smokeTestLeshanProtocolAdapter() {
        // Given
        def device = LeshanClientTemplate.createVirtualLeshanClientTemplate(uuid()).connect()

        // When
        def loadedDevice = connector.fromBus(getDevice(device.clientId()), Device.class)

        // Then
        assertThat(loadedDevice.deviceId).isEqualTo(device.clientId())
    }

}