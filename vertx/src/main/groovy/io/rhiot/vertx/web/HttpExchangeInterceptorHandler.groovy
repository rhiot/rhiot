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
package io.rhiot.vertx.web

import io.vertx.core.Handler
import io.vertx.groovy.ext.web.RoutingContext

import static io.rhiot.steroids.Steroids.beans
import static org.slf4j.LoggerFactory.getLogger

class HttpExchangeInterceptorHandler implements Handler<RoutingContext> {

    private static final def LOG = getLogger(HttpExchangeInterceptorHandler.class)

    def interceptors = beans(HttpExchangeInterceptor.class)

    @Override
    void handle(RoutingContext event) {
        for(HttpExchangeInterceptor interceptor : interceptors) {
            try {
                interceptor.intercept(event)
            } catch (Exception ex) {
                LOG.debug('Stopping HTTP interceptor chain. Reason:', ex)
                break
            }
        }
        event.next()
    }

}