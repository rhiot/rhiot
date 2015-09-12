package com.github.camellabs.component.tinkerforge.ledstrip.matrix;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.camellabs.component.tinkerforge.ledstrip.matrix.LedCharacter;
import com.github.camellabs.component.tinkerforge.ledstrip.matrix.LedCharacterFactory;

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
