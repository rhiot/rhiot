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
package io.rhiot.component.pi4j.output;

import io.rhiot.component.pi4j.mock.RaspiGpioProviderMock;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.RaspiPin;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.mockito.Mockito;

public class AnalogOutputBodyMockitoTest extends CamelTestSupport {

    public static final RaspiGpioProviderMock MOCK_RASPI = Mockito.spy(new RaspiGpioProviderMock());

    public static final int INT_RESULT = 121;
    public static final double DOUBLE_RESULT = 64.3;

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint mock;

    @Produce(uri = "direct:start")
    protected ProducerTemplate sender;

    @Test
    public void produceAnalogOutputBodyTest() throws Exception {

        mock.expectedMessageCount(1);
        mock.expectedBodiesReceived(64.3);
        sender.sendBody(INT_RESULT);

        assertMockEndpointsSatisfied();

        Mockito.verify(MOCK_RASPI).setPwm(RaspiPin.GPIO_23, 121);
        Mockito.verify(MOCK_RASPI).setValue(RaspiPin.GPIO_24, 64.3);
        Mockito.verify(MOCK_RASPI).setMode(RaspiPin.GPIO_23, PinMode.PWM_OUTPUT);
        Mockito.verify(MOCK_RASPI).setMode(RaspiPin.GPIO_24, PinMode.ANALOG_OUTPUT);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                Mockito.when(MOCK_RASPI.getMode(RaspiPin.GPIO_23)).thenReturn(PinMode.PWM_OUTPUT);
                Mockito.when(MOCK_RASPI.getMode(RaspiPin.GPIO_24)).thenReturn(PinMode.ANALOG_OUTPUT);

                GpioFactory.setDefaultProvider(MOCK_RASPI);
                from("direct:start").to("pi4j-gpio://23?mode=PWM_OUTPUT").transform().simple("" + DOUBLE_RESULT)
                        .to("pi4j-gpio://24?mode=ANALOG_OUTPUT").to("mock:result");
            }
        };
    }
}
