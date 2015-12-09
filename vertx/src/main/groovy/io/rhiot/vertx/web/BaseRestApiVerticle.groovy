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
package io.rhiot.vertx.web

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.http.HttpMethod
import io.vertx.groovy.core.eventbus.Message
import io.vertx.groovy.core.http.HttpServer
import io.vertx.groovy.core.http.HttpServerResponse
import io.vertx.groovy.ext.web.Router
import io.vertx.groovy.ext.web.RoutingContext
import io.vertx.groovy.ext.web.handler.BodyHandler
import io.vertx.groovy.ext.web.handler.CorsHandler
import io.vertx.lang.groovy.GroovyVerticle

import static io.rhiot.utils.Properties.intProperty
import static io.rhiot.vertx.jackson.Jacksons.json
import static io.vertx.core.http.HttpMethod.DELETE
import static io.vertx.core.http.HttpMethod.GET
import static io.vertx.core.http.HttpMethod.OPTIONS
import static io.vertx.core.http.HttpMethod.POST
import static io.vertx.groovy.ext.web.Router.router
import static org.apache.commons.lang3.StringUtils.isNotBlank

abstract class BaseRestApiVerticle extends GroovyVerticle {

    static def PROPERTY_REST_API_PORT = 'api_rest_port'

    protected HttpServer http

    protected Router router

    protected Closure restApi

    // Initialization

    @Override
    void start(Future<Void> startFuture) {
        vertx.runOnContext {
            http = vertx.createHttpServer()
            router = router(vertx)

            router.route().handler(new HttpExchangeInterceptorHandler())
            router.route().handler(CorsHandler.create('*').
                    allowedMethod(OPTIONS).allowedMethod(GET).allowedMethod(POST).allowedMethod(DELETE).
                    allowedHeaders(['Origin', 'Accept', 'X-Requested-With', 'Content-Type', 'Access-Control-Request-Method', 'Access-Control-Request-Headers', 'Authorization'].toSet()))

            router.route().handler(BodyHandler.create())

            http.requestHandler(router.&accept).listen(intProperty(PROPERTY_REST_API_PORT, 15000))

            restApi(this)

            startFuture.complete()
        }
    }

    protected restApi(Closure restApi) {
        this.restApi = restApi
    }

    // REST DSL

    def forMethods(String uri, String channel, HttpMethod... method) {
        def route = router.route(uri)
        method.each { route.method(it) }
        route.handler { rc ->
            def message = null
            if(rc.request().params().size() == 1) {
                def parameterName = rc.request().params().names().first()
                message = rc.request().getParam(parameterName)
            } else if(isNotBlank(rc.bodyAsString)) {
                def jsonBody = rc.bodyAsJson
                if(jsonBody != null) {
                    message = jsonBody
                }
            }
            vertx.eventBus().send(channel, message, { result -> jsonResponse(rc, result) })
        }
    }

    def options(String uri, String channel) {
        forMethods(uri, channel, OPTIONS)
    }

    def get(String uri, String channel) {
        forMethods(uri, channel, GET)
    }

    def post(String uri, String channel) {
        forMethods(uri, channel, POST)
    }

    def delete(String uri, String channel) {
        forMethods(uri, channel, DELETE)
    }

    // Helpers

    static HttpServerResponse jsonResponse(RoutingContext routingContext) {
        routingContext.response().putHeader("content-type", "application/json")
    }

    protected void jsonResponse(RoutingContext routingContext, AsyncResult<Message> message) {
        if(message.succeeded()) {
            jsonResponse(routingContext).end(message.result().body().toString())
        } else {
            jsonResponse(routingContext).end(json().writeValueAsString([failure: message.cause().message]))
        }
    }

}
