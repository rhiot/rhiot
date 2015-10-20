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
package io.rhiot.component.tinkerforge.io;

import com.tinkerforge.*;
import io.rhiot.component.tinkerforge.TinkerforgeProducer;
import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;

import java.io.IOException;

public class IO4Producer extends TinkerforgeProducer<IO4Endpoint, BrickletIO4> {
    private final IO4Endpoint endpoint;

    public IO4Producer(final IO4Endpoint endpoint) throws IOException, AlreadyConnectedException {
        super(endpoint, BrickletIO4.DEVICE_IDENTIFIER);
        this.endpoint = endpoint;
    }

    @Override
    protected BrickletIO4 createBricklet(String uid, IPConnection connection) {
        return new BrickletIO4(uid, connection);
    }

    @Override
    protected void configureBricklet() throws TimeoutException, NotConnectedException {
        bricklet.setConfiguration((short)(1 << endpoint.getIopin()), BrickletIO4.DIRECTION_OUT, false);
    }

    @Override
    public void process(Exchange exchange) throws TimeoutException, NotConnectedException, InvalidPayloadException {
        short iopin = exchange.getIn().getHeader("iopin", endpoint.getIopin(), Short.class);
        long duration = exchange.getIn().getHeader("duration", 0, Long.class);
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

        if (duration > 0) {
            log.debug("Setting pin "+iopin+" to value "+value+" for a duration of "+duration+" milliseconds. Message: "+message);
            bricklet.setMonoflop(iopin, iopin, duration);
        } else {
            log.debug("Setting pin "+iopin+" to value "+value+". Message: "+message);
            bricklet.setConfiguration(iopin, BrickletIO4.DIRECTION_OUT, value);
        }
    }
}