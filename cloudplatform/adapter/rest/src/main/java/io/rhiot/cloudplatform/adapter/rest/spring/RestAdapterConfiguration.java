package io.rhiot.cloudplatform.adapter.rest.spring;

import io.rhiot.cloudplatform.adapter.rest.RestProtocolAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestAdapterConfiguration {

    @Bean
    RestProtocolAdapter camelRestStreamSource(@Value("${http_port:8080}") int httpPort) {
        return new RestProtocolAdapter(httpPort);
    }

}