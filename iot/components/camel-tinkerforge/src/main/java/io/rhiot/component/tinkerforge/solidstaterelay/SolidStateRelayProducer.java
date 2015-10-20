/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rhiot.component.tinkerforge.solidstaterelay;

import com.tinkerforge.*;
import io.rhiot.component.tinkerforge.TinkerforgeProducer;
import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;

import java.io.IOException;

public class SolidStateRelayProducer extends TinkerforgeProducer<SolidStateRelayEndpoint, BrickletSolidStateRelay> {

    public SolidStateRelayProducer(SolidStateRelayEndpoint endpoint) throws IOException, AlreadyConnectedException {
        super(endpoint, BrickletSolidStateRelay.DEVICE_IDENTIFIER);
    }

    @Override
    protected BrickletSolidStateRelay createBricklet(String uid, IPConnection connection) {
        return new BrickletSolidStateRelay(uid, connection);
    }

    @Override
    protected void configureBricklet() throws TimeoutException, NotConnectedException {
    }

    public void process(Exchange exchange) throws InvalidPayloadException, TimeoutException, NotConnectedException {
        Integer duration = exchange.getIn().getHeader("duration", endpoint.getDuration(), Integer.class);
        String message = exchange.getIn().getMandatoryBody(String.class);

        boolean value;
        switch (message) {
            case "false" :
            case "off" :
            case "0" :
            case "no" :
            case "low" :
                value = false;
                break;

            case "true" :
            case "on" :
            case "1" :
            case "yes" :
            case "high" :
                value = true;
                break;

            default:
                throw new IllegalArgumentException("Unrecognized message: "+message);
        }

        if (duration == 0) {
            bricklet.setState(value);
            log.debug("Set relay to "+value);
        } else {
            bricklet.setMonoflop(value, duration);
            log.debug("Set relay to "+value+" for duration "+duration+" ms.");
        }
    }
}
