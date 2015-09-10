package com.github.camellabs.component.tinkerforge.ledstrip;

import java.util.Arrays;

public class LedCharacter {
    
    private final boolean[][] coordinates;
    
    public LedCharacter(boolean[][] coordinates) {
        this.coordinates = coordinates;
    }

    public boolean[][] getCoordinates() {
        return coordinates;
    }
    
    public int getWidth() {
        return coordinates[0].length;
    }
    
    public int getHeight() {
        return coordinates.length;
    }
    
    public boolean getValueAt(int x, int y) {
        return coordinates[x][y];
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.deepHashCode(coordinates);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LedCharacter other = (LedCharacter) obj;
        if (!Arrays.deepEquals(coordinates, other.coordinates))
            return false;
        return true;
    }
}
