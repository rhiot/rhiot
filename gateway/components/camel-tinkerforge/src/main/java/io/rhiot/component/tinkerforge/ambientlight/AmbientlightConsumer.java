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
package io.rhiot.component.tinkerforge.ambientlight;

import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import io.rhiot.component.tinkerforge.TinkerforgeConsumer;
import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickletAmbientLight;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class AmbientlightConsumer extends TinkerforgeConsumer<AmbientlightEndpoint, BrickletAmbientLight> implements BrickletAmbientLight.IlluminanceListener {
	
    private final AmbientlightEndpoint endpoint;

    public AmbientlightConsumer(AmbientlightEndpoint endpoint, Processor processor) throws IOException, AlreadyConnectedException {
        super(endpoint, processor, BrickletAmbientLight.DEVICE_IDENTIFIER);
        this.endpoint = endpoint;
    }

    @Override
    protected BrickletAmbientLight createBricklet(String uid, IPConnection connection) {
        return new BrickletAmbientLight(uid, connection);
    }

    @Override
    protected void configureBricklet() throws TimeoutException, NotConnectedException {
        bricklet.setIlluminanceCallbackPeriod(endpoint.getInterval());
        bricklet.addIlluminanceListener(this);
    }

    @Override
    public void illuminance(int illuminance) {
        Exchange exchange = null;
        try {
            exchange = createOutOnlyExchangeWithBodyAndHeaders(endpoint, illuminance, bricklet.getIdentity());
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