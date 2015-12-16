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
package io.rhiot.component.pi4j.gpio;

import io.rhiot.component.pi4j.Pi4jConstants;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;

import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinAnalogValueChangeEvent;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerAnalog;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

/**
 * The Pin consumer.
 */
public class GPIOConsumer extends DefaultConsumer implements GpioPinListenerDigital, GpioPinListenerAnalog {

    private final GpioPin pin;
    private final PinState state;

    /**
     * Create Consumer mapped to INPUT pin
     * 
     * @param endpoint
     *            Camel Endpoint
     * @param processor
     *            Camel Processor
     * @param pin
     *            pin
     * @param state
     *            state
     */
    public GPIOConsumer(GPIOEndpoint endpoint, Processor processor, GpioPin pin, PinState state) {
        super(endpoint, processor);
        this.pin = pin;
        this.state = state;
    }

    @Override
    protected void doStart() throws Exception {
        pin.addListener(this);
        log.trace("Start Listening GPIO {}", pin.getPin().getAddress());
    }

    @Override
    protected void doStop() throws Exception {
        pin.removeListener(this);
        log.trace("Stop Listening GPIO {}", pin.getPin().getAddress());
    }

    public GpioPin getPin() {
        return pin;
    }

    public PinState getState() {
        return state;
    }

    @Override
    public void handleGpioPinAnalogValueChangeEvent(GpioPinAnalogValueChangeEvent event) {
        Exchange exchange = getEndpoint().createExchange();

        exchange.getIn().setBody(event);

        exchange.getIn().setHeader(Pi4jConstants.CAMEL_RBPI_PIN, event.getPin());
        exchange.getIn().setHeader(Pi4jConstants.CAMEL_RBPI_PIN_VALUE, event.getValue());
        exchange.getIn().setHeader(Pi4jConstants.CAMEL_RBPI_PIN_TYPE, event.getEventType());

        log.debug("GpioEvent pin {}, event {}, state {} ",
                new Object[] { event.getPin().getName(), event.getEventType().name(), event.getValue() });

        sendEvent(exchange);
    }

    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {

        log.debug("GpioEvent pin {}, event {}, state {} ",
                new String[] { event.getPin().getName(), event.getEventType().name(), event.getState().getName() });

        // We just listen correct state
        if (state != null && state != event.getState()) {
            log.debug("Consumer state {} != {} Event state --> ignore Event", state, event.getState());
            return;
        }

        Exchange exchange = getEndpoint().createExchange();

        exchange.getIn().setBody(event);

        exchange.getIn().setHeader(Pi4jConstants.CAMEL_RBPI_PIN, event.getPin());
        exchange.getIn().setHeader(Pi4jConstants.CAMEL_RBPI_PIN_STATE, event.getState());
        exchange.getIn().setHeader(Pi4jConstants.CAMEL_RBPI_PIN_TYPE, event.getEventType());

        sendEvent(exchange);
    }

    private void sendEvent(Exchange exchange) {
        try {

            getProcessor().process(exchange);

        } catch (Exception e) {
            exchange.setException(e);
        } finally {
            // log exception if an exception occurred and was not handled
            if (exchange.getException() != null) {
                getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
            }
        }
    }

    @Override
    public GPIOEndpoint getEndpoint() {
        return (GPIOEndpoint) super.getEndpoint();
    }
}
