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
 * 
 */
public enum HTS221ControlRegistry1 {

    ODR_ONE_SHOT(0b00), ODR_1_HZ(0b01), ODR_7_HZ(0b10), ODR_12DOT5_HZ(0b011), BDU_CONTINUOUS(
            0b0), BDU_UPDATE_AFTER_READING(0b1), PD_ACTIVE(0b1), PD_POWER_DOWN(0b0);

    final byte value;

    HTS221ControlRegistry1(int value) {
        this.value = (byte) value;
    }

}
