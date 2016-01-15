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
package io.rhiot.cloduplatform.service.spark;

import com.google.common.truth.Truth;
import io.rhiot.cloudplatform.runtime.spring.test.CloudPlatformTest;
import org.apache.camel.component.spark.RddCallback;
import org.apache.spark.api.java.AbstractJavaRDDLike;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class CamelSparkStreamConsumerTest extends CloudPlatformTest {

    @Test
    public void shouldExecuteTaskViaAmqpApi() {
        int result = connector.fromBus("spark.execute.rdd.callback", 10, int.class);
        Truth.assertThat(result).isEqualTo(170);
    }

    @Component("callback")
    static class LinesXPayload implements RddCallback<Integer> {
        @Override
        public Integer onRdd(AbstractJavaRDDLike rdd, Object... payloads) {
            return (int)((int) payloads[0] * rdd.count());
        };
    }

    @Bean
    JavaRDD rdd(JavaSparkContext sparkContext) {
        return sparkContext.textFile("src/test/resources/testrdd.txt");
    }

}
