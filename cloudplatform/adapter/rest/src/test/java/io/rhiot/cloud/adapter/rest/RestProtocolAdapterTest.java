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
package io.rhiot.cloud.adapter.rest;

import com.google.common.collect.ImmutableMap;
import com.google.common.truth.Truth;
import io.rhiot.cloudplatform.encoding.spi.PayloadEncoding;
import io.rhiot.cloudplatform.service.binding.ServiceBinding;
import io.rhiot.cloudplatform.runtime.spring.test.CloudPlatformTest;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import static java.lang.System.setProperty;
import static org.springframework.util.SocketUtils.findAvailableTcpPort;

@Configuration
public class RestProtocolAdapterTest extends CloudPlatformTest {

    RestTemplate rest = new RestTemplate();

    static int restPort = findAvailableTcpPort();

    String baseURL = "http://localhost:" + restPort + "/test/";

    @Override
    protected void beforeCloudPlatformStarted() {
        setProperty("rest.port", restPort + "");
    }

    // Tests

    @Test
    public void shouldInvokeGetOperation() throws IOException {
        Map response = json.readValue(new URL(baseURL + "count/1"), Map.class);
        Truth.assertThat(response.get("payload")).isEqualTo(1);
    }

    @Test
    public void shouldInvokePostOperation() {
        byte[] request = payloadEncoding.encode(ImmutableMap.of("foo", "bar"));
        Object payload = rest.postForObject(baseURL + "sizeOf", request, Map.class).get("payload");
        Truth.assertThat(payload).isEqualTo(1);
    }

    @Test
    public void shouldPassUriAndBody() {
        byte[] request = payloadEncoding.encode(ImmutableMap.of("foo", "bar"));
        Object payload = rest.postForObject(baseURL + "numberPlusSizeOf/1", request, Map.class).get("payload");
        Truth.assertThat(payload).isEqualTo(2);
    }

    @Test
    public void shouldHandleOptions() {
        Set<HttpMethod> options = rest.optionsForAllow(baseURL + "count/1");
        Truth.assertThat(options).isEmpty();
    }

    // Beans fixtures

    public interface TestService {

        int count(int number);

        int sizeOf(Map map);

        int numberPlusSizeOf(int number, Map map);

    }

    @Component("test")
    public static class TestInterfaceImpl implements TestService {

        @Override
        public int count(int number) {
            return number;
        }

        @Override
        public int sizeOf(Map map) {
            return map.size();
        }

        @Override
        public int numberPlusSizeOf(int number, Map map) {
            return number + map.size();
        }

    }

    @Bean
    ServiceBinding testServiceBinding(PayloadEncoding payloadEncoding) {
        return new ServiceBinding(payloadEncoding, "test");
    }

}