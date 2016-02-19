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

import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.i2c.I2CDevice;

/*
 * Light Sensor (I2C)
 * 
 * Thx OlivierLD
 * More details about first implementation http://www.lediouris.net/RaspberryPI/readme.html
 * Code from https://github.com/OlivierLD/raspberry-pi4j-samples/blob/master/AdafruitI2C/src/adafruiti2c/sensor/AdafruitTSL2561.java
 */
public class TSL2561Consumer extends I2CConsumer {

    private static final transient Logger LOG = LoggerFactory.getLogger(TSL2561Consumer.class);

    public final static int TSL2561_ADDRESS = 0x39;

    public final static int TSL2561_ADDRESS_LOW = 0x29;
    public final static int TSL2561_ADDRESS_FLOAT = 0x39;
    public final static int TSL2561_ADDRESS_HIGH = 0x49;

    public final static int TSL2561_COMMAND_BIT = 0x80;
    public final static int TSL2561_WORD_BIT = 0x20;
    public final static int TSL2561_CONTROL_POWERON = 0x03;
    public final static int TSL2561_CONTROL_POWEROFF = 0x00;

    public final static int TSL2561_REGISTER_CONTROL = 0x00;
    public final static int TSL2561_REGISTER_TIMING = 0x01;
    public final static int TSL2561_REGISTER_CHAN0_LOW = 0x0C;
    public final static int TSL2561_REGISTER_CHAN0_HIGH = 0x0D;
    public final static int TSL2561_REGISTER_CHAN1_LOW = 0x0E;
    public final static int TSL2561_REGISTER_CHAN1_HIGH = 0x0F;
    public final static int TSL2561_REGISTER_ID = 0x0A;

    public final static double TSL2561_LUX_K1C = 0.130; // (0x0043) // 0.130 *
                                                        // 2^RATIO_SCALE
    public final static double TSL2561_LUX_B1C = 0.0315; // (0x0204) // 0.0315 *
                                                         // 2^LUX_SCALE
    public final static double TSL2561_LUX_M1C = 0.0262; // (0x01ad) // 0.0262 *
                                                         // 2^LUX_SCALE
    public final static double TSL2561_LUX_K2C = 0.260; // (0x0085) // 0.260 *
                                                        // 2^RATIO_SCALE
    public final static double TSL2561_LUX_B2C = 0.0337; // (0x0228) // 0.0337 *
                                                         // 2^LUX_SCALE
    public final static double TSL2561_LUX_M2C = 0.0430; // (0x02c1) // 0.0430 *
                                                         // 2^LUX_SCALE
    public final static double TSL2561_LUX_K3C = 0.390; // (0x00c8) // 0.390 *
                                                        // 2^RATIO_SCALE
    public final static double TSL2561_LUX_B3C = 0.0363; // (0x0253) // 0.0363 *
                                                         // 2^LUX_SCALE
    public final static double TSL2561_LUX_M3C = 0.0529; // (0x0363) // 0.0529 *
                                                         // 2^LUX_SCALE
    public final static double TSL2561_LUX_K4C = 0.520; // (0x010a) // 0.520 *
                                                        // 2^RATIO_SCALE
    public final static double TSL2561_LUX_B4C = 0.0392; // (0x0282) // 0.0392 *
                                                         // 2^LUX_SCALE
    public final static double TSL2561_LUX_M4C = 0.0605; // (0x03df) // 0.0605 *
                                                         // 2^LUX_SCALE
    public final static double TSL2561_LUX_K5C = 0.65; // (0x014d) // 0.65 *
                                                       // 2^RATIO_SCALE
    public final static double TSL2561_LUX_B5C = 0.0229; // (0x0177) // 0.0229 *
                                                         // 2^LUX_SCALE
    public final static double TSL2561_LUX_M5C = 0.0291; // (0x01dd) // 0.0291 *
                                                         // 2^LUX_SCALE
    public final static double TSL2561_LUX_K6C = 0.80; // (0x019a) // 0.80 *
                                                       // 2^RATIO_SCALE
    public final static double TSL2561_LUX_B6C = 0.0157; // (0x0101) // 0.0157 *
                                                         // 2^LUX_SCALE
    public final static double TSL2561_LUX_M6C = 0.0180; // (0x0127) // 0.0180 *
                                                         // 2^LUX_SCALE
    public final static double TSL2561_LUX_K7C = 1.3; // (0x029a) // 1.3 *
                                                      // 2^RATIO_SCALE
    public final static double TSL2561_LUX_B7C = 0.00338; // (0x0037) // 0.00338
                                                          // * 2^LUX_SCALE
    public final static double TSL2561_LUX_M7C = 0.00260; // (0x002b) // 0.00260
                                                          // * 2^LUX_SCALE
    public final static double TSL2561_LUX_K8C = 1.3; // (0x029a) // 1.3 *
                                                      // 2^RATIO_SCALE
    public final static double TSL2561_LUX_B8C = 0.000; // (0x0000) // 0.000 *
                                                        // 2^LUX_SCALE
    public final static double TSL2561_LUX_M8C = 0.000; // (0x0000) // 0.000 *
                                                        // 2^LUX_SCALE

    // Default value
    private TSL2561Gain gain = TSL2561Gain.GAIN_1X;
    private TSL2561IntegrationTime integration = TSL2561IntegrationTime.INTEGRATIONTIME_402MS;
    private long pause = 800L;

    public TSL2561Consumer(I2CEndpoint endpoint, Processor processor, I2CDevice device) {
        super(endpoint, processor, device);
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        write(TSL2561_COMMAND_BIT, (byte) TSL2561_CONTROL_POWERON);
        write(TSL2561_COMMAND_BIT | TSL2561_REGISTER_TIMING, (byte) (gain.gain | integration.integrationTime));
        sleep(pause);
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        write(TSL2561_COMMAND_BIT, (byte) TSL2561_CONTROL_POWEROFF);
    }

    @Override
    protected void createBody(Exchange exchange) throws IOException {
        TSL2561Value body = new TSL2561Value();
        body.setLux(readLux());

        LOG.debug("" + body);

        exchange.getIn().setBody(body);
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
