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
package io.rhiot.component.pi4j.spi.driver;

import com.pi4j.gpio.extension.mcp.MCP3008GpioProvider;
import com.pi4j.gpio.extension.mcp.MCP3008Pin;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import io.rhiot.component.pi4j.spi.SpiConsumer;
import io.rhiot.component.pi4j.spi.SpiEndpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.io.IOException;

public class MCP3008Consumer extends SpiConsumer {

    private MCP3008GpioProvider mcp3008GpioProvider;

    public MCP3008Consumer(SpiEndpoint endpoint, Processor processor, SpiDevice spiDevice) {
        super(endpoint, processor, spiDevice);
    }

    @Override
    public SpiEndpoint getEndpoint() {
        return (SpiEndpoint) super.getEndpoint();
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        mcp3008GpioProvider = new MCP3008GpioProvider(SpiChannel.getByNumber(getEndpoint().getChannelId()));
    }

    @Override
    protected void createBody(Exchange exchange) throws IOException {
        MCP3008Value body = new MCP3008Value();

        for (Pin pin : MCP3008Pin.ALL) {
            body.getValues().put(pin, mcp3008GpioProvider.getValue(pin));
        }

        exchange.getIn().setBody(body);
    }
}
