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
package io.rhiot.cloudplatform.service.binding;

import com.google.common.collect.ImmutableMap;
import com.google.common.truth.Truth;
import io.rhiot.cloudplatform.encoding.spi.PayloadEncoding;
import io.rhiot.cloudplatform.test.DataStreamTest;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

public class ServiceBindingTest extends DataStreamTest {

    // Tests

    @Test
    public void shouldBindServiceToChannel() {
        long payload = 100;
        int response = fromBus("echo.echo", payload, int.class);
        Truth.assertThat(response).isEqualTo(payload);
    }

    @Test
    public void shouldBindServiceToChannelUsingDestinationOnly() {
        long payload = 100;
        int response = fromBus("echo.echo." + payload, int.class);
        Truth.assertThat(response).isEqualTo(payload);
    }

    @Test
    public void shouldHandlePostedMap() {
        Map payload = ImmutableMap.of("foo", "foo",  "bar", "bar");
        int receivedSize = fromBus("echo.sizeOfMap", payload, int.class);
        Truth.assertThat(receivedSize).isEqualTo(payload.size());
    }

    @Test
    public void shouldHandleArgumentAndPojo() {
        String stringPayload = "foo";
        Map mapPayload = ImmutableMap.of("foo", "foo", "bar", "bar");
        String received = fromBus("echo.stringAndPojoToStringOperation.foo", mapPayload, String.class);
        Truth.assertThat(received).isEqualTo(stringPayload + mapPayload.size());
    }

    // Beans fixtures

    @Component
    public static class EchoServiceBinding extends ServiceBinding {

        @Autowired
        public EchoServiceBinding(PayloadEncoding payloadEncoding) {
            super(payloadEncoding, "echo");
        }

    }

    public static interface EchoService {

        long echo(long value);

        long sizeOfMap(Map map);

        String stringAndPojoToStringOperation(String string, Map<String, String> pojo);

    }

    @Component("echo")
    public static class DefaultEchoService implements EchoService {

        @Override
        public long echo(long value) {
            return value;
        }

        @Override
        public long sizeOfMap(Map map) {
            return map.size();
        }

        @Override
        public String stringAndPojoToStringOperation(String string, Map<String, String> pojo) {
            return string + pojo.size();
        }

    }

}