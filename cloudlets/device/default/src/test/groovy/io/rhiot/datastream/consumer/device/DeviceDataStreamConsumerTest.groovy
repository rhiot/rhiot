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
package io.rhiot.datastream.consumer.device

import io.rhiot.datastream.engine.test.DataStreamTest
import io.rhiot.datastream.schema.Device
import org.junit.Test

import static com.google.common.truth.Truth.assertThat
import static io.rhiot.utils.Uuids.uuid

class DeviceDataStreamConsumerTest extends DataStreamTest {

    def device = new Device('foo')

    @Test
    void shouldRegisterDevice() {
        toBusAndWait('device.register', device)
        def devices = fromBus('device.list', List.class)
        assertThat(devices).isNotEmpty()
    }

    @Test
    void shouldGetDevice() {
        toBusAndWait('device.register', device)
        def device = fromBus('device.get.' + device.deviceId, Device.class)
        assertThat(device.deviceId).isEqualTo(device.deviceId)
    }

    @Test
    void shouldNotGetDevice() {
        def device = fromBus('device.get.' + uuid(), Device.class)
        assertThat(device).isNull()
    }

}
