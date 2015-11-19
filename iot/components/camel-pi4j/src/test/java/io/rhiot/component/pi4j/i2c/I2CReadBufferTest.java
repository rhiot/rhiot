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

import io.rhiot.component.pi4j.mock.MockI2CDevice;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.RaspiGpioProvider;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactoryProvider;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.mockito.Mockito;

public class I2CReadBufferTest extends CamelTestSupport {

    @Test
    public void consumeAnalogEvent() throws Exception {
        byte result[] = { 0b00000000, 0b00000100, 0b00000111, 0b00001010 };

        MockEndpoint mock = getMockEndpoint("mock:result");

        mock.expectedMessageCount(1);
        mock.expectedBodiesReceived(result);

        assertMockEndpointsSatisfied();

    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                I2CFactoryProvider factory = Mockito.mock(I2CFactoryProvider.class);
                I2CBus bus = Mockito.mock(I2CBus.class);

                try {
                    Mockito.when(factory.getBus(0)).thenReturn(bus);
                    Mockito.when(bus.getDevice(24)).thenReturn(new MockI2CDevice());

                } catch (IOException e) {
                    e.printStackTrace();
                }
                I2CFactory.setFactory(factory);
                GpioFactory.setDefaultProvider(Mockito.mock(RaspiGpioProvider.class));

                from("pi4j-i2c://0/0x18?readAction=READ_BUFFER&offset=1&size=3&bufferSize=4").to("mock:result");
            }
        };
    }
}
