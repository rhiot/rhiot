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

public enum MCP23017LCDColor {
    OFF(0x00), RED(0x01), GREEN(0x02), BLUE(0x04), YELLOW(RED.getValue() + GREEN.getValue()), TEAL(
            GREEN.getValue() + BLUE.getValue()), VIOLET(RED.getValue() + BLUE.getValue()), WHITE(
                    RED.getValue() + GREEN.getValue() + BLUE.getValue()), ON(WHITE.getValue());

    private final int value;

    MCP23017LCDColor(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Returns the matching color value, or WHITE if no matching color could be
     * found.
     * 
     * @param colorValue
     * @return
     */
    public static MCP23017LCDColor getByValue(int colorValue) {
        for (MCP23017LCDColor c : values()) {
            if (c.getValue() == colorValue) {
                return c;
            }
        }
        return WHITE;
    }
}
