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
 * 
 */
public enum LPS25HControlRegistry1 {

    ODR_ONE_SHOT(0b000), ODR_1_HZ(0b001), ODR_7_HZ(0b010), ODR_12DOT5_HZ(0b011), ODR_25_HZ(0b100), ODR_RESERVED(
            0b101), DIFFEN_DISABLED(0b0), DIFFEN_ENABLE(0b1), RESETAZ_DISABLE(0b0), RESETAZ_RESET(0b1), BDU_CONTINUOUS(
                    0b0), BDU_UPDATE_AFTER_READING(0b1), PD_ACTIVE(0b1), PD_POWER_DOWN(0b0), SIM_4WIRE(0b0), SIM_3WIRE(
                            0b1);

    final byte value;

    LPS25HControlRegistry1(int value) {
        this.value = (byte) value;
    }

}
