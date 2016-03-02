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

/**
 * Available operating modes for the BMP085. Code from Marcus Hirt, 2015, From
 * http://hirt.se/blog/?p=652
 */
public enum BMP180OperatingMode {

    ULTRA_LOW_POWER(45, (byte) 0x34, 3, 0), STANDARD(75, (byte) 0x74, 5, 1), HIGH_RES(135, (byte) 0xB4, 7,
            2), ULTRA_HIGH_RES(255, (byte) 0xF4, 12, 3);

    final int waitTime;
    final int currentDraw;
    final byte pressureControlCommand;
    final int overSamplingSetting;

    private BMP180OperatingMode(int maxConversionTime, byte pressureControlCommand, int currentDraw,
            int overSamplingSetting) {
        this.waitTime = (maxConversionTime + 5) / 10;
        this.overSamplingSetting = overSamplingSetting;
        this.pressureControlCommand = pressureControlCommand;
        this.currentDraw = currentDraw;

    }

    public int getOverSamplingSetting() {
        return this.overSamplingSetting;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public int getCurrentDraw() {
        return currentDraw;
    }

    public int getPressureControlCommand() {
        return pressureControlCommand;
    }
}
