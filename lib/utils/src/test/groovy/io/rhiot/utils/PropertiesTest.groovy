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

import static com.google.common.truth.Truth.assertThat
import static io.rhiot.utils.Properties.booleanProperty
import static io.rhiot.utils.Properties.restoreSystemProperties
import static io.rhiot.utils.Properties.setBooleanProperty
import static io.rhiot.utils.Properties.stringProperty
import static io.rhiot.utils.Properties.setThreadIntProperty
import static io.rhiot.utils.Properties.setThreadStringProperty
import static io.rhiot.utils.Properties.intProperty

import static io.rhiot.utils.Uuids.uuid

import static org.slf4j.LoggerFactory.getLogger;


class PropertiesTest {
	
	private static final def LOG = getLogger(Networks.class)
	

    @Test
    void shouldReadSystemProperty() {
        // Given
        def property = uuid()
        def value = uuid()
        System.setProperty(property, value)

        // When
        def valueRead = stringProperty(property)

        // Then
        assertThat(valueRead).isEqualTo(value)
    }

    @Test
    void shouldReadPropertyFromFile() {
        assertThat(stringProperty('fromApp')).isEqualTo('I am from app!')
    }

    @Test
    void shouldRestoreProperties() {
        // Given
        def property = 'property'
        setBooleanProperty(property, true)

        // When
        restoreSystemProperties()

        // Then
        assertThat(booleanProperty(property, false)).isFalse()
    }
	
	@Test
	void shouldReadThreadProperty() {
		// Given
		def property = uuid()
		def value = uuid()

		LOG.debug(property + " " +value)

		setThreadStringProperty(property, value)

		// When
		def valueRead = stringProperty(property)

		// Then
		assertThat(valueRead).isEqualTo(value)

		property = uuid()
		value = uuid()

		valueRead = setThreadStringProperty(property,value)
		LOG.debug(property + " " +value)

		// Then
		assertThat(valueRead).isEqualTo(value)

		def thread = Thread.start {
			def anotherThreadRead = stringProperty(property)
			LOG.debug(property + " " +anotherThreadRead)

			assertThat(null).isEqualTo(anotherThreadRead);

		}
		Thread.sleep(2000)
	}


	@Test
	void shouldReadIntThreadProperty() {
		// Given
		def property = uuid()
		def value = 123

		LOG.debug(property + " " +value)

		setThreadIntProperty(property, value)

		// When
		def valueRead = intProperty(property)

		// Then
		assertThat(valueRead).isEqualTo(value)

		property = uuid()
		value = 345

		valueRead = setThreadIntProperty(property,value)
		LOG.debug( property + " " +value)

		// Then
		assertThat(valueRead).isEqualTo(value)

		def thread = Thread.start {
			def anotherThreadRead = intProperty(property)
			LOG.debug(property + " " +anotherThreadRead)

			assertThat(null).isEqualTo(anotherThreadRead);
			
		}
		Thread.sleep(2000)
	}

}
