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

import org.apache.camel.CamelContext
import org.apache.camel.ServiceStatus
import org.apache.camel.builder.RouteBuilder

class CamelRestEndpoint {

    static boolean started

    static void startCamelRestEndpoint(CamelContext camelContext, RouteBuilder routeBuilder) {
        if(!started) {
            routeBuilder.restConfiguration().component("netty4-http").
                    host("0.0.0.0").port(8080)
            if(camelContext.status == ServiceStatus.Started) {
                def components = camelContext.componentNames.collect {
                    [it, camelContext.getComponent(it)]
                }
                camelContext.stop()
                components.each { camelContext.addComponent(it[0], it[1]) }
                camelContext.start()
            }
            started = true
        }
    }

}
