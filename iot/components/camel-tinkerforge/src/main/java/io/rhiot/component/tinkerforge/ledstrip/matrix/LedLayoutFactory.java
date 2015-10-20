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

import java.util.HashMap;
import java.util.Map;

public class LedLayoutFactory {
    
    private static Map<String, LedLayout> layouts = new HashMap<>();
    
    static {
        layouts.put("30x5", new LedLayout(new int[][] {
            {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29},
            {59,58,57,56,55,54,53,52,51,50,49,48,47,46,45,44,43,42,41,40,39,38,37,36,35,34,33,32,31,30},
            {60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89},
            {119,118,117,116,115,114,113,112,111,110,109,108,107,106,105,104,103,102,101,100,99,98,97,96,95,94,93,92,91,90},
            {120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149}
        }));
    }
    
    public static LedLayout createLayout(String layout) {
        return layouts.get(layout); 
    }
    
    public static void main(String[] args) {
        System.out.print("{");
        for (int i=0; i<30; i++) {
            System.out.print(i + ",");
        }
        System.out.println("}");
        
        System.out.print("{");
        for (int i=59; i>29; i--) { // 30 - 60
            System.out.print(i + ",");
        }
        System.out.println("}");
        
        System.out.print("{");
        for (int i=60; i<90; i++) {
            System.out.print(i + ",");
        }
        System.out.println("}");
        
        System.out.print("{");
        for (int i=119; i>89; i--) { // 90 - 120
            System.out.print(i + ",");
        }
        System.out.println("}");
        
        System.out.print("{");
        for (int i=120; i<150; i++) {
            System.out.print(i + ",");
        }
        System.out.print("}");
    }
}
