package com.github.camellabs.iot.cloudlet.geofencing.service;

import com.github.camellabs.iot.cloudlet.geofencing.domain.Route;

import java.util.List;

public interface RouteService {

    int analyzeRoutes(String client);

    List<String> clients();

    List<Route> routes(String client);

}
