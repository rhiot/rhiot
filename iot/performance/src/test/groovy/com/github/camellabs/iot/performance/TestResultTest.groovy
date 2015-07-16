/**
 * Licensed to the Camel Labs under one or more
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
package com.github.camellabs.iot.performance

import org.junit.Assert
import org.junit.Test

import static java.util.concurrent.TimeUnit.SECONDS

class TestResultTest extends Assert {

    @Test
    void shouldCalculateMessagesPerSecond() {
        def results = new TestResult('testGroup', 'label', 100, SECONDS.toMillis(10))
        assertEquals(10, results.messagesPerSecond(), 0.01)
    }

}
