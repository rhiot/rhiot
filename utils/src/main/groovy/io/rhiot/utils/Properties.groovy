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

import groovy.transform.CompileStatic
import java.lang.ThreadLocal as JThreadLocal
import java.util.Properties as JProperties

import static java.lang.System.getenv

@CompileStatic
final class Properties {

    // Members

    private static final JProperties applicationPropertiesFile = new JProperties()
	private static final JThreadLocal threadLocalProperties = new ThreadLocal<JProperties>()
	
    private static JProperties propertiesSnapshot
    static {
        saveSystemProperties()
    }

    static {
        def propertiesStream = Properties.class.getResourceAsStream('/application.properties')
        if(propertiesStream != null) {
            applicationPropertiesFile.load(propertiesStream)
        }
    }

    // Constructors

    private Properties() {
    }

    static boolean hasProperty(String key) {
        stringProperty(key) != null
    }

    // String properties

    static String stringProperty(String key, String defaultValue) {
        def property = System.getProperty(key)
        if (property != null) {
            return property
        }

        property = getenv(key)
        if (property != null) {
            return property
        }

        applicationPropertiesFile.getProperty(key, defaultValue)
    }

    static String stringProperty(String key) {
        stringProperty(key, null)
    }

    static String setStringProperty(String key, String value) {
        System.setProperty(key, value)
    }

    // Integer properties

    static Integer intProperty(String key) {
        def property = stringProperty(key)
        property == null ? null : property.toInteger()
    }

    static int intProperty(String key, int defaultValue) {
        def property = stringProperty(key)
        property == null ? defaultValue : property.toInteger()
    }

    static void setIntProperty(String key, int value) {
        System.setProperty(key, "${value}")
    }

    // Long properties

    static long longProperty(String key, long defaultValue) {
        def property = stringProperty(key)
        property == null ? defaultValue : property.toLong()
    }

    static Optional<Long> longProperty(String key) {
        def property = stringProperty(key)
        property == null ? Optional.empty() : Optional.of(property.toLong())
    }

    static void setLongProperty(String key, long value) {
        System.setProperty(key, "${value}")
    }

    // Boolean properties

    static Boolean booleanProperty(String key) {
        def property = stringProperty(key)
        property == null ? null : property.toBoolean()
    }

    static boolean booleanProperty(String key, boolean defaultValue) {
        def property = stringProperty(key)
        property == null ? defaultValue : property.toBoolean()
    }

    static void setBooleanProperty(String key, boolean value) {
        System.setProperty(key, "${value}")
    }
	
	// Import/export

    static void saveSystemProperties() {
        propertiesSnapshot = new JProperties()
        propertiesSnapshot.putAll(System.getProperties())
    }

    static void restoreSystemProperties() {
        System.setProperties(propertiesSnapshot)
    }

	// ThreadLocal properties
	
	
	static JProperties getThreadLocalJProperties() {
		def prop = threadLocalProperties.get();
		
		prop == null ? threadLocalProperties.set(new JProperties()) : void ;
		
		threadLocalProperties.get();
	}
	
	static String setThreadStringProperty(String key, String value) {
		getThreadLocalJProperties().put(key,value);
		threadStringProperty(key)
	}
	
	static String threadStringProperty(String key) {
		getThreadLocalJProperties().get(key);
	}
	
	static int setThreadIntProperty(String key, int value) {
		setThreadStringProperty(key,"${value}").toInteger();
	}
	
	static Integer threadIntProperty(String key) {
		def property = threadStringProperty(key)
		property == null ? null : property.toInteger()
	}
	
	static int threadIntProperty(String key, int defaultValue) {
		def property = threadIntProperty(key)
		property == null ? defaultValue : property
	}
	
	static boolean setThreadBooleanProperty(String key, boolean value) {
		setThreadStringProperty(key,"${value}").toBoolean();
	}
	
	static Boolean threadBooleanProperty(String key) {
		def property = threadStringProperty(key)
		property == null ? null : property.toBoolean()
	}
	
	static boolean threadBooleanProperty(String key, boolean defaultValue) {
		def property = threadBooleanProperty(key)
		property == null ? defaultValue : property
	}
	
	static boolean setThreadLongProperty(String key, long value) {
		setThreadStringProperty(key,"${value}").toLong();
	}
	
	static Long threadLongProperty(String key) {
		def property = threadStringProperty(key)
		property == null ? null : property.toLong()
	}
	
	static long threadLongProperty(String key, long defaultValue) {
		def property = threadLongProperty(key)
		property == null ? defaultValue : property
	}
	
}
