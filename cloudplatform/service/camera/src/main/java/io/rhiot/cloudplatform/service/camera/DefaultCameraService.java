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
package io.rhiot.cloudplatform.service.camera;

import com.google.common.collect.ImmutableMap;
import io.rhiot.cloudplatform.connector.IoTConnector;
import io.rhiot.cloudplatform.service.camera.api.CameraImage;
import io.rhiot.cloudplatform.service.camera.api.CameraService;
import io.rhiot.cloudplatform.service.camera.api.PlateMatch;

import java.util.List;
import java.util.Map;

import static io.rhiot.cloudplatform.connector.Header.arguments;
import static io.rhiot.cloudplatform.service.camera.api.CameraImage.CAMERA_IMAGE_COLLECTION;

public class DefaultCameraService implements CameraService {

    private final IoTConnector connector;

    private final PlateRecognition plateRecognition;

    private final List<ImageProcessor> imageProcessors;

    public DefaultCameraService(IoTConnector connector, PlateRecognition plateRecognition, List<ImageProcessor> imageProcessors) {
        this.connector = connector;
        this.plateRecognition = plateRecognition;
        this.imageProcessors = imageProcessors;
    }

    @Override
    public List<PlateMatch> recognizePlate(String country, byte[] imageData) {
        return plateRecognition.recognizePlate(country, imageData);
    }

    @Override
    public void process(String deviceId, String country, byte[] imageData) {
        Map<String, Object> imageMetadata = ImmutableMap.of("deviceId", deviceId);
        String imageId = connector.fromBus("document.save", imageMetadata, String.class, arguments(CAMERA_IMAGE_COLLECTION));
        connector.toBusAndWait("binary.store", imageData, arguments(imageId));

        imageProcessors.stream().forEach(processor -> processor.process(imageId, country));
    }

}