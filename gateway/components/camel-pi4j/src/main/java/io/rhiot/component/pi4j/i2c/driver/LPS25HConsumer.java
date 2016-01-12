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

import io.rhiot.component.pi4j.i2c.I2CConstants;
import io.rhiot.component.pi4j.i2c.I2CConsumer;
import io.rhiot.component.pi4j.i2c.I2CEndpoint;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.i2c.I2CDevice;

public class LPS25HConsumer extends I2CConsumer {

    private static final transient Logger LOG = LoggerFactory.getLogger(LPS25HConsumer.class);

    public static final byte LPS25H_ADDRESS = 0x5c;

    public static final byte REF_P_XL = 0x08;
    public static final byte REF_P_L = 0x09;
    public static final byte REF_P_H = 0x0A;
    public static final byte WHO_AM_I = 0x0F;
    public static final byte RES_CONF = 0x10;
    // Reserved (Do not modify) 11-1F Reserved
    public static final byte CTRL_REG1 = 0x20;
    public static final byte CTRL_REG2 = 0x21;
    public static final byte CTRL_REG3 = 0x22;
    public static final byte CTRL_REG4 = 0x23;
    public static final byte INT_CFG = 0x24;
    public static final byte INT_SOURCE = 0x25;
    // Reserved (Do not modify) 26 Reserved
    public static final byte STATUS_REG = 0x27;
    public static final byte PRESS_POUT_XL = 0x28;
    public static final byte PRESS_OUT_L = 0x29;
    public static final byte PRESS_OUT_H = 0x2A;
    public static final byte TEMP_OUT_L = 0x2B;
    public static final byte TEMP_OUT_H = 0x2C;
    // Reserved (Do not modify) 2D Reserved
    public static final byte FIFO_CTRL = 0x2E;
    public static final byte FIFO_STATUS = 0x2F;
    public static final byte THS_P_L = 0x30;
    public static final byte THS_P_H = 0x31;
    // public static final byte Reserved 32-38
    public static final byte RPDS_L = 0x39;
    public static final byte RPDS_H = 0x3A;

    // ST suggested Configuration
    private static final byte RES_CONF_SC = 0x05;
    private static final byte CTRL_REG2_SC = 0x40;
    private static final byte FIFO_CTRL_SC = (byte) 0xc0;

    // Static constant for conversion
    private static final double DOUBLE_42_5 = 42.5;
    private static final double DOUBLE_480_0 = 480.0;
    private static final double DOUBLE_4096_0 = 4096.0;

    private ByteBuffer buffer = ByteBuffer.allocate(4);

    public static byte TEMP_DATA_AVAILABLE_MASK = 0x01;
    public static byte PRESS_DATA_AVAILABLE_MASK = 0x02;

    private LPS25HControlRegistry1 pd = LPS25HControlRegistry1.PD_ACTIVE;
    private LPS25HControlRegistry1 odr = LPS25HControlRegistry1.ODR_25_HZ;
    private LPS25HControlRegistry1 diffEn = LPS25HControlRegistry1.DIFFEN_DISABLED;
    private LPS25HControlRegistry1 bdu = LPS25HControlRegistry1.BDU_UPDATE_AFTER_READING;
    private LPS25HControlRegistry1 resetAz = LPS25HControlRegistry1.RESETAZ_DISABLE;
    private LPS25HControlRegistry1 sim = LPS25HControlRegistry1.SIM_4WIRE;

    public LPS25HConsumer(I2CEndpoint endpoint, Processor processor, I2CDevice device) {
        super(endpoint, processor, device);
        buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    @Override
    protected void createBody(Exchange exchange) throws IOException {
        LPS25HValue body = new LPS25HValue();
        body.setPressure(readPressure());
        body.setTemperature(readTemperature());
        LOG.debug("" + body);

        exchange.getIn().setBody(body);
    }

    public boolean available(byte status, byte exected) {
        return ((status & exected) == exected);
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        byte registry1 = (byte) (pd.value << 7 | odr.value << 4 | diffEn.value << 3 | bdu.value << 2
                | resetAz.value << 1 | sim.value);

        write(CTRL_REG1, registry1);
        // ST suggested configuration
        write(RES_CONF, RES_CONF_SC);
        write(FIFO_CTRL, FIFO_CTRL_SC);
        write(CTRL_REG2, CTRL_REG2_SC);
    }

    @Override
    public void doStop() throws Exception {
        super.doStop();
        byte crtl1 = (byte) read(CTRL_REG1);

        byte maskToPowerDown = (byte) (0xff ^ (~LPS25HControlRegistry1.PD_POWER_DOWN.value << 7));
        crtl1 &= maskToPowerDown;

        write(CTRL_REG1, crtl1);

    }

    private double readPressure() throws IOException {
        read(PRESS_POUT_XL | I2CConstants.MULTI_BYTE_READ_MASK, buffer.array(), 0, 3);
        buffer.put(3, (byte) 0);
        int PRESS_OUT = buffer.getInt(0);
        LOG.debug("PRESS_OUT  : " + PRESS_OUT);

        double press = (PRESS_OUT / DOUBLE_4096_0);
        LOG.debug("PRESS_OUT (hPa) : " + press);
        return press;

    }

    private double readTemperature() throws IOException {
        read(TEMP_OUT_L | I2CConstants.MULTI_BYTE_READ_MASK, buffer.array(), 0, 2);
        short TEMP_OUT = buffer.getShort(0);
        LOG.debug("TEMP_OUT  : " + TEMP_OUT);

        double temp = (DOUBLE_42_5 + (TEMP_OUT / DOUBLE_480_0));
        LOG.debug("TEMP_OUT (C) : " + temp);
        return temp;
    }

    public LPS25HControlRegistry1 getPd() {
        return pd;
    }

    public void setPd(LPS25HControlRegistry1 pd) {
        this.pd = pd;
    }

    public LPS25HControlRegistry1 getOdr() {
        return odr;
    }

    public void setOdr(LPS25HControlRegistry1 odr) {
        this.odr = odr;
    }

    public LPS25HControlRegistry1 getDiffEn() {
        return diffEn;
    }

    public void setDiffEn(LPS25HControlRegistry1 diffEn) {
        this.diffEn = diffEn;
    }

    public LPS25HControlRegistry1 getBdu() {
        return bdu;
    }

    public void setBdu(LPS25HControlRegistry1 bdu) {
        this.bdu = bdu;
    }

    public LPS25HControlRegistry1 getResetAz() {
        return resetAz;
    }

    public void setResetAz(LPS25HControlRegistry1 resetAz) {
        this.resetAz = resetAz;
    }

    public LPS25HControlRegistry1 getSim() {
        return sim;
    }

    public void setSim(LPS25HControlRegistry1 sim) {
        this.sim = sim;
    }
}
