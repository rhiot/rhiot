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
import io.rhiot.datastream.engine.DataStream
import io.rhiot.datastream.engine.JsonWithHeaders
import io.rhiot.datastream.engine.TypeConverter
import io.rhiot.mongodb.EmbeddedMongo
import io.rhiot.steroids.Bean
import io.rhiot.steroids.camel.CamelBootInitializer
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.eventbus.Message
import io.vertx.core.json.Json
import org.apache.camel.component.spark.RddCallback
import org.apache.spark.api.java.AbstractJavaRDDLike
import org.apache.spark.api.java.JavaSparkContext
import org.junit.AfterClass
import org.junit.Test

import java.util.concurrent.Callable

import static com.google.common.truth.Truth.assertThat
import static com.jayway.awaitility.Awaitility.await
import static io.rhiot.steroids.activemq.EmbeddedActiveMqBrokerBootInitializer.amqp
import static org.apache.camel.component.spark.SparkMongos.mongoRdd

class DataStreamNodeTest {

    static def mongo = new EmbeddedMongo().start()

    static def dataStream = new DataStream().start()

    @AfterClass
    static void afterClass() {
        mongo.stop()
    }

    @Test
    void smokeTestMongoDocumentStreamConsumer() {
        // Given
        def vertx = dataStream.beanRegistry().bean(Vertx.class).get()
        def message = new JsonWithHeaders(null, ['operation': 'count', 'arg0': 'foo'])
        def count = -1

        // When
        vertx.eventBus().send('document', message.json, message.deliveryOptions(), new Handler<AsyncResult<Message>>() {
            @Override
            void handle(AsyncResult<Message> event) {
                count = Json.decodeValue((String) event.result().body(), Map.class).result
            }
        })

        // Then
        await().until( (Callable<Boolean>) { count > -1 } )
        assertThat(count).isEqualTo(0)
    }

    @Test
    void smokeTestMongoSparkTask() {
        dataStream.beanRegistry().register('callback', new RddCallback<Long>() {
            @Override
            Long onRdd(AbstractJavaRDDLike rdd, Object... payloads) {
                rdd.count() * (int) payloads[0]
            }
        })

        def mongoClient = new Mongo('localhost', mongo.port())
        mongoClient.getDB('db').getCollection('collection').save(new BasicDBObject([foo: 'bar']))

        def sparkContext = dataStream.beanRegistry().bean(JavaSparkContext.class).get()
        dataStream.beanRegistry().register('rdd', mongoRdd(sparkContext, 'localhost', mongo.port(), 'db', 'collection'))

        def encodedResult = CamelBootInitializer.camelContext().createProducerTemplate().requestBody(amqp('spark.execute.rdd.callback'), Json.encode([payload: 10]), String.class)
        def result = Json.decodeValue(encodedResult, Map.class).payload
        assertThat(result).isEqualTo(10)
    }

    @Bean
    static class MockTypeConverter implements TypeConverter {

        @Override
        def <T> T convert(Object object, Class<T> targetType) {
            object
        }
    }

}
