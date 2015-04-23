package com.github.camellabs.component.tinkerforge.io;

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
