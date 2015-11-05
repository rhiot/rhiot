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
package io.rhiot.datastream.document.mongodb

import com.google.common.truth.Truth
import io.rhiot.datastream.document.DocumentStreamConsumer
import io.rhiot.datastream.engine.DataStream
import io.rhiot.datastream.engine.JsonWithHeaders
import io.rhiot.mongodb.EmbeddedMongo
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.Message
import io.vertx.core.json.Json
import org.junit.BeforeClass
import org.junit.Test

import java.util.concurrent.Callable

import static com.jayway.awaitility.Awaitility.await

class MongodbDocumentStoreTest {

    static mongo = new EmbeddedMongo().start()

    static DataStream dataStream

    static EventBus bus

    @BeforeClass
    static void beforeClass() {
        dataStream = new DataStream().start()
        bus = dataStream.beanRegistry().bean(Vertx.class).get().eventBus()
    }

    @Test
    void shouldCountSavedDocument() {
        def document = [foo: 'bar']
        def saveCommand = JsonWithHeaders.jsonWithHeaders(document, [operation: 'save', arg0: 'doc'])
        bus.send(DocumentStreamConsumer.CHANNEL, saveCommand.json, saveCommand.deliveryOptions())
        def countCommand = JsonWithHeaders.jsonWithHeaders(null, [operation: 'count', arg0: 'doc'])

        // When
        def count = -1
        bus.send(DocumentStreamConsumer.CHANNEL, countCommand.json, countCommand.deliveryOptions(), new Handler<AsyncResult<Message>>() {
            @Override
            void handle(AsyncResult<Message> event) {
                count = Json.decodeValue((String) event.result().body(), Map.class).result
            }
        })

        // Then
        await().until((Callable<Boolean>) { count > -1 })
        Truth.assertThat(count).isEqualTo(1)
    }

}
