package com.github.camellabs.component.tinkerforge.ledstrip.matrix;

import java.util.ArrayList;
import java.util.List;

public class LedLayout {
    private final int[][] layout;
    
    public LedLayout(int[][] layout) {
        validateRectangularShape(layout);
        this.layout = layout;
    }

    private void validateRectangularShape(int[][] layout) {
        int width = layout[0].length;
        for (int[] line : layout) {
            if (line.length != width) {
                throw new IllegalArgumentException("The layout is not a rectangle. Each line should have an equal amount of leds.");
            }
        }
    }
    
    public int getWidth() {
        return layout[0].length;
    }
    
    public int getHeight() {
        return layout.length;
    }
    
    public int get(int x, int y) {
        return layout[x][y];
    }
    
    public List<Integer> mapCharacters(List<LedCharacter> characters) {
        validateLayoutDimensions(characters);
        
        List<Integer> ledNumbers = new ArrayList<>();
        
        int characterCounter = 0;
        for (LedCharacter character : characters) {
            
            for (int c=0; c<characters.size(); c+=4) {
                for (int x=0; x<5; x++) {
                    for (int y=0; y<3; y++) {
                        if (character.getValueAt(x, y)) {
                            ledNumbers.add(layout[x][characterCounter*4 + y]);
                        }
                    }
                }
            }
            characterCounter++;
        }
        
        ledNumbers.sort(null);
        return ledNumbers;
    }

    private void validateLayoutDimensions(List<LedCharacter> characters) {
        if (layout.length < 5) {
            throw new IllegalArgumentException("The layout should contain a minimum of 5 lines to support drawing characters.");
        }
        if ((characters.size() * 4) -1 > layout[0].length) {
            throw new IllegalArgumentException("The layout is not wide enough for "+characters.size()+" characters.");
        }
    }
}
