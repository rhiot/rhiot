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

import io.rhiot.cloudplatform.encoding.spi.PayloadEncoding;
import io.rhiot.cloudplatform.service.binding.ServiceBinding;
import io.rhiot.cloudplatform.service.mailbox.DefaultMailboxService;
import io.rhiot.cloudplatform.service.mailbox.InMemoryMailStore;
import io.rhiot.cloudplatform.service.mailbox.MailStore;
import io.rhiot.cloudplatform.service.mailbox.MailboxService;
import org.apache.camel.ProducerTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailboxServiceConfiguration {

    @Bean(name = "mailbox")
    MailboxService mailboxService(ProducerTemplate producerTemplate, PayloadEncoding payloadEncoding, MailStore mailStore) {
        return new DefaultMailboxService(producerTemplate, payloadEncoding, mailStore);
    }

    @Bean
    ServiceBinding mailboxServiceBinding(PayloadEncoding payloadEncoding) {
        return new ServiceBinding(payloadEncoding, "mailbox");
    }

    @ConditionalOnMissingBean
    @Bean
    MailStore mailStore() {
        return new InMemoryMailStore();
    }

}