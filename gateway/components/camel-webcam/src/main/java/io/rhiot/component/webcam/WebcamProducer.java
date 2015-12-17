/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rhiot.component.webcam;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.util.ImageUtils;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

import java.awt.image.BufferedImage;

public class WebcamProducer extends DefaultProducer {
    
    public WebcamProducer(WebcamEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        Webcam webcam = getEndpoint().getWebcam();
        if (webcam == null) {
            throw new WebcamNotFoundException("No webcams found");
        } else if (!webcam.isOpen() && !webcam.open()) {
            throw new IllegalStateException("Unable to open webcam");
        } 
        
        BufferedImage image = webcam.getImage();
        if (image != null) {
            exchange.getIn().setBody(ImageUtils.toByteArray(image, getEndpoint().getFormat()));
        }
    }

    @Override
    public WebcamEndpoint getEndpoint() {
        return (WebcamEndpoint) super.getEndpoint();
    }

}
