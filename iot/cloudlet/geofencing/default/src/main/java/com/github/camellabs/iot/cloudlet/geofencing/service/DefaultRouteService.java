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

import com.github.camellabs.iot.cloudlet.geofencing.domain.GpsCoordinates;
import com.github.camellabs.iot.cloudlet.geofencing.domain.Route;
import com.github.camellabs.iot.cloudlet.geofencing.domain.RouteGpsCoordinates;
import com.mongodb.Mongo;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Component
public class DefaultRouteService implements RouteService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultRouteService.class);

    private final MongoTemplate mongoTemplate;

    @Autowired
    public DefaultRouteService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<String> clients() {
        return mongoTemplate.getDb().getCollection(GpsCoordinates.class.getSimpleName()).distinct("client");
    }

    @Override
    public int analyzeRoutes(String client) {
        Query query = new Query();
        query.addCriteria(Criteria.where("client").is(client));
        query.limit(1);
        query.with(new Sort(DESC, "_id"));
        RouteGpsCoordinates lastRouteCoordinates = mongoTemplate.findOne(query, RouteGpsCoordinates.class);
        GpsCoordinates lastCoordinates = null;
        Route lastRoute = null;
        if(lastRouteCoordinates == null) {
            LOG.info("No GPS coordinates assigned to routes for client {}", client);
        } else {
            lastCoordinates = mongoTemplate.findById(lastRouteCoordinates.getCoordinatesId(), GpsCoordinates.class, GpsCoordinates.class.getSimpleName());
            lastRoute = mongoTemplate.findById(lastRouteCoordinates.getRouteId(), Route.class);
        }

        query = new Query();
        query.addCriteria(Criteria.where("client").is(client));
        if(lastRouteCoordinates != null) {
            query.addCriteria(Criteria.where("_id").gt(new ObjectId(lastRouteCoordinates.getCoordinatesId())));
        }
        query.limit(20);
        query.with(new Sort(ASC, "_id"));
        List<GpsCoordinates> coordinatesToAnalyze = mongoTemplate.find(query, GpsCoordinates.class, GpsCoordinates.class.getSimpleName());
        for(GpsCoordinates coordinates : coordinatesToAnalyze) {
          if(lastCoordinates == null || (TimeUnit.MILLISECONDS.toMinutes(coordinates.getTimestamp().getTime() - lastCoordinates.getTimestamp().getTime()) > 5)) {
              lastRoute = new Route(null, client, new Date());
              mongoTemplate.save(lastRoute);
          }
          RouteGpsCoordinates routeGpsCoordinates = new RouteGpsCoordinates(null, lastRoute.getId(), coordinates.getId(), client);
          mongoTemplate.save(routeGpsCoordinates);
          lastCoordinates = coordinates;
        }
        return coordinatesToAnalyze.size();
    }

    @Override
    public List<Route> routes(String client) {
        return mongoTemplate.findAll(Route.class);
    }

}
