package com.github.camellabs.iot.cloudlet.geofencing.domain;

public class RouteGpsCoordinates {

    private String id;

    private String routeId;

    private String coordinatesId;

    private String client;

    public RouteGpsCoordinates(String id, String routeId, String coordinatesId, String client) {
        this.setId(id);
        this.setRouteId(routeId);
        this.setCoordinatesId(coordinatesId);
        this.setClient(client);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getCoordinatesId() {
        return coordinatesId;
    }

    public void setCoordinatesId(String coordinatesId) {
        this.coordinatesId = coordinatesId;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

}
