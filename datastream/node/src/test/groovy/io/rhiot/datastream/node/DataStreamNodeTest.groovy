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

import io.rhiot.datastream.document.CountOperation
import io.rhiot.datastream.document.DocumentStore
import io.rhiot.datastream.engine.DataStream
import io.rhiot.mongodb.EmbeddedMongo
import org.junit.BeforeClass
import org.junit.Test

import static com.google.common.truth.Truth.assertThat
import static io.rhiot.utils.Properties.setIntProperty

class DataStreamNodeTest {

    static def dataStream = new DataStream()

    static def mongo = new EmbeddedMongo().start()

    @BeforeClass
    static void beforeClass() {
        setIntProperty('MONGODB_SERVICE_PORT', mongo.port)
        dataStream = new DataStream().start()
    }

    @Test
    void shouldInitializeMongoDBStore() {
        // Given

        def store = dataStream.beanRegistry().bean(DocumentStore.class).get()

        // When
        def count = store.count(new CountOperation('foo'))

        // Then
        assertThat(count).isEqualTo(0)
    }

}
