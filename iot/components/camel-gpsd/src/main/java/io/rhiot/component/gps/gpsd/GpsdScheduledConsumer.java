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

package io.rhiot.component.gps.gpsd;

import de.taimos.gpsd4java.backend.GPSdEndpoint;
import de.taimos.gpsd4java.types.PollObject;
import de.taimos.gpsd4java.types.TPVObject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultScheduledPollConsumer;

import java.util.Date;

/**
 * The scheduled Gpsd consumer.
 */
public class GpsdScheduledConsumer extends DefaultScheduledPollConsumer {

    
    public GpsdScheduledConsumer(GpsdEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }
    
    
    @Override
    protected int poll() throws Exception {
        log.debug("Starting scheduled GPSD consumer poll.");

        GPSdEndpoint gpsd4javaEndpoint = getEndpoint().getGpsd4javaEndpoint();
        while (true && gpsd4javaEndpoint != null) {
            try {
                PollObject pollObject = gpsd4javaEndpoint.poll();
                if (pollObject != null && pollObject.getFixes().size() > 0 && pollObject.getFixes().get(0) instanceof TPVObject) {
                    consumeTPVObject(pollObject.getFixes().get(0));
                    return 1;
                }

            } catch (Exception e) {
                getExceptionHandler().handleException(e);
            }
        }
        return 0;
    }

    private void consumeTPVObject(TPVObject tpv) {
        Exchange exchange = GpsdHelper.createOutOnlyExchangeWithBodyAndHeaders(getEndpoint(),
                new ClientGpsCoordinates(new Date(new Double(tpv.getTimestamp()).longValue()), tpv.getLatitude(), tpv.getLongitude()), tpv);
        try {

            getProcessor().process(exchange);
        } catch (Exception e) {
            exchange.setException(e);
        }
    }

    @Override
    public GpsdEndpoint getEndpoint() {
        return (GpsdEndpoint) super.getEndpoint();
    }


    @Override
    protected void doStop() throws Exception {
        getEndpoint().getGpsd4javaEndpoint().stop();

        super.doStop();
    }
}