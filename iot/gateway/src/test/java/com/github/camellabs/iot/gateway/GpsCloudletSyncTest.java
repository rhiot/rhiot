/**
 * Licensed to the Camel Labs under one or more
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
package com.github.camellabs.iot.gateway;

import com.github.camellabs.iot.cloudlet.geofencing.GeofencingCloudlet;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;

import static com.github.camellabs.iot.utils.Properties.booleanProperty;
import static com.github.camellabs.iot.utils.Properties.intProperty;
import static com.google.common.io.Files.createTempDir;
import static com.jayway.awaitility.Awaitility.await;
import static java.lang.System.setProperty;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.springframework.util.SocketUtils.findAvailableTcpPort;

public class GpsCloudletSyncTest extends Assert {

    static File gpsCoordinatesStore = createTempDir();

    static int geofencingApiPort = findAvailableTcpPort();

    static String dbName;

    static MongoClient mongoClient;

    @BeforeClass
    public static void beforeClass() throws UnknownHostException {
        setProperty("camellabs_iot_gateway_gps_cloudlet_sync", "true");

        // Gateway GPS store fixtures
        setProperty("camellabs_iot_gateway_gps_store_directory", gpsCoordinatesStore.getAbsolutePath());
        setProperty("camellabs_iot_gateway_gps_cloudlet_address", "localhost:" + geofencingApiPort);

        // Geofencing cloudlet fixtures
        dbName = "test";
        setProperty("camel.labs.iot.cloudlet.document.driver.mongodb.db", dbName);
        booleanProperty("camel.labs.iot.cloudlet.document.driver.mongodb.embedded", true);
        intProperty("camel.labs.iot.cloudlet.rest.port", geofencingApiPort);

        new Thread() {
            @Override
            public void run() {
                GeofencingCloudlet.main("--spring.main.sources=com.github.camellabs.iot.cloudlet.geofencing.GeofencingCloudlet");
            }
        }.start();

        mongoClient = new MongoClient();
        new VertxGateway().start();
    }

    @Test
    public void shouldSendGpsCoordinatesToTheGeofencingCloudlet() throws InterruptedException, IOException {
        Thread.sleep(10000);
        IOUtils.write(System.currentTimeMillis() + ",10,20", new FileOutputStream(new File(gpsCoordinatesStore, "foo")));

        // When
        await().atMost(2, MINUTES).until(() -> mongoClient.getDB(dbName).getCollection("GpsCoordinates").count() > 0);

        // Then
        assertEquals(1, mongoClient.getDB(dbName).getCollection("GpsCoordinates").count());
        DBObject coordinates = mongoClient.getDB(dbName).getCollection("GpsCoordinates").findOne();
        assertEquals(10d, (Double) coordinates.get("latitude"), 0.0);
        assertEquals(20d, (Double) coordinates.get("longitude"), 0.0);
    }

}