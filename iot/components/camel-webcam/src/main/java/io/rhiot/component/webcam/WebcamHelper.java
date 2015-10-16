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

import org.apache.camel.*;
import org.apache.commons.lang3.Validate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Commonly used Webcam utilities.
 */
public class WebcamHelper {

    /**
     * Creates an OutOnly exchange with the BufferedImage.
     */
    static Exchange createOutOnlyExchangeWithBodyAndHeaders(org.apache.camel.Endpoint endpoint, BufferedImage image) {
        Exchange exchange = endpoint.createExchange(ExchangePattern.OutOnly);
        Message message = exchange.getIn();
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", output); //todo format should be set on endpoint
            message.setBody(output.toByteArray());
        } catch (IOException e) {
            exchange.setException(e);
        }

        return exchange;
    }

    /**
     * Process the TPVObject, all params required.
     * 
     * @param image The image to process.
     * @param processor Processor that handles the exchange.
     * @param endpoint WebcamEndpoint receiving the exchange.
     */
    public static void consumeBufferedImage(BufferedImage image, Processor processor, WebcamEndpoint endpoint) {
        Validate.notNull(image);
        Validate.notNull(processor);
        Validate.notNull(endpoint);
        
        Exchange exchange = createOutOnlyExchangeWithBodyAndHeaders(endpoint,
                image);
        try {

            processor.process(exchange);
        } catch (Exception e) {
            exchange.setException(e);
        }
    }
}
