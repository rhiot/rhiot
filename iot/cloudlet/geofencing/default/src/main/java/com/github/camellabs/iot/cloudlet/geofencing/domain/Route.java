package com.github.camellabs.iot.cloudlet.geofencing.domain;

import java.util.Date;

public class Route {

    private final String id;

    private final String client;

    private final Date created;

    public Route(String id, String client, Date created) {
        this.id = id;
        this.client = client;
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public String getClient() {
        return client;
    }

    public Date getCreated() {
        return created;
    }

}
