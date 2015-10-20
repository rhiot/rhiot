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
