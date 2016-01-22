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
package io.rhiot.cloudplatform.service.mailbox.spring;

import com.google.common.collect.ImmutableMap;
import com.google.common.truth.Truth;
import io.rhiot.cloudplatform.runtime.spring.test.CloudPlatformTest;
import io.rhiot.cloudplatform.service.mailbox.InMemoryMailStore;
import org.junit.Test;

import java.util.Map;

import static io.rhiot.cloudplatform.service.mailbox.MailboxConstants.inbox;
import static io.rhiot.cloudplatform.service.mailbox.MailboxConstants.outbox;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class MailboxServiceConfigurationTest extends CloudPlatformTest {

    String deviceId = randomAlphabetic(10);

    @Test
    public void shouldReceiveMail() {
        connector.toBusAndWait(outbox(deviceId), ImmutableMap.of("foo", "bar"));
        Map<String, Object> mail = connector.pollChannel(inbox(deviceId), Map.class);
        Truth.assertThat(mail.get("foo")).isEqualTo("bar");
    }

    @Test
    public void shouldSaveMail() {
        connector.toBusAndWait(outbox(deviceId), ImmutableMap.of("foo", "bar"));
        Map<String, Map<String, Object>> mails = cloudPlatform.applicationContext().getBean(InMemoryMailStore.class).mails();
        Truth.assertThat(mails.size()).isGreaterThan(0);
    }

}
