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
package io.rhiot.gateway.sensors.device.spring

import io.rhiot.cloudplatform.runtime.spring.test.CloudPlatformTest
import io.rhiot.cloudplatform.service.device.api.Device
import org.junit.Test

import static com.google.common.truth.Truth.assertThat

class DeviceSensorConfigurationTest extends CloudPlatformTest {

    @Override
    protected void beforeCloudPlatformStarted() {
        System.setProperty('deviceId', 'foo')
    }

    @Test
    void shouldRegisterDeviceOnStartup() {
        // When
        Thread.sleep(6000)
        Device[] devices = connector.fromBus('device.list', Device[].class)

        // Then
        assertThat(devices.toList()).hasSize(1)
        assertThat(devices[0].deviceId).isEqualTo('foo')
    }

}
