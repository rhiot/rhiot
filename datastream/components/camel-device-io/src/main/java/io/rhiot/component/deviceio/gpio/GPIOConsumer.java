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

package io.rhiot.component.deviceio.gpio;

import io.rhiot.component.deviceio.DeviceIOConstants;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;

import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.PinEvent;
import jdk.dio.gpio.PinListener;
import jdk.dio.gpio.PortEvent;
import jdk.dio.gpio.PortListener;

/**
 * The DeviceIO GPIO consumer.
 */
public class GPIOConsumer extends DefaultConsumer implements PinListener, PortListener {

    private final GPIOEndpoint endpoint;
    private final GPIOPin pin;

    public GPIOConsumer(GPIOEndpoint endpoint, Processor processor, GPIOPin pin) {
        super(endpoint, processor);
        this.endpoint = endpoint;
        this.pin = pin;

    }

    @Override
    protected void doStart() throws Exception {
        pin.setInputListener(this);
        log.trace("Start Listening GPIO {}", pin);
    }

    @Override
    protected void doStop() throws Exception {
        log.trace("Stop Listening GPIO {}", pin);
    }

    public GPIOPin getPin() {
        return pin;
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
    public void valueChanged(PinEvent event) {
        Exchange exchange = endpoint.createExchange();
        exchange.getIn().setBody(event.getValue());
        exchange.getIn().setHeader(DeviceIOConstants.CAMEL_DEVICE_IO_PIN, event.getDevice().getDescriptor().getID());
        exchange.getIn().setHeader(DeviceIOConstants.CAMEL_DEVICE_IO_NAME, event.getDevice().getDescriptor().getName());
        exchange.getIn().setHeader(DeviceIOConstants.CAMEL_DEVICE_IO_TIMESTAMP, event.getTimeStamp());
        sendEvent(exchange);
    }

    @Deprecated
    @Override
    public void valueChanged(PortEvent event) {
        Exchange exchange = endpoint.createExchange();
        exchange.getIn().setBody(event.getValue());
        exchange.getIn().setHeader(DeviceIOConstants.CAMEL_DEVICE_IO_PIN, event.getDevice().getDescriptor().getID());
        exchange.getIn().setHeader(DeviceIOConstants.CAMEL_DEVICE_IO_NAME, event.getDevice().getDescriptor().getName());
        exchange.getIn().setHeader(DeviceIOConstants.CAMEL_DEVICE_IO_TIMESTAMP, event.getTimeStamp());
        sendEvent(exchange);
    }
}
