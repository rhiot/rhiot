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
package io.rhiot.datastream.camel.rest

import com.google.common.truth.Truth
import io.rhiot.datastream.engine.AbstractServiceStreamConsumer
import io.rhiot.datastream.engine.DataStream
import io.rhiot.bootstrap.classpath.Bean
import io.vertx.core.json.Json
import org.junit.Test

class CamelRestServiceStreamSourceTest {

    static def dataStream = new DataStream().start()

    @Test
    void shouldInvokeGetOperation() {
        def response = Json.mapper.readValue(new URL('http://localhost:8080/test/count/1'), Map.class)
        Truth.assertThat(response.result).isEqualTo(1)
    }

    static interface TestService {

        int count(int number)

    }

    @Bean
    static class TestInterfaceImpl implements TestService {

        @Override
        int count(int number) {
            number
        }

    }

    static class TestInterfaceCamelRestStreamSource extends CamelRestServiceStreamSource<TestService> {

        TestInterfaceCamelRestStreamSource() {
            super(TestService.class, 'test')
        }

    }

    static class TestInterfaceStreamConsumer extends AbstractServiceStreamConsumer<TestService> {

        TestInterfaceStreamConsumer() {
            super('test', TestService.class)
        }
    }

}
