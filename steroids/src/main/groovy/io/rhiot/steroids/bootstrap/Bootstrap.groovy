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

import io.rhiot.utils.WithLogger

import static io.rhiot.steroids.Steroids.beans
import static java.lang.Runtime.runtime;

/**
 * Starts up Steroids framework, scans the classpath for the initializers and runs the latter.
 */
class Bootstrap implements WithLogger {

    // Members

    private final BeanRegistry beanRegistry

    private final def initializers = beans(BootInitializer.class).
            sort{ first, second -> first.order() - second.order()}.asImmutable()

    // Constructors

    Bootstrap(BeanRegistry beanRegistry) {
        this.beanRegistry = beanRegistry
    }

    Bootstrap() {
        this(new ScanningMapBeanRegistry())
    }

    // Lifecycle

    Bootstrap start() {
        log().debug('Starting Steroids Bootstrap: {}', getClass().name)
        initializers.each {
            if(it instanceof BootstrapAware) {
                it.bootstrap(this)
            }
        }
        initializers.each { it.start() }
        this
    }

    Bootstrap stop() {
        log().debug('Stopping Steroids Bootstrap: {}', getClass().name)
        initializers.reverse().each { it.stop() }
        this
    }

    BeanRegistry beanRegistry() {
        beanRegistry
    }

    def <T extends BootInitializer> T initializer(Class<T> type) {
        initializers.find { type.isAssignableFrom(it.class) }
    }

    // Main entry point

    public static void main(String[] args) {
        def bootstrap = new Bootstrap().start()
        runtime.addShutdownHook(new Thread(){
            @Override
            void run() {
                bootstrap.stop()
            }
        })
    }

}