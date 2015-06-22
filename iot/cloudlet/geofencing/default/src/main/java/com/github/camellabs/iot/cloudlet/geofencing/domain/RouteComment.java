package com.github.camellabs.iot.cloudlet.geofencing.domain;

import java.util.Date;

public class RouteComment {

    private final String id;

    private final String routeId;

    private final Date created;

    private final String text;

    public RouteComment(String id, String routeId, Date created, String text) {
        this.id = id;
        this.routeId = routeId;
        this.created = created;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getRouteId() {
        return routeId;
    }

    public Date getCreated() {
        return created;
    }

    public String getText() {
        return text;
    }

}
