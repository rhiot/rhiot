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
package io.rhiot.cloudplatform.service.device.spring;

import com.google.common.truth.Truth;
import io.rhiot.cloudplatform.runtime.spring.test.CloudPlatformTest;
import io.rhiot.datastream.schema.device.Device;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static io.rhiot.datastream.schema.device.DeviceConstants.*;
import static io.rhiot.utils.Networks.findAvailableTcpPort;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class DeviceServiceConfigurationTest extends CloudPlatformTest {

    Device device = new Device(randomAlphabetic(10), randomAlphabetic(10), new Date(), new Date(),
    null, 0, null, 0, randomAlphabetic(10), randomAlphabetic(10), null);

    @Override
    protected void beforeDataStreamStarted() {
        System.setProperty("spring.data.mongodb", findAvailableTcpPort() + "");
        System.setProperty("AMQP_PORT", findAvailableTcpPort() + "");
        System.setProperty("MQTT_ENABLED", false + "");
    }

    @Test
    public void shouldRegisterDevice() {
        connector.toBusAndWait(registerDevice(), device);
        List<Device> devices = connector.fromBus(listDevices(), List.class);
        Truth.assertThat(devices).isNotEmpty();
    }

    @Test
    public void shouldNotRegisterDeviceTwice() {
        // Given
        device.setRegistrationId(null);
        connector.toBusAndWait(registerDevice(), device);

        // When
        int initialDevicesCount = connector.fromBus(listDevices(), List.class).size();
        connector.toBusAndWait(registerDevice(), device);
        int finalDevicesCount = connector.fromBus(listDevices(), List.class).size();

        // When
        Truth.assertThat(finalDevicesCount).isEqualTo(initialDevicesCount);
    }

    @Test
    public void shouldGenerateRegistrationId() {
        // Given
        device.setRegistrationId(null);

        // When
        connector.toBusAndWait(registerDevice(), device);

        // Then
        Device fetchedDevice = connector.fromBus(getDevice(device.getDeviceId()), Device.class);
        Truth.assertThat(fetchedDevice.getRegistrationId()).isNotEmpty();
    }

    @Test
    public void shouldListDisconnected() {
        // Given
        device.setLastUpdate(new DateTime(device.getLastUpdate()).minusMinutes(2).toDate());
        connector.toBusAndWait(registerDevice(), device);

        // When
        List<String> disconnected = connector.fromBus(disconnected(), List.class);

        // Then
        Truth.assertThat(disconnected).contains(device.getDeviceId());
    }

    @Test
    public void shouldDeregisterDevice() {
        // Given
        connector.toBusAndWait(registerDevice(), device);

        // When
        connector.toBusAndWait(deregisterDevice(device.getRegistrationId()));

        // Then
        Device loadedDevice = connector.fromBus(getDevice(device.getDeviceId()), Device.class);
        Truth.assertThat(loadedDevice).isNull();
    }

    @Test
    public void shouldGetDevice() {
        connector.toBusAndWait(CHANNEL_DEVICE_REGISTER, device);
        Device loadedDevice = connector.fromBus(getDevice(device.getDeviceId()), Device.class);
        Truth.assertThat(loadedDevice.getDeviceId()).isEqualTo(device.getDeviceId());
    }

    @Test
    public void shouldNotGetDevice() {
        Device loadedDevice = connector.fromBus(getDevice(device.getDeviceId()), Device.class);
        Truth.assertThat(loadedDevice).isNull();
    }

    @Test
    public void shouldSendHeartbeatDisconnected() {
        // Given
        device.setLastUpdate(new DateTime(device.getLastUpdate()).minusMinutes(2).toDate());
        connector.toBusAndWait(CHANNEL_DEVICE_REGISTER, device);

        // When
        connector.toBusAndWait(deviceHeartbeat(device.getDeviceId()));

        // Then
        List<String> disconnected = connector.fromBus(disconnected(), List.class);
        Truth.assertThat(disconnected).doesNotContain(device.getDeviceId());
    }

    // Device metrics tests

    @Test
    public void shouldReadEmptyMetric() {
        String metric = connector.fromBus(readDeviceMetric(device.getDeviceId(), randomAlphabetic(10)), String.class);

        // Then
        Truth.assertThat(metric).isNull();
    }

    @Test
    public void shouldReadStringMetric() {
        // Given
        String metric = randomAlphabetic(10);
        String value = randomAlphabetic(10);
        connector.toBusAndWait(writeDeviceMetric(device.getDeviceId(), metric), value);

        // When
        String metricRead = connector.fromBus(readDeviceMetric(device.getDeviceId(), metric), String.class);

        // Then
        Truth.assertThat(metricRead).isEqualTo(value);
    }

    @Test
    public void shouldReadIntegerMetric() {
        // Given
        String metric = randomAlphabetic(10);
        int value = 666;
        connector.toBusAndWait(writeDeviceMetric(device.getDeviceId(), metric), value);

        // When
        int metricRead = connector.fromBus(readDeviceMetric(device.getDeviceId(), metric), int.class);

        // Then
        Truth.assertThat(metricRead).isEqualTo(value);
    }

    @Test
    public void shouldReadAllMetrics() {
        // Given
        String metric1 = randomAlphabetic(10);
        String value1 = randomAlphabetic(10);
        String metric2 = randomAlphabetic(10);
        String value2 = randomAlphabetic(10);
        connector.toBusAndWait(writeDeviceMetric(device.getDeviceId(), metric1), value1);
        connector.toBusAndWait(writeDeviceMetric(device.getDeviceId(), metric2), value2);


        // When
        Map metrics = connector.fromBus(readAllDeviceMetrics(device.getDeviceId()), Map.class);

        // Then
        Truth.assertThat(metrics).hasSize(2);
        Truth.assertThat(metrics.keySet()).containsAllIn(asList(metric1, metric2));
        Truth.assertThat(metrics.values()).containsAllIn(asList(value1, value2));
    }

}