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
import io.rhiot.cloudplatform.service.camera.api.CameraService;
import io.rhiot.cloudplatform.service.camera.api.PlateMatch;
import org.apache.camel.ProducerTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.rhiot.cloudplatform.connector.Header.arguments;
import static org.apache.commons.io.IOUtils.write;

public class DefaultCameraService implements CameraService {

    private final IoTConnector connector;

    private final ProducerTemplate producerTemplate;

    private final File imagesDirectory;

    public DefaultCameraService(IoTConnector connector, ProducerTemplate producerTemplate, File imagesDirectory) {
        this.connector = connector;
        this.producerTemplate = producerTemplate;
        this.imagesDirectory = imagesDirectory;

        imagesDirectory.mkdirs();
    }

    @Override
    public List<PlateMatch> recognizePlate(String country, byte[] imageData) {
        return ((List<io.rhiot.cloudplatform.camel.openalpr.PlateMatch>) producerTemplate.requestBody("openalpr:recognize?country=" + country, imageData, List.class)).stream().
                map(match -> new PlateMatch(match.getPlateNumber(), match.getConfidence())).collect(Collectors.toList());
    }

    @Override
    public void process(String deviceId, String country, byte[] imageData) {
        List<PlateMatch> matches = recognizePlate(country, imageData);
        Map<String, Object> imageMetadata = ImmutableMap.of("deviceId", deviceId, "plateMatches", matches);
        String imageId = connector.fromBus("document.save", imageMetadata, String.class, arguments("WebcamImage"));

        try {
            write(imageData, new FileOutputStream(new File(imagesDirectory, imageId + ".jpg")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}