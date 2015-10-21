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
package io.rhiot.utils

import org.junit.Test

import static com.google.common.truth.Truth.*
import static io.rhiot.utils.Properties.*
import static io.rhiot.utils.Uuids.uuid

class ThreadLocalPropertiesTest {

    @Test
    void shouldReadThreadProperty() {
        // Given
        def property = uuid()
        def value = uuid()
		
		println(Thread.currentThread().getName() + " : " + property + " " +value)
		
		setThreadStringProperty(property, value)
		
        // When
        def valueRead = threadStringProperty(property)
		
        // Then
        assertThat(valueRead).isEqualTo(value)
		
		property = uuid()
		value = uuid()
		
		valueRead = setThreadStringProperty(property,value)
		println(Thread.currentThread().getName() + " : " + property + " " +value)
		
		// Then
		assertThat(valueRead).isEqualTo(value)
		
		def thread = Thread.start {
			def anotherThreadRead = threadStringProperty(property)
			println(Thread.currentThread().getName() + " : " + property + " " +anotherThreadRead)
			
			assertThat(null).isEqualTo(anotherThreadRead);
			
		}
		Thread.sleep(5000)
    }
	
	
	@Test
	void shouldReadIntThreadProperty() {
		// Given
		def property = uuid()
		def value = 123
		
		println(Thread.currentThread().getName() + " : " + property + " " +value)
		
		setThreadIntProperty(property, value)
		
		// When
		def valueRead = threadIntProperty(property)
		
		// Then
		assertThat(valueRead).isEqualTo(value)
		
		property = uuid()
		value = 345
		
		valueRead = setThreadIntProperty(property,value)
		println(Thread.currentThread().getName() + " : " + property + " " +value)
		
		// Then
		assertThat(valueRead).isEqualTo(value)
		
		def thread = Thread.start {
			def anotherThreadRead = threadIntProperty(property)
			println(Thread.currentThread().getName() + " : " + property + " " +anotherThreadRead)
			
			assertThat(null).isEqualTo(anotherThreadRead);
			
		}
		Thread.sleep(5000)
	}

}
