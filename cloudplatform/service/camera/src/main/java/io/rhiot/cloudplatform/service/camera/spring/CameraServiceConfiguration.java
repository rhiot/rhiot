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
package io.rhiot.cloudplatform.service.camera.spring;

import io.rhiot.cloudplatform.connector.IoTConnector;
import io.rhiot.cloudplatform.encoding.spi.PayloadEncoding;
import io.rhiot.cloudplatform.service.binding.ServiceBinding;
import io.rhiot.cloudplatform.service.camera.*;
import io.rhiot.cloudplatform.service.camera.api.CameraService;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.MoreObjects.firstNonNull;

@Configuration
public class CameraServiceConfiguration {

    @Autowired(required = false)
    List<ImageProcessor> imageProcessors;

    @Bean(name = "camera")
    CameraService cameraService(IoTConnector connector, PlateRecognition plateRecognition) {
        List<ImageProcessor> processors = firstNonNull(imageProcessors, Collections.<ImageProcessor>emptyList());
        return new DefaultCameraService(connector, plateRecognition, processors);
    }

    @Bean
    @ConditionalOnProperty(name = "camera.processor.openalpr.enabled", havingValue = "true", matchIfMissing = true)
    ImageProcessor openalprImageProcessor(IoTConnector connector, PlateRecognition plateRecognition) {
        return new OpenalprImageProcessor(connector, plateRecognition);
    }

    @Bean
    PlateRecognition plateRecognition(ProducerTemplate producerTemplate) {
        return new PlateRecognition(producerTemplate);
    }

    @Bean
    ServiceBinding cameraServiceBinding(PayloadEncoding payloadEncoding) {
        return new ServiceBinding(payloadEncoding, "camera");
    }

    @Bean
    CameraImageRotation cameraImageRotation(IoTConnector connector,
                                            @Value("${camera.rotation.storageQuota:5120}") long storageQuota,
                                            @Value("${camera.rotation.initialDelay:15000}") int initialDelay) {
        return new CameraImageRotation(connector, storageQuota, initialDelay);
    }

}
