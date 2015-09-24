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
package io.rhiot.steroids.bootstrap

import static io.rhiot.steroids.Steroids.beans;

class Bootstrap {

    private final def initializers = beans(BootInitializer.class).sort{ first, second -> first.order() - second.order() }.asImmutable()

    Bootstrap start() {
        initializers.each { it.start() }
        this
    }

    Bootstrap stop() {
        initializers.reverse().each { it.stop() }
        this
    }

    private static final def BEANS_ORDER = new Comparator<BootInitializer>(){
        @Override
        int compare(BootInitializer first, BootInitializer second) {
            first.order() - second.order()
        }
    }

}