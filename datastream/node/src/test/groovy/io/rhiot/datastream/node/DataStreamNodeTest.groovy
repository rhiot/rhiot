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
import io.rhiot.cloudplatform.schema.device.Device
import io.rhiot.cloudplatform.schema.device.DeviceConstants
import io.rhiot.utils.leshan.client.LeshanClientTemplate
import org.junit.Test
import org.springframework.context.annotation.Configuration

import static com.google.common.truth.Truth.assertThat
import static io.rhiot.utils.Uuids.uuid

@Configuration
class DataStreamNodeTest extends CloudPlatformTest {

    @Test
    void smokeTestMongoDocumentStreamConsumer() {
        connector.fromBus('document.save.doc', [foo: 'bar'], String.class)
        def count = connector.fromBus('document.count.doc', int.class)
        assertThat(count).isEqualTo(1)
    }

    @Test
    void smokeTestLeshanProtocolAdapter() {
        // Given
        def device = LeshanClientTemplate.createVirtualLeshanClientTemplate(uuid()).connect()

        // When
        def loadedDevice = connector.fromBus(DeviceConstants.getDevice(device.clientId()), Device.class)

        // Then
        assertThat(loadedDevice.deviceId).isEqualTo(device.clientId())
    }

}