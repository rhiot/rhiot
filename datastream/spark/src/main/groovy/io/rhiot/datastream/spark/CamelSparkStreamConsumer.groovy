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
package io.rhiot.datastream.spark

import io.rhiot.steroids.camel.Route
import io.vertx.core.json.Json
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.jms.JmsMessage

import static io.rhiot.steroids.activemq.EmbeddedActiveMqBrokerBootInitializer.amqpByPrefix

@Route
class CamelSparkStreamConsumer extends RouteBuilder {

    public static final String CHANNEL = 'spark'

    @Override
    void configure() {
        from(amqpByPrefix(CHANNEL)).
                process {
                    def channel = it.getIn(JmsMessage.class).jmsMessage.properties._to.toString()
                    def rawChannel = channel.substring(channel.indexOf('.') + 1)
                    def channelParts = rawChannel.split(/\./)
                    def rdd = channelParts[0]
                    def rddCallback = channelParts[1]
                    it.in.headers.target = "spark:rhiot?rdd=#${rdd}&rddCallback=#${rddCallback}"

                    it.in.body = Json.decodeValue(it.in.getBody(String.class), Map.class).payload
                }.recipientList().header('target').
                transform{ Json.encode([payload: it.in.body]) }
    }

}