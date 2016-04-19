/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rhiot.cloud.adapter.rest.spring;

import io.rhiot.cloud.adapter.rest.RestProtocolAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.rhiot.cloud.adapter.rest.RestProtocolAdapter.DEFAULT_CONTENT_TYPE;

/**
 * Spring configuration for REST protocol adapter.
 */
@Configuration
public class RestProtocolAdapterConfiguration {

    @Bean
    RestProtocolAdapter restProtcolAdapter(
            @Value("${rest.port:8080}") int httpPort,
            @Value("${rest.contentType:" + DEFAULT_CONTENT_TYPE + "}") String contentType) {
        return new RestProtocolAdapter(httpPort, contentType);
    }

}