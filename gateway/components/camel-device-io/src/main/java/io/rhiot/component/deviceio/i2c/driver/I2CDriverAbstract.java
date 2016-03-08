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

import io.rhiot.component.deviceio.DeviceIOConstants;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jdk.dio.ClosedDeviceException;
import jdk.dio.DeviceDescriptor;
import jdk.dio.DeviceManager;
import jdk.dio.UnavailableDeviceException;
import jdk.dio.i2cbus.I2CDevice;
import jdk.dio.i2cbus.I2CDeviceConfig;

public abstract class I2CDriverAbstract implements I2CDriver, I2CDevice {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    protected I2CDevice device;

    protected I2CDriverAbstract(int busId, int deviceAddr) {
        try {
            device = DeviceManager.open(new I2CDeviceConfig(busId, deviceAddr,
                    DeviceIOConstants.CAMEL_I2C_DIO_ADDRESS_SIZE_BITS, DeviceIOConstants.CAMEL_I2C_DIO_SERIAL_CLOCK));

        } catch (Exception e) {
            LOG.error("Cannot load device " + deviceAddr + " via bus" + busId, e);

        }
    }

    public I2CDriverAbstract(I2CDevice device) {
        this.device = device;
    }

    @Override
    public void checkDevice() throws Exception {

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
        this.close();
    }

    @Override
    public void suspend() throws Exception {
    }

    @Override
    public void resume() throws Exception {
    }

    public void sleep(long howMuch) {
        try {
            Thread.sleep(howMuch);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
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

    public final int read(int subaddress) throws IOException, UnavailableDeviceException, ClosedDeviceException {
        ByteBuffer dst = ByteBuffer.allocate(1);

        read(subaddress, DeviceIOConstants.CAMEL_I2C_DIO_SUBADDRESS_SIZE_BITS, dst);

        return dst.array()[0];
    }

    @Override
    public final int read(int subaddress, ByteBuffer dst)
            throws IOException, UnavailableDeviceException, ClosedDeviceException {
        return read(subaddress, DeviceIOConstants.CAMEL_I2C_DIO_SUBADDRESS_SIZE_BITS, dst);
    }

    @Override
    public final int read(int subaddress, int subaddressSize, ByteBuffer dst)
            throws IOException, UnavailableDeviceException, ClosedDeviceException {
        int ret = device.read(subaddress, subaddressSize, dst);
        dst.rewind();
        return ret;
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

    public final int write(int subaddress, byte byteToWrite)
            throws IOException, UnavailableDeviceException, ClosedDeviceException {
        ByteBuffer dst = ByteBuffer.allocate(1);
        dst.put(0, byteToWrite);
        return device.write(subaddress, DeviceIOConstants.CAMEL_I2C_DIO_SUBADDRESS_SIZE_BITS, dst);
    }

    @Override
    public final void write(int arg0) throws IOException, UnavailableDeviceException, ClosedDeviceException {
        device.write(arg0);
    }

    public short readU16BigEndian(int register) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(2);
        int bytesRead = read(register, 1, bb);
        if (bytesRead != 2) {
            throw new IOException("Could not read 2 bytes data");
        }
        bb.rewind();
        LOG.debug(String.format("0x%02X%02X", bb.array()[0], bb.array()[1]));
        return returnShort(bb.array()[1], bb.array()[0]);
    }

    public short readU16LittleEndian(int register) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(2);
        int bytesRead = read(register, 1, bb);
        if (bytesRead != 2) {
            throw new IOException("Could not read 2 bytes data");
        }
        bb.rewind();
        LOG.debug(String.format("0x%02X%02X", bb.array()[1], bb.array()[0]));
        return returnShort(bb.array()[0], bb.array()[1]);
    }

    public short returnShort(byte lsb, byte msb) {
        return (short) ((msb << DeviceIOConstants.CAMEL_I2C_DIO_BYTE_SHIFT) + lsb);
    }

    public int readU24BigEndian(int register) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(3);
        int bytesRead = read(register, 1, bb);
        if (bytesRead != 3) {
            throw new IOException("Could not read 3 bytes data");
        }
        bb.rewind();
        LOG.debug(String.format("0x%02X %02X %02X", bb.array()[2], bb.array()[1], bb.array()[0]));
        return returnInt(bb.array()[2], bb.array()[1], bb.array()[0]);
    }

    public int readU24LittleEndian(int register) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(3);
        int bytesRead = read(register, 1, bb);
        if (bytesRead != 3) {
            throw new IOException("Could not read 3 bytes data");
        }
        bb.rewind();
        return returnInt(bb.array()[0], bb.array()[1], bb.array()[2]);
    }

    public int returnInt(byte xlsb, byte lsb, byte msb) {
        return (msb << DeviceIOConstants.CAMEL_I2C_DIO_WORD_SHIFT) + (lsb << DeviceIOConstants.CAMEL_I2C_DIO_BYTE_SHIFT)
                + xlsb;
    }
}
