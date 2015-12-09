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
package io.rhiot.utils;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

import static java.lang.Character.toLowerCase;
import static java.lang.String.format;
import static org.apache.commons.lang3.reflect.FieldUtils.getField;

/**
 * Utilities related to the Java Reflection API.
 */
public final class Reflections {

    private Reflections() {
    }

    @SuppressWarnings("unchecked") // Array#newInstance return Object
    public static <T> Class<T[]> classOfArrayOfClass(Class<T> clazz) {
        return (Class<T[]>) Array.newInstance(clazz, 0).getClass();
    }

    public static void writeField(Object object, String field, Object value) {
        try {
            FieldUtils.writeField(object, field, value, true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readField(Object object, String field, Class<T> type) {
        try {
            Field actualField = getField(object.getClass(), field, true);
            if (!isInstanceOfOrWrappable(actualField.getType(), type)) {
                String message = format("Field %s is a type of %s instead of %s.", field, actualField.getType(), type);
                throw new IllegalStateException(message);
            }
            return (T) FieldUtils.readField(actualField, object, true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void runMain(Class<?> classWithMain, String... args) {
        try {
            Method mainMethod = classWithMain.getMethod("main", String[].class);
            if(mainMethod == null) {
                throw new IllegalArgumentException("No main method in class " + classWithMain.getName());
            }
            mainMethod.invoke(null, new Object[]{args});
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Map<Class<?>, Class<?>> wrapperClasses = Maps.immutableMapOf(
            int.class, Integer.class,
            long.class, Long.class,
            short.class, Short.class,
            byte.class, Byte.class,
            char.class, Character.class,
            float.class, Float.class,
            double.class, Double.class);

    public static boolean isInstanceOfOrWrappable(Class<?> type, Class<?> instanceOf) {
        if (instanceOf.isAssignableFrom(type)) {
            return true;
        } else {
            type = wrapperClasses.get(type);
            return instanceOf.isAssignableFrom(type);
        }
    }

    public static String classNameToCamelCase(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        return toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }

    public static boolean isNumber(Class<?> type) {
        return wrapperClasses.containsKey(type) || wrapperClasses.containsValue(type);
    }

    public static boolean isJavaLibraryType(Class<?> type) {
        return isNumber(type) || type == String.class || type == Date.class;
    }

}
