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
