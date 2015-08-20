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
package com.github.camellabs.iot.vertx

import groovy.transform.CompileStatic

import static java.lang.System.getenv

@CompileStatic
final class PropertyResolver {

    private PropertyResolver() {
    }

    static String stringProperty(String key, String defaultValue) {
        def property = System.getProperty(key)
        if (property != null) {
            return property
        }

        property = getenv(key)
        if (property != null) {
            return property
        }

        return defaultValue
    }

    static String stringProperty(String key) {
        stringProperty(key, null)
    }

    static Integer intProperty(String key) {
        def property = stringProperty(key)
        property == null ? null : property.toInteger()
    }

    static int intProperty(String key, int defaultValue) {
        def property = stringProperty(key)
        property == null ? defaultValue : property.toInteger()
    }

    static Long longProperty(String key) {
        def property = stringProperty(key)
        property == null ? null : property.toLong()
    }

    static long longProperty(String key, long defaultValue) {
        def property = stringProperty(key)
        property == null ? defaultValue : property.toLong()
    }

}
