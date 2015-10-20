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

import org.junit.Test;

public class LedCharacterFactoryTest {
    private boolean O = false, X = true;
    
    @Test
    public void testGetCharacter() {
        
        LedCharacter expectedCharacter = new LedCharacter(new boolean[][] {
            {O,X,O},
            {X,O,X},
            {X,X,X},
            {X,O,X},
            {X,O,X}
        });
        
        LedCharacter actual = LedCharacterFactory.getCharacter('A');
        assertEquals(expectedCharacter, actual);
        assertEquals(3, actual.getWidth());
        assertEquals(5, actual.getHeight());
        assertEquals(false, actual.getValueAt(0,0));
        assertEquals(true, actual.getValueAt(0,1));
        assertEquals(false, actual.getValueAt(0,2));
        assertEquals(expectedCharacter, actual);
    }
    
}
