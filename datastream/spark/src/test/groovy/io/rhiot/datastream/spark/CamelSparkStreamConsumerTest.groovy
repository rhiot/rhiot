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

import io.rhiot.datastream.engine.test.DataStreamTest
import org.apache.camel.component.spark.RddCallback
import org.apache.spark.api.java.AbstractJavaRDDLike
import org.apache.spark.api.java.JavaSparkContext
import org.junit.Test

import static com.google.common.truth.Truth.assertThat

class CamelSparkStreamConsumerTest extends DataStreamTest {

    @Override
    protected void afterDataStreamStarted() {
        def sparkContext = beanRegistry.bean(JavaSparkContext.class).get()
        beanRegistry.register('rdd', sparkContext.textFile('src/test/resources/testrdd.txt'))
        beanRegistry.register('callback', new LinesXPayload())
    }

    @Test
    void shouldExecuteTaskViaAmqpApi() {
        def result = fromBus('spark.execute.rdd.callback', 10, int.class)
        assertThat(result).isEqualTo(170)
    }

    static class LinesXPayload implements RddCallback<Long> {

        @Override
        Long onRdd(AbstractJavaRDDLike rdd, Object... payloads) {
            (long) payloads[0] * rdd.count()
        }
    }

}
