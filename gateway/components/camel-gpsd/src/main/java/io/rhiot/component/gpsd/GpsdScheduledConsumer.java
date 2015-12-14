/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
* limitations under the License.
 */

package io.rhiot.component.gpsd;

import de.taimos.gpsd4java.types.PollObject;
import de.taimos.gpsd4java.types.TPVObject;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultScheduledPollConsumer;

/**
 * The scheduled GPSD consumer.
 */
public class GpsdScheduledConsumer extends DefaultScheduledPollConsumer {

    // Constructors

    public GpsdScheduledConsumer(GpsdEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }

    // Overridden

    @Override
    protected int poll() throws Exception {
        log.debug("Starting scheduled GPSD consumer poll.");

        PollObject pollObject = getEndpoint().getGpsd4javaEndpoint().poll();
        if (pollObject != null && pollObject.getFixes().size() > 0 && pollObject.getFixes().get(0) instanceof TPVObject) {
            GpsdHelper.consumeTPVObject(pollObject.getFixes().get(0), getProcessor(), getEndpoint(), getExceptionHandler());
            return 1;
        }
        log.debug("No results returned from the GPSD poll.");
        return 0;
    }

    @Override
    public GpsdEndpoint getEndpoint() {
        return (GpsdEndpoint) super.getEndpoint();
    }

}