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
package io.rhiot.datastream.engine.camel

import io.rhiot.datastream.engine.TypeConverter
import io.rhiot.steroids.Bean
import io.rhiot.bootstrap.Bootstrap
import io.rhiot.steroids.bootstrap.BootstrapAware
import org.apache.camel.CamelContext

@Bean
class CamelTypeConverter implements TypeConverter, BootstrapAware {

    private CamelContext camelContext

    @Override
    def <T> T convert(Object object, Class<T> targetType) {
        camelContext.typeConverter.convertTo(targetType, object)
    }

    @Override
    void bootstrap(Bootstrap bootstrap) {
        this.camelContext = bootstrap.beanRegistry().bean(CamelContext.class).get()
    }

}
