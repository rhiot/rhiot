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
package io.rhiot.datastream.engine

import io.vertx.core.Vertx
import io.vertx.core.eventbus.Message
import org.junit.Test

import java.util.concurrent.Callable

import static com.google.common.truth.Truth.assertThat
import static com.jayway.awaitility.Awaitility.await

class StreamConsumerBootInitializerTest {

    @Test
    void shouldStartStreamConsumer() {
        new DataStream().start()
        assertThat(StubStreamConsumer.started).isTrue()
    }

    @Test
    void shouldStopStreamConsumer() {
        new DataStream().start().stop()
        assertThat(StubStreamConsumer.started).isFalse()
    }

    @Test
    void shouldAccessLoadedStreamCosnumers() {
        def dataStream = new DataStream().start()
        def loadedConsumers = dataStream.initializer(StreamConsumerBootInitializer.class).consumers()
        assertThat(loadedConsumers).hasSize(1)
        assertThat(loadedConsumers.first()).isInstanceOf(StubStreamConsumer.class)
    }

    @Test
    void shouldConsumeFromChannel() {
        // Given
        def message = 'message'
        def stream = new DataStream().start()

        // Then
        stream.beanRegistry().bean(Vertx.class).get().eventBus().publish('channel', message)

        // Then
        await().until((Callable<Boolean>){ StubStreamConsumer.lastMessage != null })
        assertThat(StubStreamConsumer.lastMessage).isEqualTo(message)
    }

    static class StubStreamConsumer implements StreamConsumer {

        static boolean started
        static String lastMessage

        @Override
        String fromChannel() {
            'channel'
        }

        @Override
        void consume(Message message) {
            lastMessage = message.body().toString()
        }

        @Override
        void start() {
            started = true
        }

        @Override
        void stop() {
            started = false
        }

    }

}