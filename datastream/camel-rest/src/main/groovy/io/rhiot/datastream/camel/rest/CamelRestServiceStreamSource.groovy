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

import static io.rhiot.datastream.engine.ServiceBinding.transfersObject
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

        def restRouteBuilder = new RouteBuilder() {
            @Override
            void configure() throws Exception {
                def operations = serviceClass.declaredMethods.findAll{ ['save', 'count'].contains(it.name) }
                operations.forEach { op ->
                    def operationPath = "/${op.name}"
                    op.parameterTypes.eachWithIndex { param, i ->
                        if(isJavaLibraryType(param)) {
                            operationPath += "/{arg${i}}"
                        }
                    }

                    def headers = "[operation: '${op.name}', "
                    for(int i = 0; i < op.parameterCount; i++) {
                        if(isJavaLibraryType(op.parameterTypes[i]))
                            headers += "arg${i}: headers['arg${i}'], "
                    }
                    headers = headers.substring(0, headers.size() - 2) + ']'

                    def verb = transfersObject(op) ? 'POST' : 'GET'
                        rest(serviceName).verb(verb, operationPath).route().
                                setBody().groovy("io.rhiot.datastream.engine.JsonWithHeaders.jsonWithHeaders(body, ${headers})").
                                process(new VertxProducer(bootstrap.beanRegistry().bean(Vertx.class).get(), serviceName.replaceFirst('api/', '')))
                }
            }
        }
//        if(!CamelBootInitializer.camelContext().getRegistry().findByType(RestConsumerFactory.class).isEmpty())
            CamelBootInitializer.camelContext().addRoutes(restRouteBuilder)
    }

}