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
package io.rhiot.datastream.node

import com.mongodb.BasicDBObject
import com.mongodb.Mongo
import io.rhiot.datastream.engine.test.DataStreamTest
import io.rhiot.mongodb.EmbeddedMongo
import io.rhiot.steroids.camel.CamelBootInitializer
import io.vertx.core.json.Json
import org.apache.camel.component.spark.RddCallback
import org.apache.spark.api.java.AbstractJavaRDDLike
import org.apache.spark.api.java.JavaSparkContext
import org.junit.AfterClass
import org.junit.Test
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import static com.google.common.truth.Truth.assertThat
import static io.rhiot.steroids.activemq.EmbeddedActiveMqBrokerBootInitializer.amqp
import static org.apache.camel.component.spark.SparkMongos.mongoRdd

@Configuration
class DataStreamNodeTest extends DataStreamTest {

    static def mongo = new EmbeddedMongo().start()

    @AfterClass
    static void afterClass() {
        mongo.stop()
    }

    @Test
    void smokeTestMongoDocumentStreamConsumer() {
        fromBus('document.save.doc', [foo: 'bar'], String.class)
        def count = fromBus('document.count.doc', int.class)
        assertThat(count).isEqualTo(1)
    }

    @Test
    void smokeTestMongoSparkTask() {
        def mongoClient = new Mongo('localhost', mongo.port())
        mongoClient.getDB('db').getCollection('collection').save(new BasicDBObject([foo: 'bar']))

        def sparkContext = cloudPlatform.applicationContext.getBean(JavaSparkContext.class)
        cloudPlatform.beanRegistry().register('rdd', mongoRdd(sparkContext, 'localhost', mongo.port(), 'db', 'collection'))

        def encodedResult = CamelBootInitializer.camelContext().createProducerTemplate().requestBody(amqp('spark.execute.rdd.callback'), Json.encode([payload: 10]), String.class)
        def result = Json.decodeValue(encodedResult, Map.class).payload
        assertThat(result).isEqualTo(10)
    }

    @Bean
    RddCallback callback() {
        new RddCallback<Long>() {
            @Override
            Long onRdd(AbstractJavaRDDLike rdd, Object... payloads) {
                rdd.count() * (int) payloads[0]
            }
        }
    }

}
