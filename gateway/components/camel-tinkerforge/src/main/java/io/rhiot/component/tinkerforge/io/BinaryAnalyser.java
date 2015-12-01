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
package io.rhiot.component.tinkerforge.io;

import java.util.ArrayList;
import java.util.List;

public class BinaryAnalyser {

    public static List<ResultSet> analyse(short mask, short data) {
        List<ResultSet> results = new ArrayList<>(Short.SIZE);

        String stringMask = Integer.toBinaryString(mask);
        String stringData = Integer.toBinaryString(data);

        for (int i=1; i<=stringMask.length(); i++) {
            int index = stringMask.length() - i;
            char bit = stringMask.charAt(index);
            if (bit == '1') {
                results.add(new ResultSet((short)i, isSet(stringData.length()-i, stringData)));
            }
        }
        return results;
    }

    private static boolean isSet(int index, String stringData) {
        if (index < 0) return false;
        if (stringData.length()-1 < index) return false;

        if (stringData.charAt(index) == '1') {
            return true;
        } else {
            return false;
        }
    }

    public static class ResultSet {
        public short index;
        public boolean value;

        public ResultSet(short index, boolean value) {
            this.index = index;
            this.value = value;
        }
    }
}
