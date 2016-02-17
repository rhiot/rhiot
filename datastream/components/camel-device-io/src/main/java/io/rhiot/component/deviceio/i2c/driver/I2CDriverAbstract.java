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
package io.rhiot.component.deviceio.i2c.driver;

import java.io.IOException;
import java.nio.ByteBuffer;

import jdk.dio.ClosedDeviceException;
import jdk.dio.DeviceDescriptor;
import jdk.dio.UnavailableDeviceException;
import jdk.dio.i2cbus.I2CDevice;

public abstract class I2CDriverAbstract implements I2CDriver, I2CDevice {

    protected I2CDevice device;

    public I2CDriverAbstract(I2CDevice device) {
        this.device = device;
    }

    @Override
    public void init() throws Exception {
    }

    @Override
    public void start() throws Exception {
    }

    @Override
    public void stop() throws Exception {
    }

    @Override
    public void shutdown() throws Exception {
    }

    @Override
    public void suspend() throws Exception {
    }

    @Override
    public void resume() throws Exception {
    }

    @Override
    public final void begin() throws IOException, UnavailableDeviceException, ClosedDeviceException {
        device.begin();
    }

    @Override
    public final void close() throws IOException {
        device.close();
    }

    @Override
    public final void end() throws IOException, UnavailableDeviceException, ClosedDeviceException {
        device.end();
    }

    @Override
    public final Bus getBus() throws IOException {
        return device.getBus();
    }

    @Override
    public final <U extends I2CDevice> DeviceDescriptor<U> getDescriptor() {
        return device.getDescriptor();
    }

    @Override
    public final ByteBuffer getInputBuffer() throws ClosedDeviceException, IOException {
        return device.getInputBuffer();
    }

    @Override
    public final ByteBuffer getOutputBuffer() throws ClosedDeviceException, IOException {
        return device.getOutputBuffer();
    }

    @Override
    public final boolean isOpen() {
        return device.isOpen();
    }

    @Override
    public final int read() throws IOException, UnavailableDeviceException, ClosedDeviceException {
        return device.read();
    }

    @Override
    public final int read(ByteBuffer dst) throws IOException, UnavailableDeviceException, ClosedDeviceException {
        return device.read(dst);
    }

    @Override
    public final int read(int skip, ByteBuffer dst)
            throws IOException, UnavailableDeviceException, ClosedDeviceException {
        return device.read(skip, dst);
    }

    @Override
    public final int read(int subaddress, int subaddressSize, ByteBuffer dst)
            throws IOException, UnavailableDeviceException, ClosedDeviceException {
        return device.read(subaddress, subaddressSize, dst);
    }

    @Override
    public final int read(int subaddress, int subaddressSize, int skip, java.nio.ByteBuffer dst)
            throws IOException, UnavailableDeviceException, ClosedDeviceException {
        return device.read(subaddress, subaddressSize, skip, dst);
    }

    @Override
    public final void tryLock(int arg0) throws UnavailableDeviceException, ClosedDeviceException, IOException {
        device.tryLock(arg0);
    }

    @Override
    public final void unlock() throws IOException {
        device.unlock();
    }

    @Override
    public final int write(ByteBuffer arg0) throws IOException, UnavailableDeviceException, ClosedDeviceException {
        return device.write(arg0);
    }

    @Override
    public final int write(int arg0, int arg1, ByteBuffer arg2)
            throws IOException, UnavailableDeviceException, ClosedDeviceException {
        return device.write(arg0, arg1, arg2);
    }

    @Override
    public final void write(int arg0) throws IOException, UnavailableDeviceException, ClosedDeviceException {
        device.write(arg0);
    }

}
