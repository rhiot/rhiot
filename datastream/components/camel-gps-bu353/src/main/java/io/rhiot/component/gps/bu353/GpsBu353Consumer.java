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
package io.rhiot.component.gps.bu353;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.impl.DefaultScheduledPollConsumer;
import org.apache.camel.util.IOHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GpsBu353Consumer extends DefaultScheduledPollConsumer {

    public GpsBu353Consumer(GpsBu353Endpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }

    @Override
    protected int poll() throws Exception {
        log.debug("Started BU353 consumer poll.");
        BufferedReader source = null;
        try {
            log.debug("Opening connection with the GPS serial port.");
            source = new BufferedReader(new InputStreamReader(getEndpoint().getGpsCoordinatesSource().source()));
            while (true) {
                try {
                    String line = source.readLine();
                    log.debug("Consuming line: {}", line);
                    if (line.startsWith("$GPRMC")) {
                        ClientGpsCoordinates coordinates = ClientGpsCoordinates.parseNMEA(line);
                        Exchange exchange = ExchangeBuilder.anExchange(getEndpoint().getCamelContext()).withBody(coordinates).build();
                        getProcessor().process(exchange);
                        return 1;
                    } else {
                        log.debug("Not supported line read from the NMEA file. Ignoring: {}", line);
                    }
                } catch (SatelliteOutOfReachException e) {
                    log.info("GPS satellite out of reach.");
                } catch (Exception e) {
                    getExceptionHandler().handleException(e);
                }
            }
        } finally {
            log.debug("Closing connection with the GPS serial port.");
            if (source != null) {
                IOHelper.close(source);
            }
        }
    }

    @Override
    public GpsBu353Endpoint getEndpoint() {
        return (GpsBu353Endpoint) super.getEndpoint();
    }

}