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

import io.rhiot.component.pi4j.mock.RaspberryPiRevision;
import io.rhiot.component.pi4j.mock.RaspiGpioProviderMock;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Assert;
import org.junit.Test;

public class DigitalOutput1Test extends CamelTestSupport {

    public static final RaspiGpioProviderMock MOCK_RASPI = new RaspiGpioProviderMock(RaspberryPiRevision.MODEL_2);

    static {
        // Mandatory we are not inside a Real Raspberry PI
        GpioFactory.setDefaultProvider(MOCK_RASPI);
    }

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint mock;

    @Produce(uri = "direct:start")
    protected ProducerTemplate sender;

    @Test
    public void produceDigitalOutput1Test() throws Exception {

        mock.expectedMessageCount(1);

        sender.sendBody("");

        assertMockEndpointsSatisfied();

        Assert.assertEquals("", PinState.HIGH, MOCK_RASPI.getState(RaspiPin.GPIO_17));
        Assert.assertEquals("", PinState.LOW, MOCK_RASPI.getState(RaspiPin.GPIO_18));
        Assert.assertEquals("", PinState.HIGH, MOCK_RASPI.getState(RaspiPin.GPIO_19));

    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("direct:start").to("log:io.rhiot.component.pi4j?showAll=true&multiline=true")
                    .to("pi4j-gpio://17?mode=DIGITAL_OUTPUT&state=LOW&action=TOGGLE").to("pi4j-gpio://18?mode=DIGITAL_OUTPUT&state=HIGH&action=LOW")
                    .to("pi4j-gpio://19?mode=DIGITAL_OUTPUT&state=LOW&action=HIGH").to("mock:result");

            }
        };
    }
}
