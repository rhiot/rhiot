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
package io.rhiot.cloudplatform.paas.services;

import com.google.common.collect.ImmutableMap;
import com.google.common.truth.Truth;
import io.rhiot.cloudplatform.runtime.spring.test.CloudPlatformTest;
import io.rhiot.utils.leshan.client.LeshanClientTemplate;
import io.rhiot.cloudplatform.service.device.api.Device;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static io.rhiot.cloudplatform.service.mailbox.MailboxConstants.inbox;
import static io.rhiot.cloudplatform.service.mailbox.MailboxConstants.outbox;
import static io.rhiot.utils.Uuids.uuid;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static io.rhiot.cloudplatform.service.device.api.DeviceConstants.getDevice;

@Configuration
public class PaasServicesTest extends CloudPlatformTest {

    @Test
    public void smokeTestMongoDocumentService() {
        connector.fromBus("document.save.doc", ImmutableMap.of("foo", "bar"), String.class);
        int count = connector.fromBus("document.count.doc", int.class);
        Truth.assertThat(count).isEqualTo(1);
    }

    @Test
    public void smokeTestDeviceService() {
        // Given
        LeshanClientTemplate device = LeshanClientTemplate.createVirtualLeshanClientTemplate(uuid()).connect();

        // When
        Device loadedDevice = connector.fromBus(getDevice(device.clientId()), Device.class);

        // Then
        Truth.assertThat(loadedDevice.getDeviceId()).isEqualTo(device.clientId());
    }

    @Test
    public void smokeTestMailboxService() {
        String deviceId = randomAlphabetic(10);
        connector.toBusAndWait(outbox(deviceId), ImmutableMap.of("foo", "bar"));
        Map<String, Object> mail = connector.pollChannel(inbox(deviceId), Map.class);
        Truth.assertThat(mail.get("foo")).isEqualTo("bar");
    }

}