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
package camel.labs.iot.cloudlet.document.mongodb;

import com.mongodb.Mongo;
import io.rhiot.datastream.document.mongodb.MongodbDocumentStore;
import com.google.common.collect.ImmutableMap;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import io.rhiot.datastream.document.FindByQueryOperation;
import org.apache.camel.TypeConverter;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static de.flapdoodle.embed.mongo.distribution.Version.V2_6_1;
import static de.flapdoodle.embed.process.runtime.Network.localhostIsIPv6;
import static org.springframework.util.SocketUtils.findAvailableTcpPort;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MongoDbDocumentDriverTest.class)
@SpringBootApplication
@IntegrationTest("spring.main.sources=camel.labs.iot.cloudlet.document.mongodb")
public class MongoDbDocumentDriverTest extends Assert {

    @Autowired
    TypeConverter typeConverter;

    @Bean
    MongodbDocumentStore documentDriver(Mongo mongo) {
        return new MongodbDocumentStore(mongo, "testdb");
    }

    @Autowired
    MongodbDocumentStore driver;

    public static final int port = findAvailableTcpPort();

    @Bean(initMethod = "start", destroyMethod = "stop")
    public MongodExecutable mongodExecutable() throws IOException {
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(V2_6_1)
                .net(new Net(port, localhostIsIPv6()))
                .build();
        return MongodStarter.getDefaultInstance().prepare(mongodConfig);
    }

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("spring.data.mongodb.port", port + "");
    }

    @Test
    public void shouldReturnEmptyList() {
        Map<String, Object> query = ImmutableMap.of("name", "someRandomName");
        Map<String, Object> queryBuilder = ImmutableMap.of("query", query);

        // When
        List<Map<String, Object>> people = driver.findByQuery(FindByQueryOperation.findByQueryOperation(Person.class, queryBuilder));

        // Then
        assertEquals(0, people.size());
    }

}

class Person {

    private String id;

    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}