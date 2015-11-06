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

import io.rhiot.utils.WithLogger
import io.vertx.core.eventbus.Message
import io.vertx.core.json.Json

import java.lang.reflect.Method

import static io.rhiot.utils.Reflections.isJavaLibraryType

/**
 * Binds Vert.x message with a service invocation.
 */
class ServiceBinding implements WithLogger {

    public static final String OPERATION_HEADER = 'operation'

    Object[] findArguments(Method operation, Message message) {
        def arguments = operation.parameters.collect { parameter ->
            message.headers().get(parameter.name)
        }.findAll { it != null }.toList()

        if (operation.parameterCount > arguments.size()) {
            if(List.isAssignableFrom(operation.parameterTypes.last())) {
                def wrappedList = Json.decodeValue((String) message.body(), Map.class).values().first()
                arguments << Json.mapper.convertValue(wrappedList, operation.parameterTypes.last())
            } else {
                arguments << Json.decodeValue((String) message.body(), operation.parameterTypes.last())
            }
        }
        arguments
    }

    Object invokeOperation(Class<?> serviceType, Object service, Message message) {
        def operation = findOperation(serviceType, message)
        if (!operation.isPresent()) {
            throw new IllegalArgumentException(
                    "Could not find operation ${message.headers().get(OPERATION_HEADER)} in service ${serviceType.name}.")
        }
        operation.get().invoke(service, findArguments(operation.get(), message))
    }

    // Operations

    void handleOperation(Class<?> serviceType, Object service, Message message) {
        def response = invokeOperation(serviceType, service, message)
        def returnType = findOperation(serviceType, message).get().returnType
        if(returnType != Void.class) {
            if(isJavaLibraryType(returnType)) {
                log().debug('Java library type {} - will be wrapped into JSON envelope.', returnType)
                message.reply(Json.encode([result: response]))
            } else {
                message.reply(Json.encode(response))
            }
        }
    }

    // Helpers

    static Optional<Method> findOperation(Class<?> service, Message message) {
        def operationName = message.headers().get(OPERATION_HEADER)
        Optional.ofNullable(service.declaredMethods.find { it.name == operationName })
    }

}