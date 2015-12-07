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

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.eclipse.kura.gpio.GPIOService;
import org.eclipse.kura.gpio.KuraClosedDeviceException;
import org.eclipse.kura.gpio.KuraGPIODeviceException;
import org.eclipse.kura.gpio.KuraGPIODirection;
import org.eclipse.kura.gpio.KuraGPIOMode;
import org.eclipse.kura.gpio.KuraGPIOPin;
import org.eclipse.kura.gpio.KuraGPIOTrigger;
import org.eclipse.kura.gpio.KuraUnavailableDeviceException;
import org.eclipse.kura.gpio.PinStatusListener;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

public class GpioConsumerTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:test")
    MockEndpoint mock;

    StubKuraGPIOPin pin = new StubKuraGPIOPin();



    @Test
    public void shouldProcessPinChangeEvent() throws InterruptedException {
        mock.setExpectedMessageCount(1);
        pin.pinStatusListener.pinStatusChange(true);
        mock.assertIsSatisfied();
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();

        GPIOService gpioService = mock(GPIOService.class, RETURNS_DEEP_STUBS);
        registry.bind("gpioService", gpioService);

        given(gpioService.getPinByTerminal(anyInt(),
                any(KuraGPIODirection.class), any(KuraGPIOMode.class), any(KuraGPIOTrigger.class))).
                willReturn(pin);

        return registry;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("kura-gpio:13?mode=INPUT_PULL_UP&state=false").to("mock:test");
            }
        };
    }

    // Stub classes

    static class StubKuraGPIOPin implements KuraGPIOPin {

        public PinStatusListener pinStatusListener;

        @Override
        public void setValue(boolean b) throws KuraUnavailableDeviceException, KuraClosedDeviceException, IOException {

        }

        @Override
        public boolean getValue() throws KuraUnavailableDeviceException, KuraClosedDeviceException, IOException {
            return false;
        }

        @Override
        public void addPinStatusListener(PinStatusListener pinStatusListener) throws KuraClosedDeviceException, IOException {
            this.pinStatusListener = pinStatusListener;
        }

        @Override
        public void removePinStatusListener(PinStatusListener pinStatusListener) throws KuraClosedDeviceException, IOException {

        }

        @Override
        public void open() throws KuraGPIODeviceException, KuraUnavailableDeviceException, IOException {

        }

        @Override
        public void close() throws IOException {

        }

        @Override
        public KuraGPIODirection getDirection() {
            return null;
        }

        @Override
        public KuraGPIOMode getMode() {
            return null;
        }

        @Override
        public KuraGPIOTrigger getTrigger() {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public int getIndex() {
            return 0;
        }

        @Override
        public boolean isOpen() {
            return false;
        }
    }

}