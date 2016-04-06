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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jdk.dio.i2cbus.I2CDevice;

/*
 * Light Sensor (I2C)
 * 
 * Thx OlivierLD
 * More details about first implementation http://www.lediouris.net/RaspberryPI/readme.html
 * Code from https://github.com/OlivierLD/raspberry-pi4j-samples/blob/master/AdafruitI2C/src/adafruiti2c/sensor/AdafruitTSL2561.java
 */
public class TSL2561Driver extends I2CDriverAbstract implements TSL2561Constants {

    private static final transient Logger LOG = LoggerFactory.getLogger(TSL2561Driver.class);

    // Default value
    private TSL2561Gain gain = TSL2561Gain.GAIN_1X;
    private TSL2561IntegrationTime integration = TSL2561IntegrationTime.INTEGRATIONTIME_402MS;
    private long pause = 800L;

    public TSL2561Driver() {
        super(TSL2561_BUSID, TSL2561_DEVICEADDR);
    }

    public TSL2561Driver(int busId, int deviceaddr) {
        super(busId, deviceaddr);
    }

    public TSL2561Driver(I2CDevice device) {
        super(device);
    }

    @Override
    public void start() throws Exception {
        super.start();
        write(TSL2561_COMMAND_BIT, (byte) TSL2561_CONTROL_POWERON);
        write(TSL2561_COMMAND_BIT | TSL2561_REGISTER_TIMING, (byte) (gain.gain | integration.integrationTime));
        sleep(pause);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        write(TSL2561_COMMAND_BIT, (byte) TSL2561_CONTROL_POWEROFF);
    }

    @Override
    public Object get() throws Exception {
        TSL2561Value ret = new TSL2561Value();
        ret.setLux(readLux());
        LOG.debug("" + ret);
        return ret;
    }

    @Override
    public Class getType() throws Exception {
        // TODO Auto-generated method stub
        return TSL2561Value.class;
    }

    /*
     * Device lux range 0.1 - 40,000+ see
     * https://learn.adafruit.com/tsl2561/overview
     */
    public double readLux() throws IOException {
        int ambient = readU16LittleEndian(TSL2561_COMMAND_BIT | TSL2561_REGISTER_CHAN0_LOW);
        int ir = readU16LittleEndian(TSL2561_COMMAND_BIT | TSL2561_REGISTER_CHAN1_LOW);

        if (ambient >= 0xffff || ir >= 0xffff) // value(s) exeed(s) datarange
            throw new RuntimeException("Gain too high. Values exceed range.");

        if (false && this.gain == TSL2561Gain.GAIN_1X) {
            ambient *= 16; // scale 1x to 16x
            ir *= 16; // scale 1x to 16x
        }
        double ratio = (ir / (float) ambient);

        LOG.debug("IR Result:" + ir);
        LOG.debug("Ambient Result:" + ambient);

        /*
         * For the values below, see
         * https://github.com/adafruit/Adafruit_TSL2561
         * /blob/master/Adafruit_TSL2561_U.h
         */
        double lux = 0d;
        if ((ratio >= 0) && (ratio <= TSL2561_LUX_K4C))
            lux = (TSL2561_LUX_B1C * ambient) - (0.0593 * ambient * (Math.pow(ratio, 1.4)));
        else if (ratio <= TSL2561_LUX_K5C)
            lux = (TSL2561_LUX_B5C * ambient) - (TSL2561_LUX_M5C * ir);
        else if (ratio <= TSL2561_LUX_K6C)
            lux = (TSL2561_LUX_B6C * ambient) - (TSL2561_LUX_M6C * ir);
        else if (ratio <= TSL2561_LUX_K7C)
            lux = (TSL2561_LUX_B7C * ambient) - (TSL2561_LUX_M7C * ir);
        else if (ratio > TSL2561_LUX_K8C)
            lux = 0;

        return lux;
    }

    public TSL2561Gain getGain() {
        return gain;
    }

    public void setGain(TSL2561Gain gain) {
        this.gain = gain;
    }

    public TSL2561IntegrationTime getIntegration() {
        return integration;
    }

    public void setIntegration(TSL2561IntegrationTime integration) {
        this.integration = integration;
    }

    public long getPause() {
        return pause;
    }

    public void setPause(long pause) {
        this.pause = pause;
    }

}
