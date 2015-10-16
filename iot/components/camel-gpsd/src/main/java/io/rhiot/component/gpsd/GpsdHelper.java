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

package io.rhiot.component.gpsd;

import de.taimos.gpsd4java.types.TPVObject;
import org.apache.camel.*;
import org.apache.camel.spi.ExceptionHandler;
import org.apache.commons.lang3.Validate;

import java.util.Date;

/**
 * Commonly used Gpsd utilities.
 */
public class GpsdHelper {

    /**
     * Creates an OutOnly exchange with the ClientGpsCoordinates as message body and TPVObject as header.
     */
    static Exchange createOutOnlyExchangeWithBodyAndHeaders(org.apache.camel.Endpoint endpoint, ClientGpsCoordinates messageBody, TPVObject tpvObject) {
        Exchange exchange = endpoint.createExchange(ExchangePattern.OutOnly);
        Message message = exchange.getIn();
        message.setHeader(GpsdConstants.TPV_HEADER, tpvObject);
        message.setBody(messageBody);
        return exchange;
    }

    /**
     * Process the TPVObject, all params required.
     * 
     * @param tpv The time-position-velocity object to process
     * @param processor Processor that handles the exchange.
     * @param endpoint GpsdEndpoint receiving the exchange.
     */
    public static void consumeTPVObject(TPVObject tpv, Processor processor, GpsdEndpoint endpoint, ExceptionHandler exceptionHandler) {
        Validate.notNull(tpv);
        Validate.notNull(processor);
        Validate.notNull(endpoint);
        
        Exchange exchange = createOutOnlyExchangeWithBodyAndHeaders(endpoint,
                new ClientGpsCoordinates(new Date(new Double(tpv.getTimestamp()).longValue()), tpv.getLatitude(), tpv.getLongitude()), tpv);
        try {
            processor.process(exchange);
        } catch (Exception e) {
            exceptionHandler.handleException(e);
        }
    }
}
