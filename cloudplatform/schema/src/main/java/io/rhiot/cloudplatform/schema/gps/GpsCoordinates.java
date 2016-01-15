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
package io.rhiot.cloudplatform.schema.gps;

import java.util.Date;
import java.util.Map;

public class GpsCoordinates {

    private String id;

    private String client;

    private String clientId;

    private Date timestamp;

    private double lat;

    private double lng;

    private Map<String, Object> enrich;

    public GpsCoordinates(String id, String client, String clientId, Date timestamp, double lat, double lng, Map<String, Object> enrich) {
        this.id = id;
        this.client = client;
        this.clientId = clientId;
        this.timestamp = timestamp;
        this.lat = lat;
        this.lng = lng;
        this.enrich = enrich;
    }

    public GpsCoordinates() {
    }

    public static GpsCoordinates gpsCoordinates(Date timestamp, double lat, double lng) {
        GpsCoordinates coordinates = new GpsCoordinates();
        coordinates.setTimestamp(timestamp);
        coordinates.setLat(lat);
        coordinates.setLng(lng);
        return coordinates;
    }

    public static GpsCoordinates gpsCoordinates(double lat, double lng) {
        return gpsCoordinates(new Date(), lat, lng);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Map<String, Object> getEnrich() {
        return enrich;
    }

    public void setEnrich(Map<String, Object> enrich) {
        this.enrich = enrich;
    }

}