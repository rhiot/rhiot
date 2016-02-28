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
package io.rhiot.component.kura.gpio;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.eclipse.kura.gpio.KuraClosedDeviceException;
import org.eclipse.kura.gpio.KuraGPIODirection;
import org.eclipse.kura.gpio.KuraGPIOPin;
import org.eclipse.kura.gpio.KuraUnavailableDeviceException;

/**
 * The Pin producer.
 */
public class KuraGPIOProducer extends DefaultProducer {

    private KuraGPIOPin pin;
    private KuraGPIOAction action;
    private ExecutorService pool;

    public KuraGPIOProducer(KuraGPIOEndpoint endpoint, KuraGPIOPin pin) {
        super(endpoint);

        this.pin = pin;
        this.action = endpoint.getAction();
        this.pool = this.getEndpoint().getCamelContext().getExecutorServiceManager().newSingleThreadExecutor(this,
                KuraGPIOConstants.CAMEL_KURA_GPIO_THREADPOOL + pin.getIndex());
    }

    private void output(Exchange exchange)
            throws KuraUnavailableDeviceException, KuraClosedDeviceException, IOException {

        //
        KuraGPIODirection direction = pin.getDirection();
        log.debug("Mode > " + direction + " for " + pin);

        // Check mode
        switch (direction) {

        case OUTPUT:
            Boolean outputBoolean = exchange.getIn().getBody(Boolean.class);
            pin.setValue(outputBoolean);
            break;

        case INPUT:
            exchange.getIn().setHeader(KuraGPIOConstants.CAMEL_KURA_GPIO_ID, getPin().getIndex());
            exchange.getIn().setHeader(KuraGPIOConstants.CAMEL_KURA_GPIO_NAME, getPin().getName());
            exchange.getIn().setBody(pin.getValue());
            break;

        default:
            log.error("Any PinDirection found");
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

        if (pin.isOpen()) {

            KuraGPIOAction messageAction = exchange.getIn().getHeader(KuraGPIOConstants.CAMEL_KURA_GPIO_ACTION,
                    getAction(), KuraGPIOAction.class);

            if (messageAction == null) {
                log.trace("No action pick up body");
                output(exchange);
            } else {
                log.trace("action= {} ", action);
                switch (messageAction) {

                case TOGGLE:
                    pin.setValue(!pin.getValue());
                    break;

                case LOW:
                    pin.setValue(false);
                    break;

                case HIGH:
                    pin.setValue(true);
                    break;

                case BLINK:

                    pool.submit(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                Thread.sleep(getEndpoint().getDelay());
                                pin.setValue(!pin.getValue());
                                Thread.sleep(getEndpoint().getDuration());
                                pin.setValue(!pin.getValue());
                            } catch (Exception e) {
                                log.error("Thread interruption into BLINK sequence", e);
                            }
                        }
                    });

                    break;

                default:
                    log.error("Any action set found");
                    break;
                }
            }
        } else {
            log.warn("Pin {} is close", pin.getIndex());
        }
    }

    @Override
    protected void doStop() throws Exception {
        // 2 x (delay + timeout) + 5s
        long timeToWait = (getEndpoint().getDelay() + getEndpoint().getDuration()) * 2 + 5000;
        log.debug("Wait for {} ms", timeToWait);
        pool.awaitTermination(timeToWait, TimeUnit.MILLISECONDS);
        pin.setValue(getEndpoint().isShutdownState());
        log.debug("Pin {} {}", pin.getIndex(), pin.getValue());
        pin.close();
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        if (!pin.isOpen()) {
            pin.open();
        }
        pin.setValue(getEndpoint().isState());
    }

    public KuraGPIOPin getPin() {
        return pin;
    }

    public void setPin(KuraGPIOPin pin) {
        this.pin = pin;
    }

    public KuraGPIOAction getAction() {
        return action;
    }

    public void setAction(KuraGPIOAction action) {
        this.action = action;
    }

    @Override
    public KuraGPIOEndpoint getEndpoint() {
        return (KuraGPIOEndpoint) super.getEndpoint();
    }
}
