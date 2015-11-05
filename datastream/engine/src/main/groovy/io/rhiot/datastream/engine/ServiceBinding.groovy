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
package io.rhiot.datastream.engine

import com.thoughtworks.paranamer.AdaptiveParanamer
import com.thoughtworks.paranamer.AnnotationParanamer
import com.thoughtworks.paranamer.CachingParanamer
import com.thoughtworks.paranamer.Paranamer
import io.vertx.core.eventbus.Message

import java.lang.reflect.Method

class ServiceBinding {

    public static final String OPERATION_HEADER = 'operation'

    private Paranamer paranamer

    ServiceBinding(paranamer) {
        this.paranamer = paranamer
    }

    ServiceBinding() {
        this(new CachingParanamer(new AdaptiveParanamer(new AnnotationParanamer())))
    }

    Optional<Method> findOperation(Class<?> service, Message message) {
        def operationName = message.headers().get(OPERATION_HEADER)
        Optional.ofNullable(service.declaredMethods.find { it.name == operationName })
    }

    Object[] findArguments(Method operation, Message message) {
        def arguments = paranamer.lookupParameterNames(operation, false).collect{ parameter ->
            message.headers().get(parameter)
        }.toList()

        if(operation.parameterCount > arguments.size()) {
            arguments << message.body()
        }
        arguments
    }

    Object invokeOperation(Class<?> serviceType, Object service, Message message) {
        def operation = findOperation(serviceType, message)
        if(!operation.isPresent()) {
            throw new IllegalArgumentException(
                    "Could not find operation ${message.headers().get(OPERATION_HEADER)} in service ${serviceType.name}.")
        }
        operation.get().invoke(service, findArguments(operation.get(), message))
    }

}