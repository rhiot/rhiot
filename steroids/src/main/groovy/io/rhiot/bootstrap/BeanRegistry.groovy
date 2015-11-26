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
package io.rhiot.bootstrap
/**
 * Allows to access (and optionally register) beans. Provides a layer of abstraction above the IoC frameworks and
 * servers used by the end client.
 */
interface BeanRegistry {

    /**
     * Looks up for a bean by its type.
     *
     * @param type Type of bean to find in registry.
     * @return bean or empty optional if bean can't be found.
     * @throws TooManyBeansFoundException if more than a single bean of a given type have been found
     */
    def <T> Optional<T> bean(Class<T> type) throws TooManyBeansFoundException

    def Optional<?> bean(String name)

    def <T> Optional<T> bean(String name, Class<T> type)

    def <T> List<T> beans(Class<T> type)

    def <T> Map<String, T> beansWithNames(Class<T> type)

    // Mutable operations

    void register(Object bean) throws UnsupportedOperationException

    void register(String name, Object bean) throws UnsupportedOperationException

}