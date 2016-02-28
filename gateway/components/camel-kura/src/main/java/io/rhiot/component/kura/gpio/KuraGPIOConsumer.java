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

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.eclipse.kura.gpio.KuraGPIOPin;
import org.eclipse.kura.gpio.PinStatusListener;

/**
 * The Pin consumer.
 */
public class KuraGPIOConsumer extends DefaultConsumer implements PinStatusListener {

    private final KuraGPIOEndpoint endpoint;
    private final KuraGPIOPin pin;

    public KuraGPIOConsumer(KuraGPIOEndpoint endpoint, Processor processor, KuraGPIOPin pin) {
        super(endpoint, processor);
        this.endpoint = endpoint;
        this.pin = pin;
    }

    @Override
    protected void doShutdown() throws Exception {
        super.doShutdown();
        pin.close();
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        if (!pin.isOpen()) {
            pin.open();
        }
        pin.addPinStatusListener(this);
        log.trace("Start Listening GPIO name '{}'  id '{}'", getPin().getName(), getPin().getIndex());
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        if (pin.isOpen()) {
            pin.removePinStatusListener(this);
            pin.close();
        }
        log.trace("Stop Listening GPIO name '{}' id '{}'", getPin().getName(), getPin().getIndex());
    }

    public KuraGPIOPin getPin() {
        return pin;
    }

    @Override
    public void pinStatusChange(boolean value) {
        Exchange exchange = endpoint.createExchange();

        exchange.getIn().setHeader(KuraGPIOConstants.CAMEL_KURA_GPIO_ID, getPin().getIndex());
        exchange.getIn().setHeader(KuraGPIOConstants.CAMEL_KURA_GPIO_NAME, getPin().getName());
        exchange.getIn().setBody(value);

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
}
