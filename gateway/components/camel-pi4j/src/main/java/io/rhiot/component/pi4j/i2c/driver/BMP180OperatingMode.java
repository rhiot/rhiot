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

/**
 * Available operating modes for the BMP085. Code from Marcus Hirt, 2015, From
 * http://hirt.se/blog/?p=652
 */
public enum BMP180OperatingMode {
    /**
     * Max conversion time (pressure): 4.5ms Current draw: 3µA
     */
    ULTRA_LOW_POWER(45,
            3), /**
                 * Max conversion time (pressure): 7.5ms Current draw: 5µA
                 */
    STANDARD(75, 5), /**
                      * Max conversion time (pressure): 13.5ms Current draw: 7µA
                      */
    HIGH_RES(135,
            7), /**
                 * Max conversion time (pressure): 25.5ms Current draw: 12µA
                 */
    ULTRA_HIGH_RES(255, 12);

    final int waitTime;
    final int currentDraw;

    private BMP180OperatingMode(int maxConversionTime, int currentDraw) {
        this.waitTime = (maxConversionTime + 5) / 10;
        this.currentDraw = currentDraw;
    }

    /**
     * @return the over sampling setting.
     */
    public int getOverSamplingSetting() {
        return this.ordinal();
    }

    /**
     * @return time to wait for a result, in ms.
     */
    public int getWaitTime() {
        return waitTime;
    }

    /**
     * @return the average typical current at 1 sample per second, in µA.
     */
    public int getCurrentDraw() {
        return currentDraw;
    }
}
