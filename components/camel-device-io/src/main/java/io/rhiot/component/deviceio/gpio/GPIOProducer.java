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

import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultProducer;

import jdk.dio.ClosedDeviceException;
import jdk.dio.UnavailableDeviceException;
import jdk.dio.gpio.GPIOPin;

/**
 * The DeviceIO GPIO producer.
 */
public class GPIOProducer extends DefaultProducer {

    private GPIOEndpoint endpoint;
    private GPIOPin pin = null;
    private GPIOAction action;

    public GPIOProducer(GPIOEndpoint endpoint, GPIOPin pin) {
        super(endpoint);
        this.endpoint = endpoint;
        this.pin = pin;
        this.action = endpoint.getAction();

    }

    protected GPIOAction resolveAction(Message message) {
        if (message.getHeaders().containsKey(DeviceIOConstants.CAMEL_DEVICE_IO_ACTION)) {
            // Exchange Action
            return message.getHeader(DeviceIOConstants.CAMEL_DEVICE_IO_ACTION, GPIOAction.class);
        } else {
            return action; // Endpoint Action
        }
    }

    /**
     * Process the message
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        log.debug(exchange.toString());

        if (pin instanceof GPIOPin) {

            GPIOAction messageAction = resolveAction(exchange.getIn());

            if (messageAction != null) {
                switch (messageAction) {
                case HIGH:
                    setValue(true);
                    break;
                case LOW:
                    setValue(false);
                    break;
                case TOGGLE:
                    setValue(!pin.getValue());
                    break;
                }
            } else {
                setValue(exchange.getIn().getBody(Boolean.class));
            }
        }
    }

    private void setValue(boolean b) throws UnavailableDeviceException, ClosedDeviceException, IOException {
        pin.setValue(b);
    }

    public void setPin(GPIOPin pin) {
        this.pin = pin;
    }

    public GPIOPin getPin() {
        return pin;
    }
}
