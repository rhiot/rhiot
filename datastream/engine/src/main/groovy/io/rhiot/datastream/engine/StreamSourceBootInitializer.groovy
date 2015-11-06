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

class StreamSourceBootInitializer extends AbstractBootInitializer {

    private List<StreamSource> sources

    @Override
    void start() {
        sources = beans(StreamSource.class)
        sources.each {
            if(it instanceof BootstrapAware) {
                it.bootstrap(bootstrap)
            }
            it.start()
        }
    }

    @Override
    void stop() {
        sources.each { it.stop() }
        sources = null
    }

    @Override
    int order() {
        1600
    }

}