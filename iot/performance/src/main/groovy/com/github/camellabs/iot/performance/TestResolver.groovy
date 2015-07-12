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

import org.reflections.Reflections

import static java.lang.reflect.Modifier.isAbstract

class TestResolver {

    def reflections = new Reflections(getClass().getPackage().getName())

    List<TestSpecification> testsForKit(String kit) {
        reflections.getSubTypesOf(TestSpecification.class).
                findAll { !isAbstract(it.modifiers) }.
                collect { it.newInstance() }.findAll { test -> test.supportsHardwareKit(kit) }.
                sort { it.variationLabel() }
    }

}
