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
import io.rhiot.component.tinkerforge.TinkerforgeConsumer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.io.IOException;
import java.util.List;

public class IO4Consumer extends TinkerforgeConsumer<IO4Endpoint, BrickletIO4> implements BrickletIO4.InterruptListener {
    private final IO4Endpoint endpoint;

    public IO4Consumer(IO4Endpoint endpoint, Processor processor) throws IOException, AlreadyConnectedException {
        super(endpoint, processor, BrickletIO4.DEVICE_IDENTIFIER);
        this.endpoint = endpoint;
    }

    @Override
    protected BrickletIO4 createBricklet(String uid, IPConnection connection) {
        return new BrickletIO4(uid, connection);
    }

    @Override
    protected void configureBricklet() throws TimeoutException, NotConnectedException {
        bricklet.setConfiguration((short)(1 << endpoint.getIopin()), BrickletIO4.DIRECTION_IN, true);
        bricklet.setInterrupt((short)(1 << endpoint.getIopin()));
        bricklet.addInterruptListener(this);
        bricklet.setDebouncePeriod(endpoint.getDebounce());
    }

    @Override
    public void interrupt(short interruptMask, short valueMask) {
        List<BinaryAnalyser.ResultSet> results = BinaryAnalyser.analyse(interruptMask, valueMask);
        for (BinaryAnalyser.ResultSet result : results) {
            Exchange exchange = null;
            try {
                exchange = createOutOnlyExchangeWithBodyAndHeaders(endpoint, result.value, bricklet.getIdentity());
                exchange.getIn().setHeader("com.tinkerforge.bricklet.io.pin", result.index-1); //correct 1-based

                getProcessor().process(exchange);
            } catch (Exception e) {
                getExceptionHandler().handleException("Error processing exchange", exchange, e);
            } finally {
                if (exchange.getException() != null) {
                    getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
                }
            }
        }
    }
}