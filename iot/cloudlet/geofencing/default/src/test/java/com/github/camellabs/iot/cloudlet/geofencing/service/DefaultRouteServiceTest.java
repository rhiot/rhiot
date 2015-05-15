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
import com.github.camellabs.iot.cloudlet.geofencing.GeofencingCloudlet;
import com.github.camellabs.iot.cloudlet.geofencing.domain.GpsCoordinates;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.Date;

import static com.github.camellabs.iot.cloudlet.document.sdk.Pojos.pojoClassToCollection;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {GeofencingCloudlet.class, DefaultRouteServiceTest.class})
@IntegrationTest({"camel.labs.iot.cloudlet.document.driver.mongodb.embedded=true"})
public class DefaultRouteServiceTest extends Assert {

    @Autowired
    RouteService routeService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    DocumentDriver documentDriver;

    String client = "client";

    GpsCoordinates point1 = new GpsCoordinates(null, client, "clientId", new Date(), TEN, TEN);
    GpsCoordinates point2 = new GpsCoordinates(null, client, "clientId", new Date(), TEN.add(ONE), TEN.add(ONE));
    GpsCoordinates point3 = new GpsCoordinates(null, client, "clientId", new DateTime(point2.getTimestamp()).plusMinutes(6).toDate(), TEN.add(ONE), TEN.add(ONE));

    @Before
    public void before() {
        mongoTemplate.getDb().dropDatabase();
    }

    @Test
    public void shouldReturnNoClientsForEmptyDatabase() {
        assertEquals(0, routeService.clients().size());
    }

    @Test
    public void shouldReturnClients() {
        documentDriver.save(new SaveOperation(pojoClassToCollection(GpsCoordinates.class), point1));
        assertEquals(1, routeService.clients().size());
        assertEquals(Collections.singletonList(client), routeService.clients());
    }

    @Test
    public void shouldNotFailForEmptyDatabase() {
        assertEquals(0, routeService.analyzeRoutes(client));
        assertEquals(0, routeService.routes(client).size());
    }

    @Test
    public void shouldIdempotentlyAssignSinglePointToRoute() {
        documentDriver.save(new SaveOperation(pojoClassToCollection(GpsCoordinates.class), point1));
        assertEquals(1, routeService.analyzeRoutes(client));
        assertEquals(0, routeService.analyzeRoutes(client));
        assertEquals(1, routeService.routes(client).size());
    }

    @Test
    public void shouldIdempotentlyAssignTwoPointsToRoute() {
        documentDriver.save(new SaveOperation(pojoClassToCollection(GpsCoordinates.class), point1));
        assertEquals(1, routeService.analyzeRoutes(client));
        assertEquals(0, routeService.analyzeRoutes(client));
        assertEquals(1, routeService.routes(client).size());

        documentDriver.save(new SaveOperation(pojoClassToCollection(GpsCoordinates.class), point2));
        assertEquals(1, routeService.analyzeRoutes(client));
        assertEquals(0, routeService.analyzeRoutes(client));
        assertEquals(1, routeService.routes(client).size());
    }

    @Test
    public void shouldDetectNextRoute() {
        documentDriver.save(new SaveOperation(pojoClassToCollection(GpsCoordinates.class), point1));
        documentDriver.save(new SaveOperation(pojoClassToCollection(GpsCoordinates.class), point2));
        documentDriver.save(new SaveOperation(pojoClassToCollection(GpsCoordinates.class), point3));

        assertEquals(3, routeService.analyzeRoutes(client));

        assertEquals(2, routeService.routes(client).size());
    }

}