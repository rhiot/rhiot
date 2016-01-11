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
package io.rhiot.cloudplatform.service.binding

import io.rhiot.cloudplatform.test.DataStreamTest
import org.junit.Ignore
import org.junit.Test
import org.springframework.stereotype.Component

import static com.google.common.truth.Truth.assertThat

@Ignore
class ServiceBindingTest extends DataStreamTest {

    // Tests

    @Test
    void shouldBindServiceToChannel() {
        def payload = 100
        def response = fromBus("echo.echo.${payload}", long.class)
        assertThat(response).isEqualTo(payload)
    }

    @Test
    void shouldHandlePostedMap() {
        def payload = [foo: 'foo', bar: 'bar']
        def receivedSize = fromBus("echo.sizeOfMap", payload, long.class)
        assertThat(receivedSize).isEqualTo(payload.size())
    }

    @Test
    void shouldHandleArgumentAndPojo() {
        def stringPayload = 'foo'
        def mapPayload = [foo: 'foo', bar: 'bar']
        def received = fromBus("echo.stringAndPojoToStringOperation.foo", mapPayload, String.class)
        assertThat(received).isEqualTo("${stringPayload}${mapPayload.size()}".toString())
    }

    // Beans fixtures

    @Component
    static class EchoServiceBinding extends ServiceBinding {

        EchoServiceBinding() {
            super('echo')
        }

    }

    static interface EchoService {

        long echo(long value)

        long sizeOfMap(Map map)

        String stringAndPojoToStringOperation(String string, Map<String, String> pojo)

    }

    @Component("echo")
    static class DefaultEchoService implements EchoService {

        @Override
        long echo(long value) {
            value
        }

        @Override
        long sizeOfMap(Map map) {
            map.size()
        }

        @Override
        String stringAndPojoToStringOperation(String string, Map<String, String> pojo) {
            "${string}${pojo.size()}"
        }

    }

}