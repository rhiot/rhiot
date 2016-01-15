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
import io.rhiot.cloudplatform.schema.gps.GpsCoordinates;
import org.apache.camel.*;
import org.apache.camel.spi.ExceptionHandler;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static io.rhiot.component.gpsd.GpsdConstants.TPV_HEADER;
import static io.rhiot.cloudplatform.schema.gps.GpsCoordinates.gpsCoordinates;
import static org.apache.camel.ExchangePattern.OutOnly;
import static org.apache.camel.builder.ExchangeBuilder.anExchange;

/**
 * Commonly used Gpsd utilities.
 */
public class GpsdHelper {

    private static final Logger LOG = LoggerFactory.getLogger(GpsdHelper.class);

    /**
     * Process the TPVObject, all params required.
     * 
     * @param tpv The time-position-velocity object to process
     * @param processor Processor that handles the exchange.
     * @param endpoint GpsdEndpoint receiving the exchange.
     */
    public static void consumeTPVObject(TPVObject tpv, Processor processor, GpsdEndpoint endpoint, ExceptionHandler exceptionHandler) {
        // Simplify logging when https://github.com/taimos/GPSd4Java/pull/19 is merged into upstream.
        LOG.debug("About to consume TPV object {},{} from {}", tpv.getLatitude(), tpv.getLongitude(), endpoint);

        Validate.notNull(tpv);
        Validate.notNull(processor);
        Validate.notNull(endpoint);

        GpsCoordinates coordinates = gpsCoordinates(new Date(new Double(tpv.getTimestamp()).longValue()), tpv.getLatitude(), tpv.getLongitude());
        Exchange exchange = anExchange(endpoint.getCamelContext()).withPattern(OutOnly).
                withHeader(TPV_HEADER, tpv).withBody(coordinates).build();

        try {
            processor.process(exchange);
        } catch (Exception e) {
            exceptionHandler.handleException(e);
        }
    }
}
