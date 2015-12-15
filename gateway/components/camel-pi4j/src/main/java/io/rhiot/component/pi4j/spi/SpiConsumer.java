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
package io.rhiot.component.pi4j.spi;

import com.pi4j.io.spi.SpiDevice;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class SpiConsumer extends ScheduledPollConsumer implements SpiDevice {
    private final SpiEndpoint endpoint;
    private final SpiDevice spiDevice;

    public SpiConsumer(SpiEndpoint endpoint, Processor processor, SpiDevice spiDevice) {
        super(endpoint, processor);
        this.endpoint = endpoint;
        this.spiDevice = spiDevice;
    }

    public SpiDevice getSpiDevice() {
        return spiDevice;
    }

    protected void createBody(Exchange exchange) throws IOException {
        //TODO:
    }

    @Override
    protected int poll() throws Exception {
        Exchange exchange = endpoint.createExchange();
        createBody(exchange);
        getProcessor().process(exchange);
        return 1;
    }

    @Override
    public String write(String s, Charset charset) throws IOException {
        return spiDevice.write(s, charset);
    }

    @Override
    public String write(String s, String s1) throws IOException {
        return spiDevice.write(s, s1);
    }

    @Override
    public ByteBuffer write(ByteBuffer byteBuffer) throws IOException {
        return spiDevice.write(byteBuffer);
    }

    @Override
    public byte[] write(InputStream inputStream) throws IOException {
        return spiDevice.write(inputStream);
    }

    @Override
    public int write(InputStream inputStream, OutputStream outputStream) throws IOException {
        return spiDevice.write(inputStream, outputStream);
    }

    @Override
    public byte[] write(byte[] bytes, int i, int i1) throws IOException {
        return spiDevice.write(bytes, i, i1);
    }

    @Override
    public byte[] write(byte... bytes) throws IOException {
        return spiDevice.write(bytes);
    }

    @Override
    public short[] write(short[] shorts, int i, int i1) throws IOException {
        return spiDevice.write(shorts, i, i1);
    }

    @Override
    public short[] write(short... shorts) throws IOException {
        return spiDevice.write(shorts);
    }
}
