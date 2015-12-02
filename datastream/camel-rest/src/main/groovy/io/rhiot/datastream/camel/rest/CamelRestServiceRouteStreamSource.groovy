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
import io.rhiot.steroids.camel.Route
import io.rhiot.utils.WithLogger
import org.apache.camel.builder.RouteBuilder

import static io.rhiot.steroids.activemq.EmbeddedActiveMqBrokerBootInitializer.amqp
import static org.apache.camel.Exchange.HTTP_URI

@Route
class CamelRestServiceRouteStreamSource<T> extends RouteBuilder implements WithLogger, BootstrapAware {

    private Bootstrap bootstrap

    @Override
    void configure() {
        from('netty4-http:http://0.0.0.0:8080/?matchOnUriPrefix=true').
                process {
                    def uri = it.in.getHeader(HTTP_URI, String.class)
                    def channel = uri.substring(1).replaceAll(/\//, '.')
                    it.setProperty('target', amqp(channel))
                }.recipientList().exchangeProperty('target')
    }

    @Override
    void bootstrap(Bootstrap bootstrap) {
        this.bootstrap = bootstrap
    }

}