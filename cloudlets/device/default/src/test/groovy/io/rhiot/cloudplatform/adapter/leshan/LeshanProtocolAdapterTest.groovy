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
package io.rhiot.cloudplatform.adapter.leshan

import io.rhiot.cloudplatform.runtime.spring.test.CloudPlatformTest
import io.rhiot.datastream.schema.device.Device
import io.rhiot.utils.leshan.client.UpdateRequestBuilder
import org.junit.After
import org.junit.Test

import static com.google.common.truth.Truth.assertThat
import static io.rhiot.datastream.schema.device.DeviceConstants.deregisterDevice
import static io.rhiot.datastream.schema.device.DeviceConstants.getDevice
import static io.rhiot.datastream.schema.device.DeviceConstants.listDevices
import static io.rhiot.utils.Networks.findAvailableTcpPort
import static io.rhiot.utils.Properties.setBooleanProperty
import static io.rhiot.utils.Properties.setIntProperty
import static io.rhiot.utils.Uuids.uuid
import static io.rhiot.utils.leshan.client.LeshanClientTemplate.createVirtualLeshanClientTemplate

class LeshanProtocolAdapterTest extends CloudPlatformTest {

    // Data fixtures

    def deviceId = uuid()

    // Collaborators fixtures

    def leshanClient = createVirtualLeshanClientTemplate(deviceId)

    @Override
    protected void beforeDataStreamStarted() {
        setIntProperty('spring.data.mongodb', findAvailableTcpPort())
        setIntProperty('AMQP_PORT', findAvailableTcpPort())
        setBooleanProperty('MQTT_ENABLED', false)
    }

    @Override
    protected void afterDataStreamStarted() {
        leshanClient.connect()
    }

    @After
    void afterDataStream() {
        leshanClient.disconnect()
    }

    // Tests

    @Test
    void shouldRegisterDevice() {
        def device = connector.fromBus(getDevice(deviceId), Device.class)
        assertThat(device.registrationId).isNotEmpty()
    }

    @Test
    void shouldRegisterDeviceWithId() {
        def device = connector.fromBus(getDevice(deviceId), Device.class)
        assertThat(device.deviceId).isEqualTo(deviceId)
    }

    @Test
    void shouldListDevices() {
        List<Device> devices = connector.fromBus(listDevices(), List.class)
        assertThat(devices).isNotEmpty()
    }

    @Test
    void shouldUpdateDevice() {
        leshanClient.update(new UpdateRequestBuilder().smsNumber('666'));
        def updatedDevice = connector.fromBus(getDevice(deviceId), Device.class);
        assertThat(updatedDevice.smsNumber).isEqualTo('666')
    }

    @Test
    void shouldDeregisterDevice() {
        // Given
        def device = connector.fromBus(getDevice(deviceId), Device.class)

        // When
        connector.toBusAndWait(deregisterDevice(device.registrationId))

        // Then
        device = connector.fromBus(getDevice(deviceId), Device.class)
        assertThat(device).isNull()
    }

}