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

package io.rhiot.component.openimaj;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.FaceDetector;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public class OpenImajProducer extends DefaultProducer {
    
    private FaceDetector<DetectedFace, FImage> faceDetector;

    public OpenImajProducer(OpenImajEndpoint endpoint) {
        super(endpoint);
        this.faceDetector = endpoint.getFaceDetector();
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        InputStream inputStream = exchange.getIn().getBody(InputStream.class);
        if (inputStream != null) {
            FImage fImage = ImageUtilities.readF(inputStream);
            if (fImage != null) {
                List<DetectedFace> detectedFaces = faceDetector.detectFaces(fImage);

                if (detectedFaces.size() > 0) {
                    List<DetectedFace> filtered = detectedFaces.stream().filter(
                            face -> face.getConfidence() >= getEndpoint().getConfidence()).collect(Collectors.toList());

                    exchange.getIn().setBody(filtered.size() == 1 ? filtered.get(0) : filtered);
                }
            }
        }
    }

    @Override
    public OpenImajEndpoint getEndpoint() {
        return (OpenImajEndpoint) super.getEndpoint();
    }

    @Override
    protected void doStart() throws Exception {
        if (faceDetector == null) {
            faceDetector = new HaarCascadeDetector();
        }
        super.doStart();
    }
    
}
