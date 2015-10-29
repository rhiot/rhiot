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
package io.rhiot.datastream.document

import io.rhiot.datastream.engine.StreamConsumer
import io.vertx.core.eventbus.Message
import io.vertx.core.json.Json

import static io.rhiot.steroids.Steroids.bean

class DocumentStreamConsumer implements StreamConsumer {

    def documentStore = bean(DocumentStore.class).get()

    @Override
    String fromChannel() {
        'document'
    }

    @Override
    void consume(Message message) {
        switch (message.headers().get('operation')) {
            case{ it.startsWith('save') } :
                def collection = message.headers().get('collection')
                def document = Json.decodeValue((String) message.body(), Map.class)
                documentStore.save(new SaveOperation(collection, document))
                break
        }
    }

    @Override
    void start() {

    }

    @Override
    void stop() {

    }

}