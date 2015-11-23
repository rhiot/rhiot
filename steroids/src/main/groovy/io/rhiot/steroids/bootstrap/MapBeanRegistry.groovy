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
package io.rhiot.steroids.bootstrap

import static io.rhiot.utils.Uuids.uuid
import static java.util.Optional.empty

class MapBeanRegistry implements BeanRegistry {

    protected final registry = [:]

    @Override
    def <T> Optional<T> bean(Class<T> type) {
        def beans = registry.values().findAll{ type.isAssignableFrom(it.class) }
        if(beans.size() > 1) {
            throw new TooManyBeansFoundException("Expected 0 or 1 beans of type ${type.name}. Found ${beans.size()}.")
        } else if(beans.size() == 1) {
            Optional.of((T) beans.first())
        } else {
            empty()
        }
    }

    @Override
    Optional<?> bean(String name) {
        Optional.ofNullable(registry[name])
    }

    @Override
    def <T> Optional<T> bean(String name, Class<T> type) {
        def bean = registry[name]
        Optional.ofNullable(type.isAssignableFrom(bean.class) ? bean : null)
    }

    @Override
    def <T> List<T> beans(Class<T> type) {
        registry.values().findAll{ type.isAssignableFrom(it.class) }
    }

    @Override
    void register(Object bean) {
        registry.put(bean.class.name + uuid(), bean)
    }

    @Override
    void register(String name, Object bean) {
        registry.put(name, bean)
    }

}