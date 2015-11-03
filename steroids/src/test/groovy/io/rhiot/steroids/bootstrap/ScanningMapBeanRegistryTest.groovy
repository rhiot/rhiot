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
package io.rhiot.steroids.bootstrap

import io.rhiot.steroids.Bean
import org.junit.Test

import static com.google.common.truth.Truth.assertThat

class ScanningMapBeanRegistryTest {

    def beanRegistry = new ScanningMapBeanRegistry()

    @Test
    void shouldReturnCachedBean() {
        // When
        def loadedFirstTime = beanRegistry.bean(TestSingletonBean.class)
        def loadedSecondTime = beanRegistry.bean(TestSingletonBean.class)

        // Then
        assertThat(loadedFirstTime.get()).isSameAs(loadedSecondTime.get())
    }

    @Test
    void shouldReturnCachedBeans() {
        // When
        def loadedFirstTime = beanRegistry.beans(TestBaseClass.class)
        def loadedSecondTime = beanRegistry.beans(TestBaseClass.class)

        // Then
        assertThat(loadedFirstTime[0]).isSameAs(loadedSecondTime[0])
        assertThat(loadedFirstTime[1]).isSameAs(loadedSecondTime[1])
    }

    @Test
    void shouldRegisterBean() {
        // Given
        def bean = 'Register me!'
        beanRegistry.register(bean)

        // When
        def loadedBean = beanRegistry.bean(String.class).get()

        // Then
        assertThat(loadedBean).isEqualTo(bean)
    }

    @Bean
    static class TestSingletonBean {}

    @Bean
    static class TestBaseClass {}

    @Bean
    static class TestDerivedClass {}

}