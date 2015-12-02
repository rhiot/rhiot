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
package io.rhiot.datastream.engine

import io.rhiot.bootstrap.classpath.Bean
import io.rhiot.bootstrap.classpath.Named
import io.rhiot.datastream.engine.test.DataStreamTest
import io.rhiot.steroids.camel.Route
import org.junit.Test

import static com.google.common.truth.Truth.assertThat

class AbstractServiceRouteStreamConsumerTest extends DataStreamTest {

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

    // Beans fixtures

    @Route
    static class EchoStreamConsumer extends AbstractServiceRouteStreamConsumer {

        EchoStreamConsumer() {
            super('echo')
        }

    }

    static interface EchoService {

        long echo(long value)

        long sizeOfMap(Map map)

    }

    @Bean
    @Named(name = 'echo')
    static class DefaultEchoService implements EchoService {

        @Override
        long echo(long value) {
            value
        }

        @Override
        long sizeOfMap(Map map) {
            map.size()
        }

    }

}