/**
 * Licensed to the Camel Labs under one or more
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
package com.github.camellabs.iot.cloudlet.device.vertx

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import io.vertx.core.AsyncResult
import io.vertx.groovy.core.eventbus.Message
import io.vertx.groovy.core.http.HttpServerResponse
import io.vertx.groovy.ext.web.RoutingContext

import static java.lang.System.getenv


@CompileStatic
final class Vertxes {

    private static final ObjectMapper JACKSON = new ObjectMapper()

    private Vertxes() {
    }

    static HttpServerResponse jsonResponse(RoutingContext routingContext) {
        routingContext.response().putHeader("content-type", "application/json")
    }

    static void jsonResponse(RoutingContext routingContext, AsyncResult<Message> message) {
        if(message.succeeded()) {
            jsonResponse(routingContext).end(message.result().body().toString())
        } else {
            jsonResponse(routingContext).end(JACKSON.writeValueAsString([failure: message.cause().message]))
        }
    }

    static String parameter(RoutingContext routingContext, String parameter) {
        routingContext.request().getParam(parameter)
    }

    static def wrapIntoJsonResponse(Message message, String root, Object pojo) {
        def json = JACKSON.writeValueAsString(["${root}": pojo])
        message.reply(json)
    }

}
