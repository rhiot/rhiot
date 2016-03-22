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
package io.rhiot.cloudplatform.service.device.spring;

import com.mongodb.Mongo;
import io.rhiot.cloudplatform.encoding.spi.PayloadEncoding;
import io.rhiot.cloudplatform.service.binding.ServiceBinding;
import io.rhiot.cloudplatform.service.device.MongoDbDeviceRegistry;
import io.rhiot.cloudplatform.service.device.api.DeviceRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeviceServiceConfiguration {

    @ConditionalOnMissingBean(name = "deviceServiceBinding")
    @Bean
    ServiceBinding deviceServiceBinding(PayloadEncoding payloadEncoding) {
        return new ServiceBinding(payloadEncoding, "device");
    }

    @ConditionalOnMissingBean
    @Bean(name = "device")
    DeviceRegistry deviceRegistry(Mongo mongo,
                                  @Value("${device.metrics.mongodb.db:rhiot}") String db,
                                  @Value("${device.metrics.mongodb.db:device}") String collection,
                                  @Value("${disconnectionPeriod:60000}") long disconnectionPeriod) {
        return new MongoDbDeviceRegistry(mongo, db, collection, disconnectionPeriod);
    }

}