/**
 * Licensed to the Camel Labs under one or more
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
package com.github.camellabs.iot.component.gps.bu353;

import static java.lang.Double.parseDouble;

public class GpsCoordinates {

    private final double lat;

    private final double lng;

    public GpsCoordinates(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public static GpsCoordinates parse(String line) {
        String[] lineParts = line.split(",");
        double lat = parseDouble(lineParts[3]) / 100;
        double lng = parseDouble(lineParts[5]) / 100;
        return new GpsCoordinates(lat, lng);
    }

    public double lat() {
        return lat;
    }

    public double lng() {
        return lng;
    }

}
