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
package io.rhiot.component.tinkerforge.lcd20x4;

import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;

import io.rhiot.component.tinkerforge.TinkerforgeProducer;
import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class Lcd20x4Producer extends TinkerforgeProducer<Lcd20x4Endpoint, BrickletLCD20x4> {
    private Lcd20x4Endpoint endpoint;
    
    public Lcd20x4Producer(Lcd20x4Endpoint endpoint) throws IOException, AlreadyConnectedException {
        super(endpoint, BrickletLCD20x4.DEVICE_IDENTIFIER);
        this.endpoint = endpoint;
    }

    @Override
    protected BrickletLCD20x4 createBricklet(String uid, IPConnection connection) {
        return new BrickletLCD20x4(uid, connection);
    }

    @Override
    protected void configureBricklet() throws TimeoutException, NotConnectedException {
        bricklet.clearDisplay();
        bricklet.setConfig(endpoint.getCursor(), endpoint.getBlink());
        bricklet.backlightOn();
    }

    public void process(Exchange exchange) throws InvalidPayloadException, TimeoutException, NotConnectedException {
        short line = exchange.getIn().getHeader("line", endpoint.getLine(), Short.class);
        short position = exchange.getIn().getHeader("position", endpoint.getPosition(), Short.class);

        bricklet.writeLine(line, position, exchange.getIn().getMandatoryBody(String.class));
    	log.debug("Wrote text '"+exchange.getIn().getBody(String.class)+ "' to line "+line+" on position "+position);
    }
}