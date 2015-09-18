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
package io.rhiot.component.pi4j.i2c;

import java.io.IOException;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.RaspiGpioProvider;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactoryProvider;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.mockito.Mockito;

public class I2CReadTest extends CamelTestSupport {

    private static int I2CInt = 121;

    @Test
    public void consumeI2CDevice() throws Exception {

        MockEndpoint mock = getMockEndpoint("mock:result");

        mock.expectedMessageCount(1);
        mock.expectedBodiesReceived(I2CInt);

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {

                I2CFactoryProvider factory = Mockito.mock(I2CFactoryProvider.class);
                I2CBus bus = Mockito.mock(I2CBus.class);
                I2CDevice device = Mockito.mock(I2CDevice.class);

                try {
                    Mockito.when(factory.getBus(1)).thenReturn(bus);
                    Mockito.when(bus.getDevice(12)).thenReturn(device);
                    Mockito.when(device.read()).thenReturn(I2CInt);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                I2CFactory.setFactory(factory);
                GpioFactory.setDefaultProvider(Mockito.mock(RaspiGpioProvider.class));

                from("pi4j-i2c://1/12?readAction=READ").to("mock:result");
            }
        };
    }
}
