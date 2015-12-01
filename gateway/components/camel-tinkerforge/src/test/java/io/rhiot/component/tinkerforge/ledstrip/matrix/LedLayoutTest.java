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
package io.rhiot.component.tinkerforge.ledstrip.matrix;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class LedLayoutTest {
    
    @Test
    public void shouldStoreGiven2dArrayOfLedPositions() {
        LedLayout layout = new LedLayout(new int[][] {
            {30,29,28,27,26,25},
            {24,23,22,21,20,19},
            {18,17,16,15,14,13},
            {12,11,10, 9, 8, 7},
            { 6, 5, 4, 3, 2, 1}
        });
        
        assertEquals(6, layout.getWidth());
        assertEquals(5, layout.getHeight());
        assertEquals(30, layout.get(0, 0));
        assertEquals(25, layout.get(0, 5));
        assertEquals(1, layout.get(4, 5));
    }
    
    @Test
    public void shouldRejectLayoutsWidthLinesOfDifferentLength() {
        try {
            new LedLayout(new int[][] {
                {5, 4, 3},
                {2, 1}
            });
            fail("LedLayout should have rejected non-rectangular layout.");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("The layout is not a rectangle."));
        }
    }
    
    @Test
    public void shouldRejectMappingWhenLayoutIsNotHighEnoughForCharacters() {
        LedLayout layoutWithTooFewLines = new LedLayout(new int[][] {
            {24,23,22,21,20,19},
            {18,17,16,15,14,13},
            {12,11,10, 9, 8, 7},
            { 6, 5, 4, 3, 2, 1}
        });
        
        List<LedCharacter> characters = new ArrayList<LedCharacter>();
        characters.add(LedCharacterFactory.getCharacter('A'));
        
        try {
            layoutWithTooFewLines.mapCharacters(characters);
            fail("LedLayout should have thrown exception because the layout should have at least 5 lines to support drawing characters.");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("The layout should contain a minimum of 5 lines to support drawing characters."));
        }
    }
    
    @Test
    public void shouldRejectMappingWhenCharactersDoNotFitOnLayout() {
        LedLayout layout = new LedLayout(new int[][] {
            {30,29,28,27,26,25},
            {24,23,22,21,20,19},
            {18,17,16,15,14,13},
            {12,11,10, 9, 8, 7},
            { 6, 5, 4, 3, 2, 1}
        });
        
        List<LedCharacter> characters = new ArrayList<LedCharacter>();
        characters.add(LedCharacterFactory.getCharacter('A'));
        characters.add(LedCharacterFactory.getCharacter('B'));
        
        try {
            layout.mapCharacters(characters);
            fail("LedLayout should have thrown exception because the characters do not fit on the layout.");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("The layout is not wide enough"));
        }
    }
    
    @Test
    public void shouldBeAbleToMapASingleCharacterCorrectlyToLedLayout() {
        LedLayout layout = new LedLayout(new int[][] {
            {50,49,48,47,46,45,44,43,42,41},
            {40,39,38,37,36,35,34,33,32,31},
            {30,29,28,27,26,25,24,23,22,21},
            {20,19,18,17,16,15,14,13,12,11},
            {10, 9, 8, 7, 6, 5, 4, 3, 2, 1}
        });
       
        List<LedCharacter> characters = new ArrayList<LedCharacter>();
        characters.add(LedCharacterFactory.getCharacter('A'));
        
        List<Integer> actual = layout.mapCharacters(characters);
        List<Integer> expected = new ArrayList<>(Arrays.asList(8, 10, 18, 20, 28, 29, 30, 38, 40, 49));
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void shouldBeAbleToMapMultipleCharactersCorrectlyToLedLayout() {
        LedLayout layout = new LedLayout(new int[][] {
            {50,49,48,47,46,45,44,43,42,41},
            {40,39,38,37,36,35,34,33,32,31},
            {30,29,28,27,26,25,24,23,22,21},
            {20,19,18,17,16,15,14,13,12,11},
            {10, 9, 8, 7, 6, 5, 4, 3, 2, 1}
        });
       
        List<LedCharacter> characters = new ArrayList<LedCharacter>();
        characters.add(LedCharacterFactory.getCharacter('A'));
        characters.add(LedCharacterFactory.getCharacter('B'));
        
        List<Integer> actual = layout.mapCharacters(characters);
        List<Integer> expected = new ArrayList<>(Arrays.asList(5, 6, 8, 10, 14, 16, 18, 20, 25, 26, 28, 29, 30, 34, 36, 38, 40, 45, 46, 49));
        
        assertEquals(expected, actual);
    }
}
