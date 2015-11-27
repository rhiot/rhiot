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
package io.rhiot.datastream.camel.rest

import io.rhiot.datastream.engine.AbstractServiceStreamSource
import io.rhiot.steroids.camel.CamelBootInitializer
import io.vertx.core.Vertx
import org.apache.camel.builder.RouteBuilder

import java.lang.reflect.Method

import static io.rhiot.datastream.camel.rest.CamelRestEndpoint.startCamelRestEndpoint
import static io.rhiot.datastream.engine.ServiceBinding.operationTransfersObject
import static io.rhiot.utils.Reflections.isJavaLibraryType

abstract class CamelRestServiceStreamSource<T> extends AbstractServiceStreamSource<T> {

    protected final String serviceName

    CamelRestServiceStreamSource(Class<T> serviceClass, String serviceName) {
        super(serviceClass)
        this.serviceName = serviceName
    }

    @Override
    void start() {
        super.start()
        serviceClass.declaredMethods.findAll{ ['save', 'count', 'findOne'].contains(it.name) }.forEach{ Method op -> registerOperationEndpoint(op) }
    }

    @Override
    void registerOperationEndpoint(Method operation) {
        def restRouteBuilder = new RouteBuilder() {
            @Override
            void configure() throws Exception {
                    def operationPath = "/${operation.name}"
                    operation.parameterTypes.eachWithIndex { param, i ->
                        if(isJavaLibraryType(param)) {
                            operationPath += "/{arg${i}}"
                        }
                    }

                    def headers = "[operation: '${operation.name}', "
                    for(int i = 0; i < operation.parameterCount; i++) {
                        if(isJavaLibraryType(operation.parameterTypes[i]))
                            headers += "arg${i}: headers['arg${i}'], "
                    }
                    headers = headers.substring(0, headers.size() - 2) + ']'

                    def verb = operationTransfersObject(operation) ? 'POST' : 'GET'
                    def body = operationTransfersObject(operation) ? 'body' : 'null'
                    rest(serviceName).verb(verb, operationPath).route().
                            setBody().groovy("io.rhiot.datastream.engine.JsonWithHeaders.jsonWithHeaders(${body}, ${headers})").
                            process(new VertxProducer(bootstrap.beanRegistry().bean(Vertx.class).get(), serviceName.replaceFirst('api/', '')))
            }
        }
        startCamelRestEndpoint(restRouteBuilder)
    }

}