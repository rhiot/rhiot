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
package com.github.camellabs.iot.cloudlet.geofencing.googlemaps;

import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.LatLng;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;

public class StaticMaps {

    private static final String MAP_API_BASE = "https://maps.googleapis.com/maps/api/staticmap";

    public static String encodeRoute(LatLng... coordinates) {
        return encodeRoute(asList(coordinates));
    }

    public static String encodeRoute(List<LatLng> coordinates) {
        return PolylineEncoding.encode(coordinates);
    }

    public static String extractEncodedPath(URL url) {
        String urlString = url.toString();
        return urlString.substring(urlString.indexOf("enc:") + 4);
    }

    public static URL renderRouteUrl(LatLng... coordinates) {
       return renderRouteUrl(newArrayList(coordinates));
    }

    public static URL renderRouteUrl(List<LatLng> coordinates) {
        try {
            return new URL(MAP_API_BASE + "?size=400x400&path=weight:5%7Ccolor:0x0000ff%7Cenc:" + encodeRoute(coordinates));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}
