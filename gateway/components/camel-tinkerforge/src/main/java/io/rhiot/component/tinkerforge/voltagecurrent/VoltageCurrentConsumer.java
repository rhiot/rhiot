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
package io.rhiot.component.tinkerforge.voltagecurrent;

import com.tinkerforge.*;
import io.rhiot.component.tinkerforge.TinkerforgeConsumer;
import org.apache.camel.*;

import java.io.IOException;

public class VoltageCurrentConsumer extends TinkerforgeConsumer<VoltageCurrentEndpoint, BrickletVoltageCurrent> 
    implements BrickletVoltageCurrent.CurrentListener, BrickletVoltageCurrent.VoltageListener, BrickletVoltageCurrent.PowerListener {
    
    private final VoltageCurrentEndpoint endpoint;

    public VoltageCurrentConsumer(VoltageCurrentEndpoint endpoint, Processor processor) throws IOException, AlreadyConnectedException {
        super(endpoint, processor, BrickletVoltageCurrent.DEVICE_IDENTIFIER);
        this.endpoint = endpoint;
    }

    @Override
    protected BrickletVoltageCurrent createBricklet(String uid, IPConnection connection) {
        return new BrickletVoltageCurrent(uid, connection);
    }

    @Override
    protected void configureBricklet() throws TimeoutException, NotConnectedException {
        bricklet.setDebouncePeriod(endpoint.getDebounce());
        
        bricklet.setCurrentCallbackPeriod(endpoint.getInterval());
        bricklet.setVoltageCallbackPeriod(endpoint.getInterval());
        bricklet.setPowerCallbackPeriod(endpoint.getInterval());
        
        bricklet.addCurrentListener(this);
        bricklet.addVoltageListener(this);
        bricklet.addPowerListener(this);
    }

    @Override
    public void current(int current) {
        sendMessage(current, "current");
    }
    
    @Override
    public void power(int power) {
        sendMessage(power, "power");
    }
    
    @Override
    public void voltage(int voltage) {
        sendMessage(voltage, "voltage");
    }
    
    
    private void sendMessage(int value, String type) {
        Exchange exchange = null;
        try {
            exchange = createOutOnlyExchangeWithBodyAndHeaders(endpoint, value, bricklet.getIdentity());
            exchange.getIn().setHeader("com.tinkerforge.bricklet.voltagecurrent.type", type);
        } catch (TimeoutException | NotConnectedException e) {
            e.printStackTrace();
        }

        try {
            getProcessor().process(exchange);
        } catch (Exception e) {
            getExceptionHandler().handleException("Error processing exchange", exchange, e);
        } finally {
            if (exchange != null && exchange.getException() != null) {
                getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
            }
        }
    }

}