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

public interface TSL2561Constants {

    public final static int TSL2561_BUSID = 0x01;
    public final static int TSL2561_DEVICEADDR = 0x01;

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

}
