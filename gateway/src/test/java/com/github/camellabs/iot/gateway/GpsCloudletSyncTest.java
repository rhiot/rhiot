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
package com.github.camellabs.iot.gateway;

import com.github.camellabs.iot.cloudlet.geofencing.GeofencingCloudlet;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import io.rhiot.gateway.Gateway;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;

import static io.rhiot.vertx.camel.CamelContextFactories.closeCamelContext;
import static com.google.common.io.Files.createTempDir;
import static com.jayway.awaitility.Awaitility.await;
import static io.rhiot.utils.Properties.setBooleanProperty;
import static io.rhiot.utils.Properties.setIntProperty;
import static java.lang.System.setProperty;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.springframework.util.SocketUtils.findAvailableTcpPort;

public class GpsCloudletSyncTest extends Assert {

    Logger LOG = LoggerFactory.getLogger(getClass());

    static File gpsCoordinatesStore = createTempDir();

    static int geofencingApiPort = findAvailableTcpPort();

    static String dbName;

    static MongoClient mongoClient;

    @BeforeClass
    public static void beforeClass() throws UnknownHostException {
        closeCamelContext();

        setProperty("gps_cloudlet_sync", "true");

        // Gateway GPS store fixtures
        setProperty("gps_store_directory", gpsCoordinatesStore.getAbsolutePath());
        setProperty("gps_cloudlet_address", "localhost:" + geofencingApiPort);

        // Geofencing cloudlet fixtures
        dbName = "test";
        setProperty("cloudlet.document.driver.mongodb.db", dbName);
        setBooleanProperty("camel.labs.iot.cloudlet.document.driver.mongodb.embedded", true);
        setIntProperty("camel.labs.iot.cloudlet.rest.port", geofencingApiPort);

        new Thread() {
            @Override
            public void run() {
                GeofencingCloudlet.main("--spring.main.sources=com.github.camellabs.iot.cloudlet.geofencing.GeofencingCloudlet");
            }
        }.start();

        mongoClient = new MongoClient();
        new Gateway().start();
    }

    @Test
    public void shouldSendGpsCoordinatesToTheGeofencingCloudlet() throws InterruptedException, IOException {
        // Given
        URL countOperationUri = new URL("http://localhost:" + geofencingApiPort + "/api/document/count/GpsCoordinates");
        await().atMost(5, MINUTES).until(() -> {
            try {
                countOperationUri.openStream();
                return true;
            } catch (Exception ex) {
                LOG.debug("Can't connect to the geofencing cloudlet:", ex);
                return false;
            }
        });
        String gpsCoordinates = "{\"timestamp\": 123456789, \"lat\": 10.0, \"lng\": 20.0}";
        IOUtils.write(gpsCoordinates, new FileOutputStream(new File(gpsCoordinatesStore, "foo")));
        await().atMost(5, MINUTES).until(() -> {
            try {
                String countString = IOUtils.toString(countOperationUri);
                return Integer.parseInt(countString) == 1;
            } catch (Exception ex) {
                LOG.debug("Can't connect to the geofencing cloudlet:", ex);
                return false;
            }
        });

        // Then
        assertEquals(1, mongoClient.getDB(dbName).getCollection("GpsCoordinates").count());
        DBObject coordinates = mongoClient.getDB(dbName).getCollection("GpsCoordinates").findOne();
        assertEquals(10d, (Double) coordinates.get("latitude"), 0.0);
        assertEquals(20d, (Double) coordinates.get("longitude"), 0.0);
    }

}