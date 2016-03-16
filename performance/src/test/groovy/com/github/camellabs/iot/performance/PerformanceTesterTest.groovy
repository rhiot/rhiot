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
package com.github.camellabs.iot.performance

import io.rhiot.cmd.ConsoleInformation
import org.junit.Assert
import org.junit.Test

class PerformanceTesterTest extends Assert {

    def tester = new PerformanceTester()

    @Test
    void shouldDetectInvalidKit() {
        try {
            tester.runTestsForKit('invalid kit')
        } catch (ConsoleInformation info) {
            assertTrue(info.message.contains('No tests found for hardware kit'))
            return
        }
        fail('Console information was expected.')
    }

}
