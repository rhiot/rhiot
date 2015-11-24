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

import io.rhiot.datastream.engine.ServiceBinding
import io.rhiot.datastream.engine.encoding.PayloadEncoding
import io.rhiot.steroids.bootstrap.Bootstrap
import io.rhiot.steroids.bootstrap.BootstrapAware
import io.rhiot.steroids.camel.CamelBootInitializer
import io.rhiot.steroids.camel.Route
import io.vertx.core.json.Json
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.jms.JmsMessage

import static io.rhiot.steroids.activemq.EmbeddedActiveMqBrokerBootInitializer.amqpByPrefix

@Route
class CamelSparkStreamConsumer extends RouteBuilder implements BootstrapAware {

    public static final String CHANNEL = 'spark'

    private Bootstrap bootstrap

    @Override
    void configure() {
        def encoding = bootstrap.beanRegistry().bean(PayloadEncoding.class).get()
        def serviceBinding = bootstrap.beanRegistry().bean(ServiceBinding.class).get()
        def sparkService = bootstrap.beanRegistry().bean(SparkService.class).get()
        sparkService.asType(DefaultSparkService.class).bootstrap(bootstrap)
        CamelBootInitializer.registry().put('sparkService', sparkService)

        from(amqpByPrefix(CHANNEL)).
                transform {
                    def channel = it.getIn(JmsMessage.class).jmsMessage.properties._to.toString()
                    def rawChannel = channel.substring(channel.indexOf('.') + 1)
                    def channelParts = rawChannel.split(/\./)
                    def rdd = channelParts[0]
                    def rddCallback = channelParts[1]
                    [rdd, rddCallback, Json.decodeValue(it.in.getBody(String.class), Map.class).payload]
                }.to('bean:sparkService?method=execute&multiParameterArray=true').
                transform{ encoding.encode(it.in.body) }
    }

    @Override
    void bootstrap(Bootstrap bootstrap) {
        this.bootstrap = bootstrap
    }

}