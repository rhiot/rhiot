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
package io.rhiot.component.deviceio.i2c;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

import jdk.dio.ClosedDeviceException;
import jdk.dio.DeviceDescriptor;
import jdk.dio.UnavailableDeviceException;
import jdk.dio.i2cbus.I2CDevice;

/**
 * The I2C producer.
 */
public class I2CProducer extends DefaultProducer implements I2CDevice {

    protected I2CDevice device;

    public I2CProducer(I2CEndpoint endpoint, I2CDevice device) {
        super(endpoint);
        this.device = device;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Object o = exchange.getIn().getBody();

        if (o instanceof Byte) {
            if (getEndpoint().getAddress() >= 0) {
                ByteBuffer buffer = ByteBuffer.allocate(1);
                buffer.put((Byte) o);
                write(getEndpoint().getAddress(), 1, buffer);
            } else {
                write((Byte) o);
            }
        } else if (o instanceof Byte[]) {
            ByteBuffer buffer = ByteBuffer.allocate(1);
            buffer.put((byte[]) o);

            if (getEndpoint().getAddress() >= 0) {
                write(getEndpoint().getAddress(), getEndpoint().getSize(), buffer);
            } else {
                write(0, getEndpoint().getSize(), buffer);
            }
        }
    }

    public I2CDevice getDevice() {
        return device;
    }

    @Override
    public int read() throws IOException {
        return this.device.read();
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

    @Override
    public void close() throws IOException {
        this.device.close();
    }

    @Override
    public <U extends I2CDevice> DeviceDescriptor<U> getDescriptor() {
        return this.device.getDescriptor();
    }

    @Override
    public boolean isOpen() {
        return this.device.isOpen();
    }

    @Override
    public void tryLock(int arg0) throws UnavailableDeviceException, ClosedDeviceException, IOException {
        this.device.tryLock(arg0);
    }

    @Override
    public void unlock() throws IOException {
        this.device.unlock();
    }

    @Override
    public ByteBuffer getInputBuffer() throws ClosedDeviceException, IOException {
        return this.device.getInputBuffer();
    }

    @Override
    public ByteBuffer getOutputBuffer() throws ClosedDeviceException, IOException {
        return this.device.getOutputBuffer();
    }

    @Override
    public void begin() throws IOException, UnavailableDeviceException, ClosedDeviceException {
        this.device.begin();
    }

    @Override
    public void end() throws IOException, UnavailableDeviceException, ClosedDeviceException {
        this.device.end();
    }

    @Override
    public Bus getBus() throws IOException {
        return this.device.getBus();
    }

    @Override
    public int read(ByteBuffer arg0) throws IOException, UnavailableDeviceException, ClosedDeviceException {
        return this.device.read(arg0);
    }

    @Override
    public int read(int arg0, ByteBuffer arg1) throws IOException, UnavailableDeviceException, ClosedDeviceException {
        return this.device.read(arg0, arg1);
    }

    @Override
    public int read(int arg0, int arg1, ByteBuffer arg2)
            throws IOException, UnavailableDeviceException, ClosedDeviceException {
        return this.device.read(arg0, arg1, arg2);
    }

    @Override
    public int read(int arg0, int arg1, int arg2, ByteBuffer arg3)
            throws IOException, UnavailableDeviceException, ClosedDeviceException {
        return this.device.read(arg0, arg1, arg2, arg3);
    }

    @Override
    public int write(ByteBuffer arg0) throws IOException, UnavailableDeviceException, ClosedDeviceException {
        return this.device.write(arg0);
    }

    @Override
    public void write(int arg0) throws IOException, UnavailableDeviceException, ClosedDeviceException {
        this.device.write(arg0);
    }

    @Override
    public int write(int arg0, int arg1, ByteBuffer arg2)
            throws IOException, UnavailableDeviceException, ClosedDeviceException {
        return this.device.write(arg0, arg1, arg2);
    }

}
