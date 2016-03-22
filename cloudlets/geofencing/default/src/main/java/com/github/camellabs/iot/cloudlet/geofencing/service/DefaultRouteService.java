/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.camellabs.iot.cloudlet.geofencing.service;

import com.github.camellabs.iot.cloudlet.geofencing.domain.GpsCoordinates;
import com.github.camellabs.iot.cloudlet.geofencing.domain.Route;
import com.github.camellabs.iot.cloudlet.geofencing.domain.RouteComment;
import com.github.camellabs.iot.cloudlet.geofencing.domain.RouteGpsCoordinates;
import com.github.camellabs.iot.cloudlet.geofencing.googlemaps.StaticMaps;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.maps.model.LatLng;
import io.rhiot.cloudplatform.service.document.api.DocumentStore;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.github.camellabs.iot.cloudlet.geofencing.domain.Route.createNewRoute;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static io.rhiot.cloudplatform.service.document.api.Pojos.collectionName;
import static io.rhiot.cloudplatform.service.document.api.Pojos.pojoToMap;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.util.Assert.isTrue;

public class DefaultRouteService implements RouteService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultRouteService.class);

    private final DocumentStore documentDriver;

    private final MongoTemplate mongoTemplate;

    private final int routeAnalysisBatchSize;

    @Autowired
    public DefaultRouteService(DocumentStore documentDriver, MongoTemplate mongoTemplate,
                               @Value("${camel.labs.iot.cloudlet.geofencing.routeAnalysis.batch.size:20}") int routeAnalysisBatchSize) {
        this.documentDriver = documentDriver;
        this.mongoTemplate = mongoTemplate;
        this.routeAnalysisBatchSize = routeAnalysisBatchSize;
    }

    // Overridden

    @Override
    public int analyzeRoutes(String client) {
        RouteGpsCoordinates lastRouteCoordinates = findLastRouteCoordinates(client);
        GpsCoordinates lastCoordinates = null;
        if (lastRouteCoordinates == null) {
            LOG.info("No GPS coordinates assigned to routes for client {}", client);
        } else {
            lastCoordinates = mongoTemplate.findById(lastRouteCoordinates.getCoordinatesId(), GpsCoordinates.class, GpsCoordinates.class.getSimpleName());
        }

        Query query = new Query();
        query.addCriteria(where("client").is(client));
        if (lastRouteCoordinates != null) {
            query.addCriteria(where("_id").gt(new ObjectId(lastRouteCoordinates.getCoordinatesId())));
        }
        query.limit(routeAnalysisBatchSize);
        query.with(new Sort(ASC, "_id"));
        List<GpsCoordinates> coordinatesToAnalyze = mongoTemplate.find(query, GpsCoordinates.class, GpsCoordinates.class.getSimpleName());
        for (GpsCoordinates coordinates : coordinatesToAnalyze) {
            String routeId;
            if (lastCoordinates == null || (TimeUnit.MILLISECONDS.toMinutes(coordinates.getTimestamp().getTime() - lastCoordinates.getTimestamp().getTime()) > 5)) {
                Route newRoute = createNewRoute(client);
                routeId = documentDriver.save(collectionName(newRoute.getClass()), pojoToMap(newRoute));
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
        return mongoTemplate.find(new Query().addCriteria(where("deleted").is(null)), Route.class, collectionName(Route.class));
    }

    @Override
    public void deleteRoute(String routeId) {
        Map<String, Object> queryBuilder = ImmutableMap.of("query", ImmutableMap.of("_idIn", singletonList(new ObjectId(routeId))));
        Map<String,Object> route = documentDriver.findByQuery(Route.class.getName(), queryBuilder).get(0);
        route.put("deleted", new Date());
        documentDriver.save(collectionName(Route.class), route);
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

    @Override
    public byte[] exportRoutes(String client, String format) {
        try {
            List<List<String>> exportedRoutes = exportRoutes(client);
            Workbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet("Routes report for " + client);

            for (int i = 0; i < exportedRoutes.size(); i++) {
                Row row = sheet.createRow(i);
                for (int j = 0; j < exportedRoutes.get(i).size(); j++) {
                    row.createCell(j).setCellValue(exportedRoutes.get(i).get(j));
                }
            }

            ByteArrayOutputStream xlsBytes = new ByteArrayOutputStream();
            workbook.write(xlsBytes);
            xlsBytes.close();
            return xlsBytes.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<List<String>> exportRoutes(String client) {
        List<Route> routes = routes(client);
        List<String> routesIds = routes.parallelStream().map(Route::getId).collect(toList());
        Query commentsQuery = new Query().addCriteria(where("routeId").in(routesIds));
        List<RouteComment> routeComments = mongoTemplate.find(commentsQuery, RouteComment.class, "RouteComment");
        Map<String, List<String>> commentsForRoute = newHashMap();
        for (RouteComment comment : routeComments) {
            String routeId = comment.getRouteId();
            if (!commentsForRoute.containsKey(routeId)) {
                commentsForRoute.put(routeId, newLinkedList());
            }
            commentsForRoute.get(routeId).add(comment.getCreated().toString());
            commentsForRoute.get(routeId).add(comment.getText());
        }
        return routes.parallelStream().map(route ->
                        ImmutableList.<String>builder().
                                add(route.getCreated().toString()).
                                addAll(commentsForRoute.getOrDefault(route.getId(), emptyList())).
                                build()
        ).collect(toList());
    }

    // Callbacks

    protected RouteGpsCoordinates findLastRouteCoordinates(String client) {
        Query lastRouteCoordinatesQuery = new Query().addCriteria(where("client").is(client)).with(new Sort(DESC, "_id")).limit(1);
        return mongoTemplate.findOne(lastRouteCoordinatesQuery, RouteGpsCoordinates.class, collectionName(RouteGpsCoordinates.class));
    }

}
