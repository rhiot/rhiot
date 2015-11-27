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
package camel.labs.iot.cloudlet.document.driver.mongodb;

import com.github.camellabs.iot.cloudlet.document.driver.DriverDocumentCloudlet;
import com.mongodb.Mongo;
import com.mongodb.MongoTimeoutException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static java.lang.Boolean.TRUE;
import static org.springframework.util.SocketUtils.findAvailableTcpPort;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {DriverDocumentCloudlet.class, EmbeddedMongoDbTest.class})
@IntegrationTest("camel.labs.iot.cloudlet.document.driver.mongodb.embedded=true")
@Ignore
public class EmbeddedMongoDbTest extends Assert {

    // Collaborator fixtures

    @Autowired
    Mongo mongo;

    // Configuration fixtures

    @BeforeClass
    public static void beforeClass() {
        int mongodbPort = findAvailableTcpPort();
        System.setProperty("camel.labs.iot.cloudlet.document.driver.mongodb.embedded.port", mongodbPort + "");
        System.setProperty("camel.labs.iot.cloudlet.document.driver.mongodb.springbootconfig", TRUE.toString());
        System.setProperty("spring.data.mongodb.port", mongodbPort + "");

        System.setProperty("server.port", findAvailableTcpPort() + "");
        System.setProperty("camel.labs.iot.cloudlet.rest.port", findAvailableTcpPort() + "");
    }

    // Tests

    @Test
    public void shouldStartEmbeddedMongodb() {
        try {
            mongo.getDatabaseNames();
        } catch (MongoTimeoutException ex) {
            fail("Embedded mongodb not started");
        }
    }

}