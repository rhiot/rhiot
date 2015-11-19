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
public enum HTS221HumidityResolutionMode {

    AVGH_4(0b000), AVGH_8(0b001), AVGH_16(0b010), AVGH_32(0b011), AVGH_64(0b100), AVGH_128(0b101), AVGH_256(
            0b110), AVGH_512(0b0111);

    final int average;

    HTS221HumidityResolutionMode(int average) {
        this.average = average;
    }

}
