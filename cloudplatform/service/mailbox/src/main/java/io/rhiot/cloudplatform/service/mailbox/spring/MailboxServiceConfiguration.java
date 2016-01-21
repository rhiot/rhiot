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