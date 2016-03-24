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
package io.rhiot.component.deviceio.i2c.util;

import java.io.IOException;

import jdk.dio.DeviceManager;
import jdk.dio.DeviceNotFoundException;
import jdk.dio.i2cbus.I2CDevice;
import jdk.dio.i2cbus.I2CDeviceConfig;

/**
 * i2cdetect Java Implementation
 */
public class I2CDetect {

    enum State {
        FOUND, NOTFOUND, ERROR, INVALIDRANGE;
    }

    public static final int I2C_DIO_SERIAL_CLOCK = 3400000;

    public static void main(String[] args) {

        int busId = 1;

        if (args.length > 0) {
            busId = Integer.parseInt(args[args.length - 1]);
        }

        System.out.print("   ");
        for (int i = 0; i <= 0xf; i++) {
            System.out.printf("  %x", i);
        }
        System.out.println("");

        for (int j = 0; j < 0x80; j = j + 0x10) {
            System.out.printf("%02x:", j);
            for (int i = 0; i <= 0xf; i++) {

                switch (foundDevice(busId, i + j)) {
                case FOUND:
                    System.out.printf(" %02x", j + i);
                    break;
                case NOTFOUND:
                    System.out.printf(" --", j + i);
                    break;
                case ERROR:
                    System.out.printf(" UU", j + i);
                    break;
                case INVALIDRANGE:
                    System.out.printf("   ", j + i);
                    break;
                }

            }
            System.out.println("");
        }
    }

    public static State foundDevice(int busId, int deviceAddr) {
        I2CDevice device = null;
        if (deviceAddr < 0x03 || deviceAddr > 0x77) {
            return State.INVALIDRANGE;
        }
        try {
            device = DeviceManager.open(new I2CDeviceConfig(busId, deviceAddr, I2CDeviceConfig.ADDR_SIZE_7, -1),
                    DeviceManager.EXCLUSIVE);
        } catch (DeviceNotFoundException e) {
            return State.NOTFOUND;
        } catch (IOException e) {
            return State.ERROR;
        }

        try {

            // Force read
            if (device.isOpen() && device.read() == 0) {

            }
            return State.FOUND;
        } catch (IllegalArgumentException | IOException e) {
            return State.NOTFOUND;
        }
    }
}
