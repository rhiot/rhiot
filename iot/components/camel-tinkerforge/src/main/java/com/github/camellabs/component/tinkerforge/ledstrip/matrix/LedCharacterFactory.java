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
package com.github.camellabs.component.tinkerforge.ledstrip.matrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LedCharacterFactory {
    
    private static Map<Character, LedCharacter> characters = new HashMap<>();
    private static boolean O = false, X = true;
    
    static {
       characters.put('1', new LedCharacter(new boolean[][] {
            {O,X,O},
            {X,X,O},
            {O,X,O},
            {O,X,O},
            {X,X,X}
       }));
        
       characters.put('2', new LedCharacter(new boolean[][] {
           {X,X,X},
           {O,O,X},
           {X,X,X},
           {X,O,O},
           {X,X,X}
       }));
       
       characters.put('3', new LedCharacter(new boolean[][] {
           {X,X,X},
           {O,O,X},
           {X,X,X},
           {O,O,X},
           {X,X,X}
       }));
       
       characters.put('4', new LedCharacter(new boolean[][] {
           {X,O,X},
           {X,O,X},
           {X,X,X},
           {O,O,X},
           {O,O,X}
       }));
       
       characters.put('5', new LedCharacter(new boolean[][] {
           {X,X,X},
           {X,O,O},
           {X,X,X},
           {O,O,X},
           {X,X,X}
       }));
       
       characters.put('6', new LedCharacter(new boolean[][] {
           {X,X,X},
           {X,O,O},
           {X,X,X},
           {X,O,X},
           {X,X,X}
       }));
       
       characters.put('7', new LedCharacter(new boolean[][] {
           {X,X,X},
           {O,O,X},
           {O,X,X},
           {O,O,X},
           {O,O,X}
       }));
       
       characters.put('8', new LedCharacter(new boolean[][] {
           {X,X,X},
           {X,O,X},
           {X,X,X},
           {X,O,X},
           {X,X,X}
       }));
       
       characters.put('9', new LedCharacter(new boolean[][] {
           {X,X,X},
           {X,O,X},
           {X,X,X},
           {O,O,X},
           {X,X,X}
       }));
       
       characters.put('0', new LedCharacter(new boolean[][] {
           {X,X,X},
           {X,O,X},
           {X,O,X},
           {X,O,X},
           {X,X,X}
       }));
       
       
        
       characters.put('A', new LedCharacter(new boolean[][] {
             {O,X,O},
             {X,O,X},
             {X,X,X},
             {X,O,X},
             {X,O,X}
       }));
        
       characters.put('B', new LedCharacter(new boolean[][] {
            {X,X,O},
            {X,O,X},
            {X,X,O},
            {X,O,X},
            {X,X,O}
        }));
        
        characters.put('C', new LedCharacter(new boolean[][] {
            {X,X,X},
            {X,O,O},
            {X,O,O},
            {X,O,O},
            {X,X,X}
        }));
    }
    
    public static LedCharacter getCharacter(char character) {
        return characters.get(character);
    }

    public static List<LedCharacter> getCharacters(String message) {
        List<LedCharacter> ledCharacters = new ArrayList<>();
        
        char[] messageCharacters = message.toUpperCase().toCharArray();
        
        for (char character : messageCharacters) {
            if (characters.containsKey(character)) {
                ledCharacters.add(characters.get(character));
            }
        }
        
        return ledCharacters;
    }
}
