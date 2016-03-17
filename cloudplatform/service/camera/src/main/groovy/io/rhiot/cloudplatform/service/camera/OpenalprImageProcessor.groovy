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
package io.rhiot.cloudplatform.service.camera

import groovy.transform.CompileStatic
import io.rhiot.cloudplatform.connector.IoTConnector
import io.rhiot.cloudplatform.service.camera.api.CameraImage
import io.rhiot.cloudplatform.service.camera.api.PlateMatch

import static io.rhiot.cloudplatform.connector.Header.arguments
import static io.rhiot.cloudplatform.service.camera.api.CameraImage.CAMERA_IMAGE_COLLECTION

@CompileStatic
class OpenalprImageProcessor implements ImageProcessor {

    private final IoTConnector connector

    private final PlateRecognition plateRecognition

    OpenalprImageProcessor(IoTConnector connector, PlateRecognition plateRecognition) {
        this.connector = connector
        this.plateRecognition = plateRecognition
    }

    @Override
    void process(String imageId, String country) {
        def imageData = (byte[]) connector.fromBus("binary.read", imageId, "".bytes.class);
        List<PlateMatch> matches = plateRecognition.recognizePlate(country, imageData);

        CameraImage imageMetadata = connector.fromBus("document.findOne", CameraImage.class, arguments(CAMERA_IMAGE_COLLECTION, imageId));
        imageMetadata.setPlateMatches(matches);
        connector.toBus("document.save", imageMetadata, arguments("CameraImage"));
    }

}
