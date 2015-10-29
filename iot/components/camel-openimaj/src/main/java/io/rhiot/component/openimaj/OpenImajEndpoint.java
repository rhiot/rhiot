/*
 * 
 *  Licensed to the Rhiot under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 */
package io.rhiot.component.openimaj;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.openimaj.image.processing.face.detection.FaceDetector;

/**
 * Represents an OpenIMAJ endpoint.
 */
@UriEndpoint(scheme = "openimaj", title = "openimaj", syntax="openimaj:name", producerOnly = true, label = "OpenIMAJ")
public class OpenImajEndpoint extends DefaultEndpoint {
     
    @UriParam(defaultValue = "new HaarCascadeDetector()", description = "An implementation capable of detecting faces")
    private FaceDetector faceDetector;
    
    @UriParam(defaultValue = "1", description = "The minimum confidence of the detection; higher numbers mean higher confidence.")
    private float confidence = OpenImajConstants.DEFAULT_CONFIDENCE;    
    
    public OpenImajEndpoint() {
    }

    public OpenImajEndpoint(String uri, OpenImajComponent component) {
        super(uri, component);
    }

    public OpenImajEndpoint(String endpointUri) {
        super(endpointUri);
        createEndpointConfiguration(endpointUri);
    }

    // Producer/consumer factories

    @Override
    public Producer createProducer() throws Exception {
        return new OpenImajProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("OpenIMAJ component supports only producer endpoints.");
    }

    // Configuration

    @Override
    public boolean isSingleton() {
        return false;
    }

    // Configuration getters and setters


    public FaceDetector getFaceDetector() {
        return faceDetector;
    }

    public void setFaceDetector(FaceDetector faceDetector) {
        this.faceDetector = faceDetector;
    }

    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }
}
