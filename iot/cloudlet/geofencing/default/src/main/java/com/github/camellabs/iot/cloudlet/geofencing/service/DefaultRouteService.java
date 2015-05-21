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
package com.github.camellabs.iot.cloudlet.geofencing.service;

import com.github.camellabs.iot.cloudlet.document.driver.spi.DocumentDriver;
import com.github.camellabs.iot.cloudlet.document.driver.spi.SaveOperation;
import com.github.camellabs.iot.cloudlet.geofencing.domain.GpsCoordinates;
import com.github.camellabs.iot.cloudlet.geofencing.domain.Route;
import com.github.camellabs.iot.cloudlet.geofencing.domain.RouteGpsCoordinates;
import com.github.camellabs.iot.cloudlet.geofencing.googlemaps.StaticMaps;
import com.google.maps.model.LatLng;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.camellabs.iot.cloudlet.document.driver.spi.Pojos.collectionName;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.util.Assert.isTrue;

@Component("routeService")
public class DefaultRouteService implements RouteService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultRouteService.class);

    private final DocumentDriver documentDriver;

    private final MongoTemplate mongoTemplate;

    @Autowired
    public DefaultRouteService(DocumentDriver documentDriver, MongoTemplate mongoTemplate) {
        this.documentDriver = documentDriver;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public int analyzeRoutes(String client) {
        RouteGpsCoordinates lastRouteCoordinates = findLastRouteCoordinates(client);
        GpsCoordinates lastCoordinates = null;
        if(lastRouteCoordinates == null) {
            LOG.info("No GPS coordinates assigned to routes for client {}", client);
        } else {
            lastCoordinates = mongoTemplate.findById(lastRouteCoordinates.getCoordinatesId(), GpsCoordinates.class, GpsCoordinates.class.getSimpleName());
        }

        Query query = new Query();
        query.addCriteria(where("client").is(client));
        if(lastRouteCoordinates != null) {
            query.addCriteria(where("_id").gt(new ObjectId(lastRouteCoordinates.getCoordinatesId())));
        }
        query.limit(20);
        query.with(new Sort(ASC, "_id"));
        List<GpsCoordinates> coordinatesToAnalyze = mongoTemplate.find(query, GpsCoordinates.class, GpsCoordinates.class.getSimpleName());
        for(GpsCoordinates coordinates : coordinatesToAnalyze) {
          String routeId;
          if(lastCoordinates == null || (TimeUnit.MILLISECONDS.toMinutes(coordinates.getTimestamp().getTime() - lastCoordinates.getTimestamp().getTime()) > 5)) {
              Route newRoute = new Route(null, client, new Date());
              routeId = documentDriver.save(new SaveOperation(newRoute));
          } else {
              routeId = lastRouteCoordinates.getRouteId();
          }

          lastRouteCoordinates = new RouteGpsCoordinates(null, routeId, coordinates.getId(), client);
          mongoTemplate.save(lastRouteCoordinates, collectionName(RouteGpsCoordinates.class));
          lastCoordinates = coordinates;
        }
        return coordinatesToAnalyze.size();
    }

    @Override
    public List<String> clients() {
        return mongoTemplate.getDb().getCollection(GpsCoordinates.class.getSimpleName()).distinct("client");
    }

    @Override
    public List<Route> routes(String client) {
        return mongoTemplate.findAll(Route.class, Route.class.getSimpleName());
    }

    @Override
    public URL renderRouteUrl(String routeId) {
        isTrue(routeId != null, "Route ID can't be null.");

        Query query = new Query().addCriteria(where("routeId").is(routeId));
        List<ObjectId> coordinatesIds = mongoTemplate.find(query, RouteGpsCoordinates.class, collectionName(RouteGpsCoordinates.class)).
                parallelStream().map(coordinates -> new ObjectId(coordinates.getCoordinatesId())).collect(toList());
        query = new Query().addCriteria(where("_id").in(coordinatesIds));
        List<LatLng> coordinatesToEncode = mongoTemplate.find(query, GpsCoordinates.class, collectionName(GpsCoordinates.class)).
                parallelStream().map(coordinates -> new LatLng(coordinates.getLatitude().doubleValue(), coordinates.getLongitude().doubleValue())).collect(toList());
        return StaticMaps.renderRouteUrl(coordinatesToEncode);
    }

    // Callbacks

    protected RouteGpsCoordinates findLastRouteCoordinates(String client) {
        Query lastRouteCoordinatesQuery = new Query().addCriteria(where("client").is(client)).with(new Sort(DESC, "_id")).limit(1);
        return mongoTemplate.findOne(lastRouteCoordinatesQuery, RouteGpsCoordinates.class);
    }

}
