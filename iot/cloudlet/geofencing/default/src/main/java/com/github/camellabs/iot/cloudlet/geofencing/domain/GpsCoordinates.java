package com.github.camellabs.iot.cloudlet.geofencing.domain;

import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.Date;

public class GpsCoordinates {

    String id;

    String client;

    String clientId;

    Date timestamp;

    BigDecimal latitude;

    BigDecimal longitude;

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

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
}
