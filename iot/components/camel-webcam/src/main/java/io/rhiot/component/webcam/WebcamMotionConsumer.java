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

import com.github.sarxos.webcam.WebcamMotionDetector;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;

/**
 * Detects motion.
 */
public class WebcamMotionConsumer extends DefaultConsumer {
    
    public WebcamMotionConsumer(WebcamEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        endpoint.setScheduled(false);
    }
    
    @Override
    protected void doStart() throws Exception {
        log.debug("Starting motion detection consumer.");
        
        WebcamMotionDetector detector = new WebcamMotionDetector(getEndpoint().getWebcam(), getEndpoint().getPixelThreshold(),
                getEndpoint().getAreaThreshold(), getEndpoint().getMotionInterval());
        if (getEndpoint().getMotionInertia() > 0) {
            detector.setInertia(getEndpoint().getMotionInertia());
        }

        detector.addMotionListener(wme -> WebcamHelper.consumeWebcamMotionEvent(wme, getProcessor(), getEndpoint(), getExceptionHandler()));
        detector.start();
        log.debug("Started motion detection.");            
    }

    

    @Override
    public WebcamEndpoint getEndpoint() {
        return (WebcamEndpoint) super.getEndpoint();
    }

}