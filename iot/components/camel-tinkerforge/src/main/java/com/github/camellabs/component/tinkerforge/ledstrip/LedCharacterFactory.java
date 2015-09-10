package com.github.camellabs.component.tinkerforge.ledstrip;

import java.util.HashMap;
import java.util.Map;

public class LedCharacterFactory {
    
    private static Map<Character, LedCharacter> characters = new HashMap<>();
    private static boolean O = false, X = true;
    
    static {    
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
}
