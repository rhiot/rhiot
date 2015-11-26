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
package io.rhiot.steroids.spark

import io.rhiot.bootstrap.classpath.Bean
import io.rhiot.bootstrap.BootInitializer
import io.rhiot.bootstrap.Bootstrap
import io.rhiot.bootstrap.BootstrapAware
import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext

@Bean
class SparkBootInitializer implements BootInitializer, BootstrapAware {

    private Bootstrap bootstrap

    private JavaSparkContext sparkContext

    @Override
    void start() {
        def conf = new SparkConf().setAppName("Rhiot application").setMaster('local[*]')
        sparkContext = new JavaSparkContext(conf);
        bootstrap.beanRegistry().register(sparkContext)
    }

    @Override
    void stop() {
        sparkContext.stop()
    }

    @Override
    int order() {
        2000
    }

    JavaSparkContext sparkContext() {
        sparkContext
    }

    @Override
    void bootstrap(Bootstrap bootstrap) {
        this.bootstrap = bootstrap
    }

}
