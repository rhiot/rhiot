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
import io.rhiot.datastream.schema.device.Device
import org.joda.time.DateTime
import org.junit.Test

import static com.google.common.truth.Truth.assertThat
import static io.rhiot.datastream.schema.device.DeviceConstants.CHANNEL_DEVICE_DEREGISTER
import static io.rhiot.datastream.schema.device.DeviceConstants.CHANNEL_DEVICE_REGISTER
import static io.rhiot.datastream.schema.device.DeviceConstants.deviceHeartbeat
import static io.rhiot.datastream.schema.device.DeviceConstants.disconnected
import static io.rhiot.datastream.schema.device.DeviceConstants.getDevice
import static io.rhiot.datastream.schema.device.DeviceConstants.listDevices
import static io.rhiot.datastream.schema.device.DeviceConstants.registerDevice
import static io.rhiot.utils.Networks.findAvailableTcpPort
import static io.rhiot.utils.Properties.setBooleanProperty
import static io.rhiot.utils.Properties.setIntProperty
import static io.rhiot.utils.Uuids.uuid

class DeviceDataStreamConsumerTest extends DataStreamTest {

    def device = new Device(uuid(), uuid(), new Date(), new Date())

    @Override
    protected void beforeDataStreamStarted() {
        setIntProperty('AMQP_PORT', findAvailableTcpPort())
        setBooleanProperty('MQTT_ENABLED', false)
    }

    @Test
    void shouldRegisterDevice() {
        toBusAndWait(registerDevice(), device)
        def devices = fromBus(listDevices(), List.class)
        assertThat(devices).isNotEmpty()
    }

    @Test
    void shouldNotRegisterDeviceTwice() {
        // Given
        device.registrationId = null
        toBusAndWait(registerDevice(), device)

        // When
        def initialDevicesCount = fromBus(listDevices(), List.class).size()
        toBusAndWait(registerDevice(), device)
        def finalDevicesCount = fromBus(listDevices(), List.class).size()

        // When
        assertThat(finalDevicesCount).isEqualTo(initialDevicesCount)
    }

    @Test
    void shouldGenerateRegistrationId() {
        // Given
        device.registrationId = null

        // When
        toBusAndWait(registerDevice(), device)

        // Then
        def device = fromBus(getDevice(device.deviceId), Device.class)
        assertThat(device.registrationId).isNotEmpty()
    }

    @Test
    void shouldListDisconnected() {
        // Given
        device.lastUpdate = new DateTime(device.lastUpdate).minusMinutes(2).toDate()
        toBusAndWait(registerDevice(), device)

        // When
        List<String> disconnected = fromBus(disconnected(), List.class)

        // Then
        assertThat(disconnected).contains(device.deviceId)
    }

    @Test
    void shouldDeregisterDevice() {
        // Given
        toBusAndWait(registerDevice(), device)

        // When
        toBusAndWait("${CHANNEL_DEVICE_DEREGISTER}.${device.registrationId}")

        // Then
        def device = fromBus(getDevice(device.deviceId), Device.class)
        assertThat(device).isNull()
    }

    @Test
    void shouldGetDevice() {
        toBusAndWait(CHANNEL_DEVICE_REGISTER, device)
        def device = fromBus(getDevice(device.deviceId), Device.class)
        assertThat(device.deviceId).isEqualTo(device.deviceId)
    }

    @Test
    void shouldNotGetDevice() {
        def device = fromBus(getDevice(device.deviceId), Device.class)
        assertThat(device).isNull()
    }

    @Test
    void shouldSendHeartbeatDisconnected() {
        // Given
        device.lastUpdate = new DateTime(device.lastUpdate).minusMinutes(2).toDate()
        toBusAndWait(CHANNEL_DEVICE_REGISTER, device)

        // When
        toBusAndWait(deviceHeartbeat(device.deviceId))

        // Then
        List<String> disconnected = fromBus(disconnected(), List.class)
        assertThat(disconnected).doesNotContain(device.deviceId)
    }

}
