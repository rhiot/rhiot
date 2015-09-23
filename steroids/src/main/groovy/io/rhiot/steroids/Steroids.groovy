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
package io.rhiot.steroids

import io.rhiot.utils.Properties
import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder

import static io.rhiot.utils.Properties.stringProperty
import static com.google.common.base.Preconditions.checkNotNull
import static java.util.Optional.empty

final class Steroids {

    static def APPLICATION_PACKAGE_PROPERTY = 'application_package'

    final private static Reflections classpath
    static {
        def classpathConfiguration = new ConfigurationBuilder().forPackages('io.rhiot')
        if(Properties.hasProperty(APPLICATION_PACKAGE_PROPERTY)) {
            classpathConfiguration.forPackages(stringProperty(APPLICATION_PACKAGE_PROPERTY))
        }
        classpath = new Reflections(classpathConfiguration)
    }

    private Steroids() {
    }

    static <T> Optional<T> bean(Class<T> type) {
        checkNotNull(type, 'Type of the bean cannot be null.')

        def beans = classpath.getSubTypesOf(type).toList()
        if(beans.isEmpty()) {
            return empty()
        }
        Optional.of(beans.first().newInstance())
    }

    static <T> List<T> beans(Class<T> type) {
        checkNotNull(type, 'Type of the beans cannot be null.')
        classpath.getSubTypesOf(type).toList().collect{ it.newInstance() }
    }

    static <T> List<T> beans(Class<T> type, Class<?> annotation) {
        checkNotNull(type, 'Type of the beans cannot be null.')
        classpath.getTypesAnnotatedWith(annotation).toList().collect{ it.newInstance() }
    }

}
