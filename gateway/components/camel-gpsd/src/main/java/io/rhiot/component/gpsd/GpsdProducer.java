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

import de.taimos.gpsd4java.backend.GPSdEndpoint;
import de.taimos.gpsd4java.types.PollObject;
import de.taimos.gpsd4java.types.TPVObject;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

import java.util.Date;

import static io.rhiot.cloudplatform.schema.gps.GpsCoordinates.gpsCoordinates;

public class GpsdProducer extends DefaultProducer {

    public GpsdProducer(GpsdEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        GPSdEndpoint gpsd4javaEndpoint = getEndpoint().getGpsd4javaEndpoint();
        
        PollObject pollObject = gpsd4javaEndpoint.poll();
        
        if (pollObject != null && pollObject.getFixes().size() > 0 && pollObject.getFixes().get(0) instanceof TPVObject) {
            TPVObject tpv = pollObject.getFixes().get(0);
            exchange.getIn().setBody(gpsCoordinates(new Date(new Double(tpv.getTimestamp()).longValue()), tpv.getLatitude(), tpv.getLongitude()));
            exchange.getIn().setHeader(GpsdConstants.TPV_HEADER, tpv);
        }
    }

    @Override
    public GpsdEndpoint getEndpoint() {
        return (GpsdEndpoint) super.getEndpoint();
    }

}
