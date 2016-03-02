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
import java.nio.ByteOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jdk.dio.i2cbus.I2CDevice;

/**
 * inspired by
 * https://github.com/richards-tech/RTIMULibCS/blob/master/RTIMULibCS/RTIMULibCS
 * /Devices/HTS221/HTS221HumiditySensor.cs and
 * https://github.com/richards-tech/RTIMULib/blob/master/RTIMULib/IMUDrivers/
 * RTHumidityHTS221.cpp
 */
public final class HTS221Driver extends I2CDriverAbstract implements HTS221Constants {

    private static final transient Logger LOG = LoggerFactory.getLogger(HTS221Driver.class);

    private ByteBuffer buffer = ByteBuffer.allocate(2);

    private double internalHumiditySlope;
    private double internalHumidityYIntercept;

    private double internalTemperatureSlope;
    private double internalTemperatureYIntercept;

    private HTS221TemperatureResolutionMode temperatureMode = HTS221TemperatureResolutionMode.AVGT_16;
    private HTS221HumidityResolutionMode humidityMode = HTS221HumidityResolutionMode.AVGH_32;
    private HTS221ControlRegistry1 bdu = HTS221ControlRegistry1.BDU_UPDATE_AFTER_READING;
    private HTS221ControlRegistry1 odr = HTS221ControlRegistry1.ODR_12DOT5_HZ;
    private HTS221ControlRegistry1 pd = HTS221ControlRegistry1.PD_ACTIVE;

    public HTS221Driver() {
        super(1, HTS221_ADDRESS);
    }

    public HTS221Driver(int busId, int deviceaddr) {
        super(busId, deviceaddr);
    }

    public HTS221Driver(I2CDevice device) {
        super(device);
    }

    @Override
    public void checkDevice() throws Exception {
        byte result = (byte) read(WHO_AM_I_ADDR);
        LOG.debug("WHO_I_AM : {}", toHexToString(result));
        if (result != (byte) WHO_AM_I) {
            throw new IOException("Could not read device identification");
        }

    }

    @Override
    public Object get() throws Exception {
        HTS221Value body = new HTS221Value();

        body.setHumidity(String.format("%+.2f%% rH", getHumidity()));
        body.setTemperature(String.format("%+.2f ºC", readTemperature()));

        LOG.debug("" + body);

        return body;
    }

    public static boolean available(byte status, byte exected) {
        return ((status & exected) == exected);
    }

    @Override
    public void start() throws Exception {
        super.start();
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        byte crtl1 = (byte) (odr.value | bdu.value << 2 | pd.value << 7);

        byte avconf = (byte) (humidityMode.average | temperatureMode.average << 3);
        LOG.debug("crtl1 : " + toHexToString(crtl1));
        LOG.debug("avconf : " + toHexToString(avconf));

        write(CTRL_REG1, crtl1);
        write(AV_CONF, avconf);

        temperatureCalibration();
        humidityCalibration();

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        byte crtl1 = (byte) read(CTRL_REG1);

        byte maskToPowerDown = (byte) (0xff ^ (~HTS221ControlRegistry1.PD_POWER_DOWN.value << 7));
        crtl1 &= maskToPowerDown;

        write(CTRL_REG1, crtl1);

    }

    public double getHumidity() throws IOException {

        read(HUMIDITY_OUT_L | MULTI_BYTE_READ_MASK, buffer);
        short rawHumidity = buffer.getShort(0);

        return (rawHumidity * internalHumiditySlope + internalHumidityYIntercept);
    }

    public double readTemperature() throws IOException {
        read(TEMP_OUT_L | MULTI_BYTE_READ_MASK, buffer);
        short TEMP_OUT_L = buffer.getShort(0);
        LOG.debug("TEMP_OUT_L " + TEMP_OUT_L);

        return (TEMP_OUT_L * internalTemperatureSlope + internalTemperatureYIntercept);
    }

