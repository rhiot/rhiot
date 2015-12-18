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

import java.util.concurrent.ExecutorService;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinAnalogOutput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.PinMode;

/**
 * The Pin producer.
 */
public class GPIOProducer extends DefaultProducer {

    private GpioPin pin;
    private GPIOAction action;
    private ExecutorService pool;

    /**
     * Create Producer to PIN with OUTPUT mode
     *
     * @param endpoint
     *            the endpoint
     * @param pin
     *            the pin to manage
     * @param action
     *            the action to do
     */
    public GPIOProducer(GPIOEndpoint endpoint, GpioPin pin, GPIOAction action) {
        super(endpoint);

        this.pin = pin;
        this.action = action;
        this.pool = this.getEndpoint().getCamelContext().getExecutorServiceManager().newSingleThreadExecutor(this,
                Pi4jConstants.CAMEL_PI4J_GPIO_THREADPOOL);
    }

    public GpioPin getPin() {
        return pin;
    }

    private void output(Exchange exchange, Object value) {

        //
        PinMode mode = pin.getMode();
        log.debug("Mode > " + mode + " for " + pin);

        // Check mode
        switch (mode) {

        case DIGITAL_OUTPUT:
            Boolean outputBoolean = exchange.getContext().getTypeConverter().convertTo(Boolean.class, value);
            ((GpioPinDigitalOutput) pin).setState(outputBoolean);
            break;

        case ANALOG_OUTPUT:
            Double outputDouble = exchange.getContext().getTypeConverter().convertTo(Double.class, value);
            ((GpioPinAnalogOutput) pin).setValue(outputDouble);
            break;

        case PWM_OUTPUT:
            Integer outputInt = exchange.getContext().getTypeConverter().convertTo(Integer.class, value);
            ((GpioPinPwmOutput) pin).setPwm(outputInt);

            break;

        case ANALOG_INPUT:
        case DIGITAL_INPUT:
            log.error("Cannot output with INPUT PinMode");
            break;

        default:
            log.error("Any PinMode found");
            break;
        }

    }

    /**
     * Process the message
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        if (log.isTraceEnabled()) {
            log.trace(exchange.toString());
        }

        GPIOAction messageAction = exchange.getIn().getHeader(Pi4jConstants.CAMEL_RBPI_PIN_ACTION, action,
                GPIOAction.class);

        if (messageAction == null) {
            log.trace("No action pick up body");
            this.output(exchange, exchange.getIn().getBody());
        } else {
            log.trace("action= {} ", action);
            switch (messageAction) {

            case TOGGLE:
                if (pin.getMode() == PinMode.DIGITAL_OUTPUT) {
                    ((GpioPinDigitalOutput) pin).toggle();
                }
                break;

            case LOW:
                if (pin.getMode() == PinMode.DIGITAL_OUTPUT) {
                    ((GpioPinDigitalOutput) pin).low();
                }
                break;

            case HIGH:
                if (pin.getMode() == PinMode.DIGITAL_OUTPUT) {
                    ((GpioPinDigitalOutput) pin).high();
                }
                break;

            case BLINK:
                if (pin.getMode() == PinMode.DIGITAL_OUTPUT) {

                    pool.submit(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                Thread.sleep(getEndpoint().getDelay());
                                ((GpioPinDigitalOutput) pin).toggle();
                                Thread.sleep(getEndpoint().getDuration());
                                ((GpioPinDigitalOutput) pin).toggle();
                            } catch (InterruptedException e) {
                                log.error("Thread interruption into BLINK sequence", e);
                            }
                        }
                    });
                }

                break;

            default:
                log.error("Any action set found");
                break;
            }
        }
    }

    public void setPin(GpioPin pin) {
        this.pin = pin;
    }

    @Override
    public GPIOEndpoint getEndpoint() {
        return (GPIOEndpoint) super.getEndpoint();
    }

}
