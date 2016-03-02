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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

import jdk.dio.ClosedDeviceException;
import jdk.dio.UnavailableDeviceException;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;

/**
 * The DeviceIO GPIO producer.
 */
public class GPIOProducer extends DefaultProducer {

    private GPIOPin pin = null;
    private ExecutorService pool;

    public GPIOProducer(GPIOEndpoint endpoint, GPIOPin pin) {
        super(endpoint);
        this.pin = pin;
        this.pool = this.getEndpoint().getCamelContext().getExecutorServiceManager().newSingleThreadExecutor(this,
                DeviceIOConstants.CAMEL_DEVICE_IO_THREADPOOL + pin.getDescriptor().getID());
    }

    @Override
    protected void doShutdown() throws Exception {
        // 2 x (delay + timeout) + 5s
        long timeToWait = (getEndpoint().getDelay() + getEndpoint().getDuration()) * 2 + 5000;
        log.debug("Wait for {} ms", timeToWait);
        pool.awaitTermination(timeToWait, TimeUnit.MILLISECONDS);
        pin.setValue(getEndpoint().isShutdownState());
        pin.close(); // TODO check this part
        log.debug("Pin {} {}", pin.getDescriptor().getID(), pin.getValue());
    }

    /**
     * Process the message
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        log.debug(exchange.toString());

        if (pin instanceof GPIOPin) {

            GPIOAction messageAction = exchange.getIn().getHeader(DeviceIOConstants.CAMEL_DEVICE_IO_ACTION,
                    getEndpoint().getAction(), GPIOAction.class);

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
                    break;
                }
            } else {
                if (pin.getDirection() == GPIOPinConfig.DIR_OUTPUT_ONLY && exchange.getIn().getBody() != null) {
                    setValue(exchange.getIn().getBody(Boolean.class));
                } else if ((pin.getDirection() == GPIOPinConfig.DIR_OUTPUT_ONLY && exchange.getIn().getBody() == null)
                        || pin.getDirection() == GPIOPinConfig.DIR_INPUT_ONLY) {
                    exchange.getIn().setBody(pin.getValue(), Boolean.class);
                    exchange.getIn().setHeader(DeviceIOConstants.CAMEL_DEVICE_IO_PIN, pin.getDescriptor().getID());
                    exchange.getIn().setHeader(DeviceIOConstants.CAMEL_DEVICE_IO_NAME, pin.getDescriptor().getName());
                    exchange.getIn().setHeader(DeviceIOConstants.CAMEL_DEVICE_IO_TIMESTAMP, System.currentTimeMillis());

                }
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

    @Override
    public GPIOEndpoint getEndpoint() {
        return (GPIOEndpoint) super.getEndpoint();
    }

}
