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
package io.rhiot.steroids.conditions

import io.rhiot.steroids.Bean
import io.rhiot.steroids.PropertyCondition
import io.rhiot.steroids.Steroids
import org.junit.Test

import static com.google.common.truth.Truth.assertThat
import static io.rhiot.utils.Properties.setBooleanProperty
import static java.lang.System.clearProperty

class ConditionalPropertyTest {

    @Test
    void shouldLoadConditionalBean() {
        setBooleanProperty('load_that_bean', true)
        assertThat(Steroids.bean(ConditionalBean.class).isPresent()).isTrue()
    }

    @Test
    void shouldNotLoadConditionalBean_false() {
        setBooleanProperty('load_that_bean', false)
        assertThat(Steroids.bean(ConditionalBean.class).isPresent()).isFalse()
    }

    @Test
    void shouldNotLoadConditionalBean_empty() {
        clearProperty('load_that_bean')
        assertThat(Steroids.bean(ConditionalBean.class).isPresent()).isFalse()
    }

}

@Bean
@PropertyCondition(property = 'load_that_bean')
class ConditionalBean {
}