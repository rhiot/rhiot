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
package io.rhiot.cloudplatform.adapter.leshan.spring;

import com.google.common.truth.Truth;
import io.rhiot.cloudplatform.runtime.spring.test.CloudPlatformTest;
import io.rhiot.utils.leshan.client.LeshanClientTemplate;
import io.rhiot.utils.leshan.client.UpdateRequestBuilder;
import io.rhiot.cloudplatform.service.device.api.Device;
import io.rhiot.cloudplatform.service.device.api.DeviceMetrics;
import org.junit.After;
import org.junit.Test;

import java.util.List;

import static io.rhiot.cloudplatform.connector.Header.arguments;
import static io.rhiot.utils.Networks.findAvailableTcpPort;
import static io.rhiot.utils.Properties.setIntProperty;
import static io.rhiot.utils.leshan.client.LeshanClientTemplate.createVirtualLeshanClientTemplate;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static io.rhiot.cloudplatform.service.device.api.DeviceConstants.*;

public class LeshanProtocolAdapterConfigurationTest extends CloudPlatformTest {

    // Data fixtures

    String deviceId = randomAlphabetic(20);

    // Collaborators fixtures

    LeshanClientTemplate leshanClient = createVirtualLeshanClientTemplate(deviceId);

    @Override
    protected void beforeCloudPlatformStarted() {
        setIntProperty("spring.data.mongodb.port", findAvailableTcpPort());
        System.setProperty("AMQP_PORT", findAvailableTcpPort() + "");
        System.setProperty("MQTT_ENABLED", false + "");
    }

    @Override
    protected void afterCloudPlatformStarted() {
        leshanClient.connect();
    }

    @After
    public void afterDataStream() {
        leshanClient.disconnect();
    }

    // Tests

    @Test
    public void shouldRegisterDevice() {
        Device device = connector.fromBus(getDevice(deviceId), Device.class);
        Truth.assertThat(device.getRegistrationId()).isNotEmpty();
    }

    @Test
    public void shouldRegisterDeviceWithId() {
        Device device = connector.fromBus(getDevice(deviceId), Device.class);
        Truth.assertThat(device.getDeviceId()).isEqualTo(deviceId);
    }

    @Test
    public void shouldListDevices() {
        List<Device> devices = connector.fromBus(listDevices(), List.class);
        Truth.assertThat(devices).isNotEmpty();
    }

    @Test
    public void shouldUpdateDevice() {
        leshanClient.update(new UpdateRequestBuilder().smsNumber("666"));
        Device updatedDevice = connector.fromBus(getDevice(deviceId), Device.class);
        Truth.assertThat(updatedDevice.getProperties().get("smsNumber")).isEqualTo("666");
    }

    @Test
    public void shouldDeregisterDevice() {
        // Given
        Device device = connector.fromBus(getDevice(deviceId), Device.class);

        // When
        connector.toBusAndWait(deregisterDevice(device.getDeviceId()));

        // Then
        device = connector.fromBus(getDevice(deviceId), Device.class);
        Truth.assertThat(device).isNull();
    }

    @Test
    public void shouldPollFirmwareVersion() {
        String firmwareVersion = connector.fromBus("deviceMetricsPoll.read", String.class, arguments(deviceId, DeviceMetrics.firmwareVersion.toString()));
        Truth.assertThat(firmwareVersion).isEqualTo("1.0.0");
    }

}