/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.rhiot.component.pi4j.i2c.driver;

import java.io.IOException;

import io.rhiot.component.pi4j.i2c.I2CConsumer;
import io.rhiot.component.pi4j.i2c.I2CEndpoint;
import com.pi4j.io.i2c.I2CDevice;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Thx @OlivierLD
 * More details about first implementation http://www.lediouris.net/RaspberryPI/readme.html
 * Code from https://github.com/OlivierLD/raspberry-pi4j-samples/blob/master/AdafruitI2C/src/adafruiti2c/sensor/AdafruitLSM303.java
 *
 * device doc : http://cdn.sparkfun.com/datasheets/Sensors/Magneto/DM00026454.pdf
 */
public class LSM303MagnetometerConsumer extends I2CConsumer {

    private static final transient Logger LOG = LoggerFactory.getLogger(LSM303MagnetometerConsumer.class);

    public final static int LSM303_ADDRESS_MAG = (0x3C >> 1); // 0011110x, 0x1E
                                                              // Default Type
    public final static int LSM303_REGISTER_MAG_CRB_REG_M = 0x01;
    public final static int LSM303_REGISTER_MAG_MR_REG_M = 0x02;
    public final static int LSM303_REGISTER_MAG_OUT_X_H_M = 0x03;

    // Gain settings for setMagGain()
    public final static int LSM303_MAGGAIN_1_3 = 0x20; // +/- 1.3
    public final static int LSM303_MAGGAIN_1_9 = 0x40; // +/- 1.9
    public final static int LSM303_MAGGAIN_2_5 = 0x60; // +/- 2.5
    public final static int LSM303_MAGGAIN_4_0 = 0x80; // +/- 4.0
    public final static int LSM303_MAGGAIN_4_7 = 0xA0; // +/- 4.7
    public final static int LSM303_MAGGAIN_5_6 = 0xC0; // +/- 5.6
    public final static int LSM303_MAGGAIN_8_1 = 0xE0; // +/- 8.1

    private int gain = LSM303_MAGGAIN_1_3;

    public LSM303MagnetometerConsumer(I2CEndpoint endpoint, Processor processor, I2CDevice device) {
        super(endpoint, processor, device);
    }

    @Override
    protected void createBody(Exchange exchange) throws IOException {
        LSM303Value body = readingSensors();

        LOG.debug("" + body);

        exchange.getIn().setBody(body);
    }

    protected void doStart() throws Exception {
        super.doStart();
        getDevice().write(LSM303_REGISTER_MAG_MR_REG_M, (byte) 0x00);
        getDevice().write(LSM303_REGISTER_MAG_CRB_REG_M, (byte) gain);
    }

    public int getGain() {
        return gain;
    }

    private int mag16(byte[] list, int idx) {
        int n = (list[idx] << 8) | list[idx + 1]; // High, low bytes
        return (n < 32768 ? n : n - 65536); // 2's complement signed
    }

    private LSM303Value readingSensors() throws IOException {
        LSM303Value ret = new LSM303Value();
        byte[] magData = new byte[6];

        // Reading magnetometer measurements.
        int r = getDevice().read(LSM303_REGISTER_MAG_OUT_X_H_M, magData, 0, 6);
        if (r != 6) {
            System.out.println("Error reading mag data, < 6 bytes");
        }

        ret.setX(mag16(magData, 0));
        ret.setY(mag16(magData, 2));
        ret.setZ(mag16(magData, 4));

        return ret;
    }

    public void setGain(int gain) {
        this.gain = gain;
    }

}
