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

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;

import com.pi4j.io.i2c.I2CDevice;

/**
 * The I2C consumer.
 */
public class I2CConsumer extends ScheduledPollConsumer implements I2CDevice {
    private final I2CDevice device;

    public I2CConsumer(I2CEndpoint endpoint, Processor processor, I2CDevice device) {
        super(endpoint, processor);
        this.device = device;
    }

    protected void createBody(Exchange exchange) throws IOException {
        byte[] buffer = null;
        if (getEndpoint().getReadAction() != null) {
            switch (getEndpoint().getReadAction()) {
            case READ:
                exchange.getIn().setBody(read(), Integer.class);
                break;
            case READ_ADDR:
                exchange.getIn().setBody(read(getEndpoint().getAddress()), Integer.class);
                break;
            case READ_BUFFER:
                buffer = new byte[getEndpoint().getBufferSize()];
                read(buffer, getEndpoint().getOffset(), getEndpoint().getSize());
                exchange.getIn().setBody(buffer, byte[].class);
                break;
            case READ_ADDR_BUFFER:
                buffer = new byte[getEndpoint().getBufferSize()];
                read(getEndpoint().getAddress(), buffer, getEndpoint().getOffset(), getEndpoint().getSize());
                exchange.getIn().setBody(buffer, byte[].class);
                break;
            default:
                break;
            }
        } else {
            // to Impl
        }

    }

    public I2CDevice getDevice() {
        return device;
    }

    @Override
    protected int poll() throws Exception {
        Exchange exchange = getEndpoint().createExchange();
        try {
            createBody(exchange);
            getProcessor().process(exchange);
            return 1; // number of messages polled
        } finally {
            // log exception if an exception occurred and was not handled
            if (exchange.getException() != null) {
                getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
            }
        }
    }

    @Override
    public int read() throws IOException {
        return this.device.read();
    }

    @Override
    public int read(byte[] buffer, int offset, int size) throws IOException {
        return this.device.read(buffer, offset, size);
    }

    @Override
    public int read(byte[] writeBuffer, int writeOffset, int writeSize, byte[] buffer, int offset, int size)
            throws IOException {
        return this.device.read(writeBuffer, writeOffset, writeSize, buffer, offset, size);
    }

    @Override
    public int read(int address) throws IOException {
        return this.device.read(address);
    }

    public int readU16BigEndian(int register) throws IOException {
        int hi = read(register);
        int lo = read(register + 1);
        return (hi << 8) + lo;
    }

    public int readU16LittleEndian(int register) throws IOException {
        int lo = read(register);
        int hi = read(register + 1);
        return (lo << 8) + hi;
    }

    @Override
    public int read(int address, byte[] buffer, int offset, int size) throws IOException {
        return this.device.read(address, buffer, offset, size);
    }

    @Override
    public void write(byte b) throws IOException {
        this.device.write(b);
    }

    @Override
    public void write(byte[] buffer, int offset, int size) throws IOException {
        this.device.write(buffer, offset, size);
    }

    @Override
    public void write(int address, byte b) throws IOException {
        this.device.write(address, b);
    }

    @Override
    public void write(int address, byte[] buffer, int offset, int size) throws IOException {
        this.device.write(address, buffer, offset, size);
    }

    public void sleep(long howMuch) {
        try {
            Thread.sleep(howMuch);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    @Override
    public I2CEndpoint getEndpoint() {
        return (I2CEndpoint) super.getEndpoint();
    }
}
