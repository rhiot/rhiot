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

public interface HTS221Constants {

    public static final byte HTS221_ADDRESS = 0x5F;

    /**
     * We use the same registry
     */
    public static final byte WHO_AM_I_ADDR = 0x0F;
    public static final short WHO_AM_I = 0xBC;

    public static final byte AV_CONF = 0x10;
    public static final byte CTRL_REG1 = 0x20;
    public static final byte CTRL_REG2 = 0x21;
    public static final byte CTRL_REG3 = 0x22;
    public static final byte STATUS_REG = 0x27;
    public static final byte HUMIDITY_OUT_L = 0x28;
    public static final byte HUMIDITY_OUT_H = 0x29;
    public static final byte TEMP_OUT_L = 0x2A;
    public static final byte TEMP_OUT_H = 0x2B;
    public static final byte H0_rH_x2 = 0x30;
    public static final byte H1_rH_x2 = 0x31;
    public static final byte T0_degC_x8 = 0x32;
    public static final byte T1_degC_x8 = 0x33;
    public static final byte T1_T0_msb = 0x35;
    public static final byte T0_OUT = 0x3C; // 2 bytes
    public static final byte T1_OUT = 0x3E; // 2 bytes
    public static final byte H0_T0_OUT = 0x36; // 2 bytes
    public static final byte H1_T0_OUT = 0x3A; // 2 bytes
    public static final byte TEMP_DATA_AVAILABLE_MASK = 0x01;
    public static final byte HUMI_DATA_AVAILABLE_MASK = 0x02;
    public static final int MULTI_BYTE_READ_MASK = 0x80;

}
