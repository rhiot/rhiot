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
package io.rhiot.component.gpsd;

import java.util.Date;

import static java.lang.Double.parseDouble;
import static java.lang.Long.parseLong;
import static java.lang.String.format;

/**
 * GPS coordinates collected and stored on the device.
 */
public class ClientGpsCoordinates {
    
    private final Date timestamp;

    private final double lat;

    private final double lng;

    public ClientGpsCoordinates(Date timestamp, double lat, double lng) {
        this.timestamp = timestamp;
        this.lat = lat;
        this.lng = lng;
    }

    // Encoding

    public String serialize() {
        return format("%d,%s,%s", timestamp.getTime(), lat, lng);
    }

    public static ClientGpsCoordinates deserialize(String serializedCoordinates) {
        String[] serializedCoordinatesParts = serializedCoordinates.split(",");
        long timestamp = parseLong(serializedCoordinatesParts[0]);
        double lat = parseDouble(serializedCoordinatesParts[1]);
        double lng = parseDouble(serializedCoordinatesParts[2]);
        return new ClientGpsCoordinates(new Date(timestamp), lat, lng);
    }

    // Getters

    public Date timestamp() {
        return new Date(timestamp.getTime());
    }

    public double lat() {
        return lat;
    }

    public double lng() {
        return lng;
    }

}
