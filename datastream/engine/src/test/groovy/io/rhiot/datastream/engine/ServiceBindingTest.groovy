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

import io.vertx.core.eventbus.Message
import org.junit.Test

import javax.inject.Named

import static com.google.common.truth.Truth.assertThat
import static org.mockito.BDDMockito.given
import static org.mockito.Mockito.RETURNS_DEEP_STUBS
import static org.mockito.Mockito.mock

class ServiceBindingTest {

    def serviceBinding = new ServiceBinding()

    def message = mock(Message.class, RETURNS_DEEP_STUBS)

    @Test
    void shouldPassHeader() {
        // Given
        given(message.headers().get('operation')).willReturn('stringStringOperation')
        given(message.headers().get('string')).willReturn('foo')

        // When
        def count = serviceBinding.invokeOperation(Service.class, new ServiceImpl(), message)

        // Then
        assertThat(count).isEqualTo('foo')
    }

    @Test
    void shouldPassBody() {
        // Given
        given(message.headers().get('operation')).willReturn('pojoIntOperation')
        given(message.body()).willReturn([foo: 'bar'])

        // When
        def count = serviceBinding.invokeOperation(Service.class, new ServiceImpl(), message)

        // Then
        assertThat(count).isEqualTo(1)
    }

    static interface Service {

        String stringStringOperation(@Named('string') String string)

        int pojoIntOperation(Map<String, String> pojo)


    }

    static class ServiceImpl implements Service {

        @Override
        String stringStringOperation(String string) {
            string
        }

        @Override
        int pojoIntOperation(Map<String, String> pojo) {
            pojo.size()
        }

    }

}
