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
package io.rhiot.bootstrap.classpath

import io.rhiot.steroids.Bean
import io.rhiot.steroids.Named
import io.rhiot.steroids.PropertyCondition
import io.rhiot.utils.Properties
import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.util.ConfigurationBuilder

import java.lang.annotation.Annotation

import static io.rhiot.utils.Properties.booleanProperty
import static io.rhiot.utils.Properties.stringProperty
import static com.google.common.base.Preconditions.checkNotNull
import static java.lang.reflect.Modifier.isAbstract
import static java.util.Optional.empty

/**
 * Central point of accessing the steroids beans.
 */
final class ClasspathBeans {

    static final def RHIOT_PACKAGE = 'io.rhiot'

    static final def APPLICATION_PACKAGE_PROPERTY = 'application_package'

    final private static Reflections classpath
    static {
        def classpathConfiguration = new ConfigurationBuilder().forPackages(RHIOT_PACKAGE)
        if(Properties.hasProperty(APPLICATION_PACKAGE_PROPERTY)) {
            classpathConfiguration.forPackages(stringProperty(APPLICATION_PACKAGE_PROPERTY))
        }
        classpathConfiguration.addScanners(new MethodAnnotationsScanner())
        classpath = new Reflections(classpathConfiguration)
    }

    // Constructors

    private ClasspathBeans() {
    }

    // Scanning operations

    static <T> Optional<T> bean(Class<T> type, Class<? extends Annotation> annotation, String name) {
        checkNotNull(type, 'Type of the bean cannot be null.')

        def beans = scanForBeans(type, annotation, name)
        if(beans.isEmpty()) {
            return empty()
        }
        Optional.of(beans.first())
    }

    static <T> Optional<T> bean(Class<T> type, Class<? extends Annotation> annotation) {
        bean(type, annotation, null)
    }

    static <T> Optional<T> bean(Class<T> type) {
        bean(type, (String) null)
    }

    static <T> Optional<T> bean(Class<T> type, String name) {
        bean(type, Bean.class, name)
    }

    static <T> Optional<T> bean(String name) {
        def beans = scanForNamedBeans(name)
        if(beans.isEmpty()) {
            return empty()
        }
        Optional.of(beans.first())
    }

    static <T> List<T> beans(Class<T> type, Class<? extends Annotation> annotation) {
        scanForBeans(type, annotation, null)
    }

    static <T> List<T> beans(Class<T> type) {
        beans(type, Bean.class)
    }

    // Helpers

    private static List<Class<?>> classesMatchingConditions(List<Class<?>> classes) {
        classes.findAll{ cls ->
            !cls.isAnnotationPresent(PropertyCondition.class) ||
                    booleanProperty(cls.getAnnotation(PropertyCondition.class).property()) }
    }

    private static List<Class<?>> inclusiveSubTypesOf(Class<?> type, Class<? extends Annotation> annotation) {
        def subtypes = classpath.getTypesAnnotatedWith(annotation).findAll{ type.isAssignableFrom(it) }.findAll{ !isAbstract(it.class.getModifiers()) }.toList()
        if(type.isAnnotationPresent(Bean.class)) {
            subtypes.add(type)
        }
        subtypes
    }

    private static List<Object> createdByFactories(Class<?> type) {
        def factories = classpath.getMethodsAnnotatedWith(Bean.class).findAll{ type.isAssignableFrom(it.returnType) }.toList()
        factories.collect {
            def instance = instantiate(it.declaringClass)
            if(!instance.isPresent()) {
                return null
            }
            it.invoke(instance.get())
        }.findAll{ it != null }
    }

    private static List<Object> scanForBeans(Class<?> type, Class<? extends Annotation> annotation, String name) {
        def beansClasses = inclusiveSubTypesOf(type, annotation)
        beansClasses = classesMatchingConditions(beansClasses)
        if(name != null) {
            beansClasses = beansClasses.findAll { it.getAnnotation(Named.class).name() == name }
        }
        def beans = beansClasses.collect{ instantiate(it) }.findAll{ it.isPresent() }.collect{ it.get() }

        beans + (type != null ? createdByFactories(type) : [])
    }

    private static List<Object> scanForNamedBeans(String name) {
        def beansClasses = instantiableOnly(classpath.getTypesAnnotatedWith(Named.class).toList())
        beansClasses = classesMatchingConditions(beansClasses)
        beansClasses = beansClasses.findAll { it.getAnnotation(Named.class).name() == name }
        beansClasses.collect{ instantiate(it) }.findAll{ it.isPresent() }.collect{ it.get() }
    }

    private static List<Class> instantiableOnly(List<Class> classes) {
        classes.findAll{ !isAbstract(it.getModifiers()) && !it.interface }
    }

    private static Optional<?> instantiate(Class<?> type) {
        try {
            Optional.of(type.newInstance())
        } catch (GroovyRuntimeException e) {
            empty()
        } catch (InstantiationException e) {
            empty()
        }
    }

}