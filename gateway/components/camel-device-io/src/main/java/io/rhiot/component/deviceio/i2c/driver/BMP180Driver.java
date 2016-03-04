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

import jdk.dio.i2cbus.I2CDevice;

public class BMP180Driver extends I2CDriverAbstract implements BMP180Constants {

    private static final transient Logger LOG = LoggerFactory.getLogger(BMP180Driver.class);

    // Calibration variables
    private short AC1;
    private short AC2;
    private short AC3;
    private int AC4;
    private int AC5;
    private int AC6;
    private short B1;
    private short B2;
    private short MB;
    private short MC;
    private short MD;

    private BMP180OperatingMode mode = BMP180OperatingMode.STANDARD;

    public BMP180Driver() {
        super(BMP085_BUSID, BMP085_DEVICEADDR);
    }

    public BMP180Driver(int busId, int deviceaddr) {
        super(busId, deviceaddr);
    }

    public BMP180Driver(I2CDevice device) {
        super(device);
    }

    public BMP180OperatingMode getMode() {
        return mode;
    }

    public void setMode(BMP180OperatingMode mode) {
        this.mode = mode;
    }

    @Override
    public void checkDevice() throws Exception {
        ByteBuffer bb = ByteBuffer.allocate(1);
        int bytesRead = read(BMP085_DEVICE_ID_ADDR, DeviceIOConstants.CAMEL_I2C_DIO_SUBADDRESS_SIZE_BITS, bb);
        LOG.debug("bytesRead=" + bytesRead);
        if (bb.get(0) != BMP085_DEVICE_ID) {
            throw new IOException("Could not read device identification");
        }

    }

    @Override
    public void start() throws Exception {
        checkDevice();
        int totalBytes = BMP085_CALIBRATION_END - BMP085_CALIBRATION_START + 1;
        ByteBuffer bb = ByteBuffer.allocate(totalBytes);
        int bytesRead = read(BMP085_CALIBRATION_START, bb);
        LOG.debug("bytesRead=" + bytesRead);
        if (bytesRead != totalBytes) {
            throw new IOException("Could not read calibration data");
        }

        AC1 = bb.getShort(0);
        AC2 = bb.getShort(2);
        AC3 = bb.getShort(4);
        AC4 = bb.getShort(6);
        AC4 = (AC4 < 0) ? (AC4 + Short.MAX_VALUE * 2) : AC4;
        AC5 = bb.getShort(8);
        AC5 = (AC5 < 0) ? (AC5 + Short.MAX_VALUE * 2) : AC5;
        AC6 = bb.getShort(10);
        AC6 = (AC6 < 0) ? (AC6 + Short.MAX_VALUE * 2) : AC6;
        B1 = bb.getShort(12);
        B2 = bb.getShort(14);
        MB = bb.getShort(16);
        MC = bb.getShort(18);
        MD = bb.getShort(20);

        LOG.debug(String.format("AC1:%d, AC2:%d, AC3:%d, AC4:%d, AC5:%d, AC6:%d, B1:%d, B2:%d,  MB:%d, MC:%d, MD:%d",
                AC1, AC2, AC3, AC4, AC5, AC6, B1, B2, MB, MC, MD));
    }

    @Override
    public void stop() {
        try {
            device.close();
        } catch (IOException e) {
            LOG.error("Cannot close device");
        }
    }

    public int readPressure() throws IOException {
        long p = 0;
        int UT = readRawTemperature();
        int UP = readRawPressure();

        int X1 = ((UT - AC6) * AC5) >> 15;
        int X2 = (MC << 11) / (X1 + MD);
        int B5 = X1 + X2;

        LOG.debug("X1 = " + X1);
        LOG.debug("X2 = " + X2);
        LOG.debug("B5 = " + B5);

        int B6 = B5 - 4000;
        X1 = (B2 * ((B6 * B6) >> 12)) >> 11;
        X2 = (AC2 * B6) >> 11;
        int X3 = X1 + X2;
        int B3 = (((AC1 * 4 + X3) << mode.getOverSamplingSetting()) + 2) / 4;

        LOG.debug("B6 = " + B6);
        LOG.debug("X1 = " + X1);
        LOG.debug("X2 = " + X2);
        LOG.debug("X3 = " + X3);
        LOG.debug("B3 = " + B3);

        X1 = (AC3 * B6) >> 13;
        X2 = (B1 * ((B6 * B6) >> 12)) >> 16;
        X3 = ((X1 + X2) + 2) >> 2;
        long B4 = (AC4 * ((long) (X3 + 32768))) >> 15;
        long B7 = ((long) UP - B3) * (50000 >> mode.getOverSamplingSetting());

        LOG.debug("X1 = " + X1);
        LOG.debug("X2 = " + X2);
        LOG.debug("X3 = " + X3);
        LOG.debug("B4 = " + B4);
        LOG.debug("B7 = " + B7);

        if (B7 < 0x80000000) {
            p = (B7 * 2) / B4;
        } else {
            p = (B7 / B4) * 2;
        }

        X1 = (int) ((p >> 8) * (p >> 8));
        X1 = (X1 * 3038) >> 16;
        X2 = (int) (-7357 * p) >> 16;
        p = p + ((X1 + X2 + 3791) >> 4);
        LOG.debug("X1 = " + X1);
        LOG.debug("X2 = " + X2);
        LOG.debug("p = " + p);
        return (int) p;
    }

    @Override
    public Object get() throws Exception {
        BMP180Value ret = new BMP180Value();
        ret.setPressure(String.format("%.2f hPa", (double) readPressure() / 100));
        ret.setTemperature(String.format("%+.2f ºC", readTemperature()));
        return ret;
    }

    @Override
    public Class getType() throws Exception {
        return BMP180Value.class;
    }

    private int readRawPressure() throws IOException {
        ByteBuffer bb = ByteBuffer.allocateDirect(1);
        bb.put(0, (byte) mode.getPressureControlCommand());
        int bytesWrote = write(BMP085_CONTROL, 1, bb);
        if (bytesWrote != 1) {
            throw new IOException("Could not write 1 bytes data");
        }
        sleep(mode.getWaitTime());
        return readU24BigEndian(BMP085_ADCDATA) >> (8 - mode.getOverSamplingSetting());
    }

    private int readRawTemperature() throws IOException {
        ByteBuffer bb = ByteBuffer.allocateDirect(1);
        bb.put(0, BMP085_READTEMPCMD);
        int bytesWrote = write(BMP085_CONTROL, 1, bb);
        if (bytesWrote != 1) {
            throw new IOException("Could not write 1 bytes data");
        }
        sleep(50);
        return readU16BigEndian(BMP085_ADCDATA);
    }

    private float readTemperature() throws IOException {
        int UT = readRawTemperature();
        int X1 = ((UT - AC6) * AC5) >> 15;
        int X2 = (MC << 11) / (X1 + MD);
        int B5 = X1 + X2;
        LOG.debug("UT = " + UT);
        LOG.debug("X1 = " + X1);
        LOG.debug("X2 = " + X2);
        LOG.debug("B5 = " + B5);
        return ((B5 + 8) >> 4) / 10.0f;
    }
}
