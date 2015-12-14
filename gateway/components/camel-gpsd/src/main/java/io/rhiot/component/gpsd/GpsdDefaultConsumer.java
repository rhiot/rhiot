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

import de.taimos.gpsd4java.api.DistanceListener;
import de.taimos.gpsd4java.api.ObjectListener;
import de.taimos.gpsd4java.backend.GPSdEndpoint;
import de.taimos.gpsd4java.types.TPVObject;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;

/**
 * The default Gpsd consumer listens for events from the device and consumes it immediately.
 */
public class GpsdDefaultConsumer extends DefaultConsumer {
    
    public GpsdDefaultConsumer(GpsdEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }
    
    
    @Override
    protected void doStart() throws Exception {
        log.debug("Starting GPSD consumer.");
            
        GPSdEndpoint gpsd4javaEndpoint = getEndpoint().getGpsd4javaEndpoint();
        if (getEndpoint().getDistance() > 0) {
            gpsd4javaEndpoint.addListener(new DistanceListener(getEndpoint().getDistance()) {
                @Override
                protected void handleLocation(TPVObject tpv) {
                    GpsdHelper.consumeTPVObject(tpv, getProcessor(), getEndpoint(), getExceptionHandler());
                }
            });
        } else {
            gpsd4javaEndpoint.addListener(new ObjectListener() {
                @Override
                public void handleTPV(final TPVObject tpv) {
                    GpsdHelper.consumeTPVObject(tpv, getProcessor(), getEndpoint(), getExceptionHandler());
                }
            });
        }
    }
    

    @Override
    public GpsdEndpoint getEndpoint() {
        return (GpsdEndpoint) super.getEndpoint();
    }

}