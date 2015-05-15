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
package com.github.camellabs.iot.cloudlet.geofencing.domain;

import java.math.BigDecimal;
import java.util.Date;

public class GpsCoordinates {

    private final String id;

    private final String client;

    private final String clientId;

    private final Date timestamp;

    private final BigDecimal latitude;

    private final BigDecimal longitude;

    public GpsCoordinates(String id, String client, String clientId, Date timestamp, BigDecimal latitude, BigDecimal longitude) {
        this.id = id;
        this.client = client;
        this.clientId = clientId;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public String getClient() {
        return client;
    }

    public String getClientId() {
        return clientId;
    }

    public Date getTimestamp() {
        return timestamp;
    }


    public BigDecimal getLatitude() {
        return latitude;
    }


    public BigDecimal getLongitude() {
        return longitude;
    }

}
