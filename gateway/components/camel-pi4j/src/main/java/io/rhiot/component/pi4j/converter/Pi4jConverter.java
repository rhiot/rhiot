/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
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
package io.rhiot.component.pi4j.converter;

import static com.pi4j.io.gpio.PinState.getState;

import java.nio.ByteBuffer;

import io.rhiot.component.pi4j.i2c.driver.LSM303Value;
import com.pi4j.io.gpio.PinState;

import org.apache.camel.Converter;

@Converter
public final class Pi4jConverter {

    private Pi4jConverter() {
    }

    @Converter
    public static PinState toPinState(String state) {
        for (PinState pin : PinState.allStates()) {
            if (pin.getName().equals(state)) {
                return pin;
            }
        }
        return null;
    }

    @Converter
    public static boolean pinStateToBoolean(PinState state) {
        return state.isHigh();
    }

    @Converter
    public static PinState booleanToPinState(boolean state) {
        return getState(state);
    }

    @Converter
    public static String[] stringToStringArray(String string) {
        return string.split(",");
    }

    @Converter
    public static byte[] LSM303ValuetoArray(LSM303Value value) {

        ByteBuffer ret = ByteBuffer.allocate(3 * 4);

        ret.putInt(value.getX());
        ret.putInt(value.getY());
        ret.putInt(value.getZ());

        return ret.array();
    }

    @Converter
    public static LSM303Value intArraytoLSM303Value(byte[] value) {
        LSM303Value ret = new LSM303Value();

        ByteBuffer b = ByteBuffer.wrap(value);

        ret.setX(b.getInt());
        ret.setY(b.getInt());
        ret.setZ(b.getInt());

        return ret;
    }
}
