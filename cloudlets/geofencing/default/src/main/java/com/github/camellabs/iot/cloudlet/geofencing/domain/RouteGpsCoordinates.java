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
package com.github.camellabs.iot.cloudlet.geofencing.domain;

public class RouteGpsCoordinates {

    private final String id;

    private final String routeId;

    private final String coordinatesId;

    private final String client;

    public RouteGpsCoordinates(String id, String routeId, String coordinatesId, String client) {
        this.id = id;
        this.routeId = routeId;
        this.coordinatesId = coordinatesId;
        this.client = client;
    }

    public String getId() {
        return id;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getCoordinatesId() {
        return coordinatesId;
    }

    public String getClient() {
        return client;
    }

}
