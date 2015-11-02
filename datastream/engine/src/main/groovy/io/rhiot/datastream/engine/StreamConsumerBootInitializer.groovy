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

import io.rhiot.steroids.bootstrap.AbstractBootInitializer
import io.rhiot.steroids.bootstrap.BootstrapAware
import io.vertx.core.Vertx

import static io.rhiot.steroids.Steroids.beans

class StreamConsumerBootInitializer extends AbstractBootInitializer {

    private List<StreamConsumer> consumers

    @Override
    void start() {
        consumers = beans(StreamConsumer.class)
        consumers.each {
            if(it instanceof BootstrapAware) {
                it.bootstrap(bootstrap)
            }
        }
        def eventBus = bootstrap.beanRegistry().bean(Vertx.class).get().eventBus()
        consumers.each { streamConsumer ->
            eventBus.consumer(streamConsumer.fromChannel()){
                streamConsumer.consume(it)
            }
            streamConsumer.start()
        }
    }

    @Override
    void stop() {
        consumers.each { it.stop() }
        consumers = null
    }

    @Override
    int order() {
        1500
    }

    List<StreamConsumer> consumers() {
        consumers
    }

}