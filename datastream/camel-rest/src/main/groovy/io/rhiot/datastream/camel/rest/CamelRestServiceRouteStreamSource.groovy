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

import io.rhiot.bootstrap.Bootstrap
import io.rhiot.bootstrap.BootstrapAware
import io.rhiot.utils.WithLogger
import org.apache.camel.builder.RouteBuilder

import java.lang.reflect.Method

import static io.rhiot.datastream.engine.ServiceBinding.operationTransfersObject
import static io.rhiot.utils.Reflections.isJavaLibraryType

abstract class CamelRestServiceRouteStreamSource<T> extends RouteBuilder implements WithLogger, BootstrapAware {

    protected final Class<T> serviceClass

    protected final String serviceName

    protected Bootstrap bootstrap

    CamelRestServiceRouteStreamSource(Class<T> serviceClass, String serviceName) {
        this.serviceClass = serviceClass
        this.serviceName = serviceName
    }

    @Override
    void configure() {
        serviceClass.declaredMethods.each { Method operation ->
            def operationPath = "/${operation.name}"
            operation.parameterTypes.eachWithIndex { param, i ->
                if(isJavaLibraryType(param)) {
                    operationPath += "/{arg${i}}"
                }
            }

            def verb = operationTransfersObject(operation) ? 'POST' : 'GET'
            rest(serviceName).verb(verb, operationPath).route().process {
                def operationChannel = "${serviceName}/${operation}"
                for(int i = 0; i < 10; i++) {
                    def parameter = it.in.headers["arg${i}"]
                    if(parameter == null) {
                        break
                    }
                    operationChannel += ".${parameter}"
                }
                it.setProperty('target', operationChannel)
            }
        }
    }

    @Override
    void bootstrap(Bootstrap bootstrap) {
        this.bootstrap = bootstrap
    }

}