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

import org.jfree.data.category.DefaultCategoryDataset
import org.junit.Assert
import org.junit.Test

import static java.util.concurrent.TimeUnit.SECONDS
import static org.mockito.Matchers.*
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify

class DiagramDrawerTest extends Assert {

    @Test
    void shouldIncludeMsgPerSecondInTheLabel() {
        // Given
        def testResult = new TestResult('testGroup', 'label', 10, SECONDS.toMillis(10))
        def expectedLabel = "${testResult.label}: ${testResult.messagesPerSecond().round()}"
        def dataset = mock(DefaultCategoryDataset.class)

        // When
        new DiagramDrawer().addDataFromResult(dataset, testResult)

        // Then
        verify(dataset).setValue(anyDouble(), anyString(), eq(expectedLabel))
    }

}
