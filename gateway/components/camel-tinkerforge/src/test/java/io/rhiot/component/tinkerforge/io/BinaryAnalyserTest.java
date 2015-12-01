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

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BinaryAnalyserTest {

    @Test
    public void returnsEmptyListOnZeroOnlyValue() throws Exception {
        assertTrue(BinaryAnalyser.analyse((short) 0b00000000, (short) 0b0).size() == 0);
    }

    @Test
    public void canRecogniseSinglebitValues() throws Exception {
        List<BinaryAnalyser.ResultSet> results;

        results = BinaryAnalyser.analyse((short) 0b00000001, (short) 0b00000001);
        assertTrue(results.size() == 1);
        assertTrue(results.get(0).index == 1);
        assertTrue(results.get(0).value);

        results = BinaryAnalyser.analyse((short) 0b00000010, (short) 0b00000010);
        assertTrue(results.size() == 1);
        assertTrue(results.get(0).index == 2);
        assertTrue(results.get(0).value);

        results = BinaryAnalyser.analyse((short) 0b00010000, (short) 0b11101111);
        assertTrue(results.size() == 1);
        assertTrue(results.get(0).index == 5);
        assertFalse(results.get(0).value);
    }

    @Test
    public void canRecogniseMultiplebitValues() throws Exception {
        List<BinaryAnalyser.ResultSet> results = BinaryAnalyser.analyse((short) 0b00100011, (short) 0b00000011);
        assertTrue(results.size() == 3);

        BinaryAnalyser.ResultSet result1 = results.get(0);
        BinaryAnalyser.ResultSet result2 = results.get(1);
        BinaryAnalyser.ResultSet result3 = results.get(2);

        assertTrue(result1.index == 1);
        assertTrue(result1.value == true);

        assertTrue(result2.index == 2);
        assertTrue(result2.value == true);

        assertTrue(result3.index == 6);
        assertTrue(result3.value == false);
    }
}
