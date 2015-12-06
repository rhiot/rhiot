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
package io.rhiot.datastream.source.rest.camel

import io.rhiot.datastream.engine.AbstractCamelStreamSource
import io.rhiot.steroids.camel.Route
import io.vertx.core.json.Json

import static io.rhiot.steroids.activemq.EmbeddedActiveMqBrokerBootInitializer.amqp
import static io.rhiot.utils.Properties.intProperty
import static org.apache.camel.Exchange.CONTENT_TYPE
import static org.apache.camel.Exchange.EXCEPTION_CAUGHT
import static org.apache.camel.Exchange.HTTP_URI
import static org.apache.commons.lang3.StringUtils.countMatches
import static org.apache.commons.lang3.StringUtils.removeEnd

@Route
class CamelRestStreamSource extends AbstractCamelStreamSource {

    // Constants

    static final def URI_TOO_SHORT_MESSAGE = 'URI too short. The proper request format is /service/operation[/argument1/argument2/...]'

    // Routes

    @Override
    void configure() {
        def httpPort = intProperty('http_port', 8080)
        log.debug('Started REST data stream source at port {}.', httpPort)

        onException(Exception.class).handled(true).
                transform{Json.encode([error: it.getProperty(EXCEPTION_CAUGHT, Exception.class).message])}

        from("netty4-http:http://0.0.0.0:${httpPort}/?matchOnUriPrefix=true").
                setHeader(CONTENT_TYPE).constant('application/json').
                process {
                    def requestUri = it.in.getHeader(HTTP_URI, String.class)
                    def trimmedUri = removeEnd(requestUri, '/')
                    if(countMatches(trimmedUri, '/') < 2) {
                        throw new IllegalArgumentException(URI_TOO_SHORT_MESSAGE)
                    }
                    def busChannel = trimmedUri.substring(1).replaceAll(/\//, '.')
                    it.setProperty('target', amqp(busChannel))
                }.recipientList().exchangeProperty('target')
    }

}