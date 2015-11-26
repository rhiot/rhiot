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
package io.rhiot.steroids.camel

import io.rhiot.bootstrap.BeanRegistry
import org.apache.camel.spi.Registry

class BootstrapRegistry implements Registry {

    private final BeanRegistry beanRegistry

    BootstrapRegistry(BeanRegistry beanRegistry) {
        this.beanRegistry = beanRegistry
    }

    @Override
    Object lookupByName(String name) {
        beanRegistry.bean(name).orElse(null)
    }

    @Override
    def <T> T lookupByNameAndType(String name, Class<T> type) {
        beanRegistry.bean(name, type).orElse(null)
    }

    @Override
    def <T> Map<String, T> findByTypeWithName(Class<T> type) {
        beanRegistry.beansWithNames(type)
    }

    @Override
    def <T> Set<T> findByType(Class<T> type) {
        beanRegistry.beans(type).toSet()
    }

    @Override
    Object lookup(String name) {
        beanRegistry.bean(name)
    }

    @Override
    def <T> T lookup(String name, Class<T> type) {
        beanRegistry.bean(name, type).orElse(null)
    }

    @Override
    def <T> Map<String, T> lookupByType(Class<T> type) {
        beanRegistry.beansWithNames(type)
    }

}