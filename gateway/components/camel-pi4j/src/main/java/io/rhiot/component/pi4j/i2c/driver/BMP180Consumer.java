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
package io.rhiot.component.pi4j.i2c.driver;

import io.rhiot.component.pi4j.i2c.I2CConsumer;
import io.rhiot.component.pi4j.i2c.I2CEndpoint;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.i2c.I2CDevice;

/**
 * Code from Marcus Hirt, 2015, Code From http://hirt.se/blog/?p=652
 */
public final class BMP180Consumer extends I2CConsumer {

    private static final transient Logger LOG = LoggerFactory.getLogger(BMP180Consumer.class);

    // Calibration data
    private static final int CALIBRATION_START = 0xAA;

    private static final int CALIBRATION_END = 0xBF;
    private static final short BMP085_CONTROL = 0xF4;
    private static final short BMP085_TEMPDATA = 0xF6;
    private static final short BMP085_PRESSUREDATA = 0xF6;
    private static final byte BMP085_READTEMPCMD = 0x2E;

    private final static byte BMP085_READPRESSURECMD = 0x34;

    private BMP180OperatingMode mode = BMP180OperatingMode.STANDARD;

    // Calibration variables
    private short AC1;

    private short AC2;
    private short AC3;
    private int AC4;
    private int AC5;
    private int AC6;
    private short B1;
    private short B2;
    private short MC;
    private short MD;

    public BMP180Consumer(I2CEndpoint endpoint, Processor processor, I2CDevice device) {
        super(endpoint, processor, device);
    }

    @Override
    protected void createBody(Exchange exchange) throws IOException {
        BMP180Value body = new BMP180Value();
        body.setPressure(readPressure());
        body.setTemperature(readTemperature());

        LOG.debug("" + body);

        exchange.getIn().setBody(body);
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        int totalBytes = CALIBRATION_END - CALIBRATION_START + 1;
        byte[] bytes = new byte[totalBytes];
        int bytesRead = read(CALIBRATION_START, bytes, 0, totalBytes);
        if (bytesRead != totalBytes) {
            throw new IOException("Could not read calibration data. Read " + bytes + " of " + totalBytes);
        }

        DataInputStream calibrationData = new DataInputStream(new ByteArrayInputStream(bytes));
        AC1 = calibrationData.readShort();
        AC2 = calibrationData.readShort();
        AC3 = calibrationData.readShort();
        AC4 = calibrationData.readUnsignedShort();
        AC5 = calibrationData.readUnsignedShort();
        AC6 = calibrationData.readUnsignedShort();
        B1 = calibrationData.readShort();
        B2 = calibrationData.readShort();
        calibrationData.readShort(); // MB not used for anything it seems...
        MC = calibrationData.readShort();
        MD = calibrationData.readShort();

        LOG.debug(String.format("AC1:%d, AC2:%d, AC3:%d, AC4:%d, AC5:%d, AC6:%d, B1:%d, B2:%d, MC:%d, MD:%d", AC1, AC2,
                AC3, AC4, AC5, AC6, B1, B2, MC, MD));
    }

    public BMP180OperatingMode getMode() {
        return mode;
    }

    public int readPressure() throws IOException {
        long p = 0;
        int UT = readRawTemp();
        int UP = readRawPressure();

        int X1 = ((UT - AC6) * AC5) >> 15;
        int X2 = (MC << 11) / (X1 + MD);
        int B5 = X1 + X2;

        int B6 = B5 - 4000;
        X1 = (B2 * ((B6 * B6) >> 12)) >> 11;
        X2 = (AC2 * B6) >> 11;
        int X3 = X1 + X2;
        int B3 = (((AC1 * 4 + X3) << mode.getOverSamplingSetting()) + 2) / 4;

        X1 = (AC3 * B6) >> 13;
        X2 = (B1 * ((B6 * B6) >> 12)) >> 16;
        X3 = ((X1 + X2) + 2) >> 2;
        long B4 = (AC4 * ((long) (X3 + 32768))) >> 15;
        long B7 = ((long) UP - B3) * (50000 >> mode.getOverSamplingSetting());

        if (B7 < 0x80000000) {
            p = (B7 * 2) / B4;
        } else {
            p = (B7 / B4) * 2;
        }

        X1 = (int) ((p >> 8) * (p >> 8));
        X1 = (X1 * 3038) >> 16;
        X2 = (int) (-7357 * p) >> 16;
        p = p + ((X1 + X2 + 3791) >> 4);
        return (int) p;
    }

    private int readRawPressure() throws IOException {
        write(BMP085_CONTROL, BMP085_READPRESSURECMD);
        sleep(mode.getWaitTime());
        return readU3(BMP085_PRESSUREDATA) >> (8 - mode.getOverSamplingSetting());
    }

    private int readRawTemp() throws IOException {
        write(BMP085_CONTROL, BMP085_READTEMPCMD);
        sleep(50);
        return readU16BigEndian(BMP085_TEMPDATA);
    }

    private float readTemperature() throws IOException {
        int UT = readRawTemp();
        int X1 = ((UT - AC6) * AC5) >> 15;
        int X2 = (MC << 11) / (X1 + MD);
        int B5 = X1 + X2;
        return ((B5 + 8) >> 4) / 10.0f;
    }

    private int readU3(int address) throws IOException {
        // TODO: Check if there is any potential performance benefit to reading
        // them all at once into a byte array. It's probably translated to
        // to consecutive byte reads anyways, so probably not.
        int msb = read(address);
        int lsb = read(address + 1);
        int xlsb = read(address + 2);
        return (msb << 16) + (lsb << 8) + xlsb;
    }

    public void setMode(BMP180OperatingMode mode) {
        this.mode = mode;
    }
}
