/*
 * *
 *  * Licensed to the Rhiot under one or more
 *  * contributor license agreements.  See the NOTICE file distributed with
 *  * this work for additional information regarding copyright ownership.
 *  * The licenses this file to You under the Apache License, Version 2.0
 *  * (the "License"); you may not use this file except in compliance with
 *  * the License.  You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  
 *  
 */

package com.github.camellabs.iot.cloudlet.webcam.service;

import com.github.camellabs.iot.cloudlet.webcam.WebcamCloudlet;
import com.github.camellabs.iot.cloudlet.webcam.domain.Webcam;
import com.github.camellabs.iot.cloudlet.webcam.domain.WebcamImage;
import io.rhiot.thingsdata.DocumentDriver;
import io.rhiot.thingsdata.SaveOperation;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.TRUE;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.springframework.util.SocketUtils.findAvailableTcpPort;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {WebcamCloudlet.class, DefaultWebcamServiceTest.class})
@IntegrationTest({"camel.labs.iot.cloudlet.document.driver.mongodb.embedded=true"})
public class DefaultWebcamServiceTest {
    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    WebcamService webcamService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    DocumentDriver documentDriver;

    static int restPort = findAvailableTcpPort();

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("server.port", findAvailableTcpPort() + "");
        System.setProperty("camel.labs.iot.cloudlet.rest.port", restPort + "");

        int mongodbPort = findAvailableTcpPort();
        System.setProperty("camel.labs.iot.cloudlet.document.driver.mongodb.embedded.port", mongodbPort + "");
        System.setProperty("camel.labs.iot.cloudlet.document.driver.mongodb.springbootconfig", TRUE.toString());
        System.setProperty("spring.data.mongodb.port", mongodbPort + "");
    }

    // Test data fixtures

    String restApi = "http://localhost:" + restPort + "/api/webcam/";

    String device1 = "device1";
    String device2 = "device2";
    String webcamId1 = "webcam1";
    String webcamId2 = "webcam2";
    String webcamId3 = "webcam3";
    WebcamImage webcamImage = new WebcamImage(null, new Date(),  "png", "target/test.png", webcamId1);
    WebcamImage webcamImage2 = new WebcamImage(null, new Date(),  "png", "target/test.png", webcamId2);
    WebcamImage webcamImage3 = new WebcamImage(null, new Date(),  "png", "target/test.png", webcamId3);
    Webcam webcam1 = new Webcam(null, device1, "my 1st webcam");
    Webcam webcam2 = new Webcam(null, device1, "my 2nd webcam");
    Webcam webcam3 = new Webcam(null, device2, "my 2nd devices webcam");
    
    @Before
    public void before() {
        mongoTemplate.getDb().dropDatabase();
    }

    @Test
    public void shouldReturnNoDevicesForEmptyDatabase() {
        assertEquals(0, webcamService.devices().size());
    }

    @Test
    public void shouldReturnDevices() throws URISyntaxException {
        // Given
        documentDriver.save(new SaveOperation(webcam1));
        URI devicesRequestUri = new URI(restApi + "devices");

        // When
        @SuppressWarnings("unchecked")
        Map<String, List<String>> devices = restTemplate.getForObject(devicesRequestUri, Map.class);

        // Then
        assertEquals(singletonList(device1), devices.get("devices"));
    }

    @Test
    @Ignore("todo taariq : failing test")
    public void shouldReturnWebcamImageByWebcamId() throws URISyntaxException {
        // Given
        documentDriver.save(new SaveOperation(webcamImage));
        URI uri = new URI(restApi + webcamId1);

        // When
        @SuppressWarnings("unchecked")
        Map<String, List<String>> webcams = restTemplate.getForObject(uri, Map.class);

        // Then
        assertEquals(singletonList(webcamId1), webcams.get("webcams"));
    }

    @Test
    @Ignore("todo taariq : failing test")
    public void shouldReturnWebcam() throws URISyntaxException {
        // Given
        String id = documentDriver.save(new SaveOperation(webcam1));
        documentDriver.save(new SaveOperation(webcam2));
        documentDriver.save(new SaveOperation(webcam3));
        URI uri = new URI(restApi + id);

        // When
        @SuppressWarnings("unchecked")
        Map<String, List<String>> webcams = restTemplate.getForObject(uri, Map.class);

        // Then
        assertEquals(singletonList(webcamId1), webcams.get("webcam"));
    }

}