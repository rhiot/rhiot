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

import io.rhiot.datastream.engine.DataStream
import io.rhiot.datastream.engine.encoding.PayloadEncoding
import org.apache.camel.CamelContext
import org.apache.camel.component.spark.RddCallback
import org.apache.spark.api.java.AbstractJavaRDDLike
import org.apache.spark.api.java.JavaSparkContext
import org.junit.Test

import static com.google.common.truth.Truth.assertThat
import static io.rhiot.steroids.activemq.EmbeddedActiveMqBrokerBootInitializer.amqp

class CamelSparkStreamConsumerTest {

    static dataStream = new DataStream().start()
    static {
        def sparkContext = dataStream.beanRegistry().bean(JavaSparkContext.class).get()
        dataStream.beanRegistry().register('rdd', sparkContext.textFile('src/test/resources/testrdd.txt'))
        dataStream.beanRegistry().register('callback', new LinesXPayload())

    }

    @Test
    void shouldExecuteTaskViaAmqpApi() {
        // Given
        def payloadEncoding = dataStream.beanRegistry().bean(PayloadEncoding.class).get()
        def payload = payloadEncoding.encode(10)

        // When
        def encodedResult = dataStream.beanRegistry().bean(CamelContext.class).get().createProducerTemplate().requestBody(amqp('spark.execute.rdd.callback'), payload, byte[].class)
        def result = payloadEncoding.decode(encodedResult, int.class)

        // Then
        assertThat(result).isEqualTo(170)
    }

    static class LinesXPayload implements RddCallback<Long> {

        @Override
        Long onRdd(AbstractJavaRDDLike rdd, Object... payloads) {
            (long) payloads[0] * rdd.count()
        }
    }

}
