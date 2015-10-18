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

import com.github.sarxos.webcam.WebcamMotionEvent;
import org.apache.camel.*;
import org.apache.camel.spi.ExceptionHandler;
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
    static Exchange createOutOnlyExchangeWithBodyAndHeaders(WebcamEndpoint endpoint, BufferedImage image) throws IOException {
        Exchange exchange = endpoint.createExchange(ExchangePattern.OutOnly);
        Message message = exchange.getIn();
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ImageIO.write(image, endpoint.getFormat(), output);
            message.setBody(output.toByteArray());
            message.setHeader(Exchange.FILE_NAME, message.getMessageId() + "." + endpoint.getFormat());
        } 

        return exchange;
    }

    /**
     * Consume the java.awt.BufferedImage from the webcam, all params required.
     * 
     * @param image The image to process.
     * @param processor Processor that handles the exchange.
     * @param endpoint WebcamEndpoint receiving the exchange.
     */
    public static void consumeBufferedImage(BufferedImage image, Processor processor, WebcamEndpoint endpoint, ExceptionHandler exceptionHandler) {
        Validate.notNull(image);
        Validate.notNull(processor);
        Validate.notNull(endpoint);
        
        try {
            Exchange exchange = createOutOnlyExchangeWithBodyAndHeaders(endpoint, image);
            processor.process(exchange);
        } catch (Exception e) {
            exceptionHandler.handleException(e);
        }
    }

    /**
     * Consume the motion event from the webcam, all params required.
     * The event is stored in the header while the latest image is available as the body.
     *
     * @param motionEvent The motion event that triggered.
     * @param processor Processor that handles the exchange.
     * @param endpoint WebcamEndpoint receiving the exchange.
     */
    public static void consumeWebcamMotionEvent(WebcamMotionEvent motionEvent, Processor processor, WebcamEndpoint endpoint, ExceptionHandler exceptionHandler){
        Validate.notNull(motionEvent);
        Validate.notNull(processor);
        Validate.notNull(endpoint);

        try {
            Exchange exchange = createOutOnlyExchangeWithBodyAndHeaders(endpoint, motionEvent.getCurrentImage());
            exchange.getIn().setHeader(WebcamConstants.MOTION_EVENT_HEADER, motionEvent);
            processor.process(exchange);
        } catch (Exception e) {
            exceptionHandler.handleException(e);
        }
    }
}
