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
package org.apache.camel.component.spark;

import com.google.common.truth.Truth;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.camel.component.spark.SparkConstants.SPARK_RDD_CALLBACK_HEADER;
import static org.apache.camel.component.spark.SparkConstants.SPARK_TRANSFORMATION_HEADER;
import static org.apache.camel.component.spark.SparkTransformation.MAP;
import static org.apache.camel.component.spark.Sparks.createLocalSparkContext;

public class SparkProducerTest extends CamelTestSupport {

    // Fixtures

    static JavaSparkContext sparkContext = createLocalSparkContext();

    // Tests

    @Test
    public void shouldExecuteRddCallback() {
        long pomLinesCount = template.requestBodyAndHeader("spark:analyze?rdd=#pomRdd", null, SPARK_RDD_CALLBACK_HEADER, new RddCallback<Long>() {
            @Override
            public Long onRdd(JavaRDD rdd, Object... payloads) {
                return rdd.count();
            }
        }, Long.class);
        Truth.assertThat(pomLinesCount).isEqualTo(17);
    }

    @Test
    public void shouldExecuteRddCallbackWithSinglePayload() {
        long pomLinesCount = template.requestBodyAndHeader("spark:analyze?rdd=#pomRdd&sparkContext=#sparkContext", 10, SPARK_RDD_CALLBACK_HEADER, new RddCallback<Long>() {
            @Override
            public Long onRdd(JavaRDD rdd, Object... payloads) {
                return rdd.count() * (int) payloads[0];
            }
        }, Long.class);
        Truth.assertThat(pomLinesCount).isEqualTo(170);
    }

    @Test
    public void shouldExecuteRddCallbackWithPayloads() {
        long pomLinesCount = template.requestBodyAndHeader("spark:analyze?rdd=#pomRdd&sparkContext=#sparkContext", asList(10, 10), SPARK_RDD_CALLBACK_HEADER, new RddCallback<Long>() {
            @Override
            public Long onRdd(JavaRDD rdd, Object... payloads) {
                return rdd.count() * (int) payloads[0] * (int) payloads[1];
            }
        }, Long.class);
        Truth.assertThat(pomLinesCount).isEqualTo(1700);
    }

    @Test
    public void shouldExecuteRddCallbackWithTypedPayloads() {
        TypedRddCallback rddCallback = new TypedRddCallback<Long>(context, new Class[]{int.class, int.class}) {
            @Override
            public Long doOnRdd(JavaRDD rdd, Object... payloads) {
                return rdd.count() * (int) payloads[0] * (int) payloads[1];
            }
        };
        long pomLinesCount = template.requestBodyAndHeader("spark:analyze?rdd=#pomRdd&sparkContext=#sparkContext", asList("10", "10"), SPARK_RDD_CALLBACK_HEADER, rddCallback, Long.class);
        Truth.assertThat(pomLinesCount).isEqualTo(1700);
    }

    @Test
    public void shouldExecuteMap() {
        List<String> words = template.requestBodyAndHeader("spark:analyze?rdd=#pomRdd&sparkContext=#sparkContext", new DoubleWord(), SPARK_TRANSFORMATION_HEADER, MAP, List.class);
        Truth.assertThat(words).contains("foo barfoo bar");
    }

    @Test
    public void shouldExecuteFlatMap() {
        List<String> words = template.requestBody("spark:analyze?rdd=#pomRdd&sparkContext=#sparkContext", new LineToWord(), List.class);
        Truth.assertThat(words).contains("foo");
    }

    @Test
    public void shouldExecuteMapAndFlatMap() {
        List<String> doubledWords = template.requestBodyAndHeader("direct:mapAndFlatMap", null, SPARK_TRANSFORMATION_HEADER, MAP, List.class);
        Truth.assertThat(doubledWords).contains("foofoo");
    }

    @Test
    public void shouldUseTransformationFromRegistry() {
        long pomLinesCount = template.requestBody("spark:analyze?rdd=#pomRdd&sparkContext=#sparkContext&rddCallback=#countLinesTransformation", null, Long.class);
        Truth.assertThat(pomLinesCount).isGreaterThan(0L);
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        registry.bind("sparkContext", sparkContext);
        registry.bind("pomRdd", sparkContext.textFile("testrdd.txt"));
        registry.bind("countLinesTransformation", new RddCallback() {
            @Override
            public Object onRdd(JavaRDD rdd, Object... payloads) {
                return rdd.count();
            }
        });
        return registry;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:mapAndFlatMap").
                        setBody().constant(new LineToWord()).
                        to("spark:analyze?rdd=#pomRdd&sparkContext=#sparkContext&collect=false").
                        setBody().constant(new DoubleWord()).
                        to("spark:analyze?rdd=#pomRdd&sparkContext=#sparkContext");
            }
        };
    }

    static class DoubleWord implements Function<String, String> {

        @Override
        public String call(String v1) throws Exception {
            return v1 + v1;
        }

    }

    static class LineToWord implements FlatMapFunction<String, String> {

        @Override
        public Iterable<String> call(String s) throws Exception {
            return asList(s.split(" "));
        }

    }

}
