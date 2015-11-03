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

class BootstrapTest {

    @Test
    void shouldStartInitializer() {
        new Bootstrap().start()
        assertThat(TestBootInitializer.started).isTrue()
    }

    @Test
    void shouldStopInitializer() {
        new Bootstrap().start().stop()
        assertThat(TestBootInitializer.started).isFalse()
    }

}

class TestBootInitializer implements BootInitializer {

    static boolean started

    @Override
    void start() {
        started = true
    }

    @Override
    void stop() {
        started = false
    }

    @Override
    int order() {
        0
    }

}