    public void humidityCalibration() throws Exception {
        read(H0_rH_x2 | MULTI_BYTE_READ_MASK, buffer);
        buffer.put(1, (byte) 0);
        short H0_H_2 = buffer.getShort(0);
        LOG.debug("H0_H_2 " + H0_H_2);

        double H0 = H0_H_2 / 2.0;
        LOG.debug("H0 " + H0);

        read(H1_rH_x2 | MULTI_BYTE_READ_MASK, buffer);
        buffer.put(1, (byte) 0);
        short H1_H_2 = buffer.getShort(0);
        LOG.debug("H1_H_2 " + H1_H_2);

        double H1 = H1_H_2 / 2.0;
        LOG.debug("H1 " + H1);

        read(H0_T0_OUT | MULTI_BYTE_READ_MASK, buffer);
        short H0_T0_OUT = buffer.getShort(0);
        LOG.debug("H0_T0_OUT: " + toHexToString(H0_T0_OUT));

        read(H1_T0_OUT | MULTI_BYTE_READ_MASK, buffer);
        short H1_T0_OUT = buffer.getShort(0);
        LOG.debug("H1_T0_OUT: " + toHexToString(H1_T0_OUT));

        internalHumiditySlope = (H1 - H0) / (H1_T0_OUT - H0_T0_OUT);
        internalHumidityYIntercept = H0 - (internalHumiditySlope * H0_T0_OUT);
    }

    public void temperatureCalibration() throws Exception {
        byte tempMSB = (byte) (0x0f & read(T1_T0_msb));
        LOG.debug("T1/T0 msb:" + toHexToString(tempMSB));

        // retrieve T0 / (Msb T0_degC U T0_degC)_x8
        byte temp0LSB = (byte) (0xff & read(T0_degC_x8));
        buffer.put(0, temp0LSB);
        buffer.put(1, (byte) (tempMSB & 0x03));
        short T0_C_8 = buffer.getShort(0);
        LOG.debug("T0_C_8:" + toHexToString(T0_C_8));
        double T0 = T0_C_8 / 8.0;
        LOG.debug("T0 " + T0);

        // retrieve T1 / (Msb T1_degC U T1_degC)_x8
        byte temp1LSB = (byte) (read(T1_degC_x8));
        buffer.put(0, temp1LSB);
        buffer.put(1, (byte) ((byte) (tempMSB & 0x0C) >> 2));
        short T1_C_8 = buffer.getShort(0);
        LOG.debug("T1_C_8:" + toHexToString(T1_C_8));
        double T1 = T1_C_8 / 8.0;
        LOG.debug("T1 " + T1);

        // retrieve T0_OUT
        read(T0_OUT | MULTI_BYTE_READ_MASK, buffer);
        short T0_OUT = buffer.getShort(0);
        LOG.debug("T0_OUT " + toHexToString(T0_OUT));

        // retrieve T1_OUT
        read(T1_OUT | MULTI_BYTE_READ_MASK, buffer);
        short T1_OUT = buffer.getShort(0);
        LOG.debug("T1_OUT " + toHexToString(T1_OUT));

        // Temperature calibration slope
        internalTemperatureSlope = ((T1 - T0) / (T1_OUT - T0_OUT));

        // Temperature calibration y intercept
        internalTemperatureYIntercept = (T0 - (internalTemperatureSlope * T0_OUT));
    }

    public static String toHexToString(byte i) {
        return String.format("0b%8s", Integer.toBinaryString(0x000000ff & i)).replace(' ', '0');
    }

    public static String toHexToString(short i) {
        return String.format("0b%16s", Integer.toBinaryString(0x0000ffff & i)).replace(' ', '0');
    }

    public HTS221TemperatureResolutionMode getTemperatureMode() {
        return temperatureMode;
    }

    public void setTemperatureMode(HTS221TemperatureResolutionMode temperatureMode) {
        this.temperatureMode = temperatureMode;
    }

    public HTS221HumidityResolutionMode getHumidityMode() {
        return humidityMode;
    }

    public void setHumidityMode(HTS221HumidityResolutionMode humidityMode) {
        this.humidityMode = humidityMode;
    }

    public HTS221ControlRegistry1 getBdu() {
        return bdu;
    }

    public void setBdu(HTS221ControlRegistry1 bdu) {
        this.bdu = bdu;
    }

    public HTS221ControlRegistry1 getOdr() {
        return odr;
    }

    public void setOdr(HTS221ControlRegistry1 odr) {
        this.odr = odr;
    }

    public HTS221ControlRegistry1 getPd() {
        return pd;
    }

    public void setPd(HTS221ControlRegistry1 pd) {
        this.pd = pd;
    }

    @Override
    public Class getType() throws Exception {
        // TODO Auto-generated method stub
        return HTS221Value.class;
    }
}
