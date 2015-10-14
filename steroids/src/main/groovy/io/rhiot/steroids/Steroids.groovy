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

import java.lang.annotation.Annotation

import static io.rhiot.utils.Properties.booleanProperty
import static io.rhiot.utils.Properties.stringProperty
import static com.google.common.base.Preconditions.checkNotNull
import static java.util.Optional.empty

/**
 * Central point of accessing the steroids beans.
 */
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

        def beans = inclusiveSubTypesOf(type)
        beans = classesMatchingConditions(beans)
        if(beans.isEmpty()) {
            return empty()
        }
        instantiate(beans.first())
    }

    static <T> List<T> beans(Class<T> type) {
        checkNotNull(type, 'Type of the beans cannot be null.')
        def classes = inclusiveSubTypesOf(type)
        classesMatchingConditions(classes).collect{ instantiate(it) }.findAll{it.isPresent()}.collect{it.get()}
    }

    static <T> List<T> beans(Class<T> type, Class<? extends Annotation> annotation) {
        checkNotNull(type, 'Type of the beans cannot be null.')
        def annotatedClasses = classpath.getTypesAnnotatedWith(annotation).toList()
        classesMatchingConditions(annotatedClasses).collect{ instantiate(it) }.findAll{it.isPresent()}.collect{it.get()}
    }

    // Helpers

    private static List<Class<?>> classesMatchingConditions(List<Class<?>> classes) {
        classes.findAll{ cls ->
            !cls.isAnnotationPresent(PropertyCondition.class) ||
                    booleanProperty(cls.getAnnotation(PropertyCondition.class).property()) }
    }

    private static List<Class<?>> inclusiveSubTypesOf(Class<?> type) {
        def subtypes = classpath.getSubTypesOf(type).toList()
        subtypes.add(type)
        subtypes
    }

    private static Optional<?> instantiate(Class<?> type) {
        try {
            Optional.of(type.newInstance())
        } catch (GroovyRuntimeException e) {
            empty()
        }
    }

}
