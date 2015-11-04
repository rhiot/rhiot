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
package com.github.camellabs.iot.cloudlet.document.driver;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoTimeoutException;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import io.rhiot.datastream.document.mongodb.MongodbDocumentStore;
import io.rhiot.datastream.engine.DataStream;
import io.rhiot.steroids.camel.CamelBootInitializer;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.apache.camel.spring.boot.FatJarRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.UnknownHostException;

import static de.flapdoodle.embed.mongo.distribution.Version.V2_6_1;
import static de.flapdoodle.embed.process.runtime.Network.localhostIsIPv6;
import static java.lang.Integer.parseInt;
import static java.lang.System.getenv;

@SpringBootApplication
public class DriverDocumentCloudlet extends FatJarRouter {

    static final Logger LOG = LoggerFactory.getLogger(DriverDocumentCloudlet.class);

    @ConditionalOnProperty(value = "camel.labs.iot.cloudlet.document.driver.mongodb.embedded", havingValue = "true")
    @Bean(initMethod = "start", destroyMethod = "stop")
    public MongodExecutable embeddedMongoDB(
            @Value("${camel.labs.iot.cloudlet.document.driver.mongodb.embedded.port:27017}") int mongodbPort
    ) throws IOException {
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(V2_6_1)
                .net(new Net(mongodbPort, localhostIsIPv6()))
                .build();
        return MongodStarter.getDefaultInstance().prepare(mongodConfig);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    DataStream dataStream() {
        return new DataStream();
    }

//    @Bean
//    MongodbDocumentStore mongodbDocumentStore(DataStream dataStream) {
//        return dataStream.beanRegistry().bean(MongodbDocumentStore.class).get();
//    }

    @Bean
    CamelContextConfiguration camelContextConfiguration(DataStream dataStream, Mongo mongo, @Value("${cloudlet.document.driver.mongodb.db}") String documentsDbName) {
        MongodbDocumentStore mongodbDocumentStore = dataStream.beanRegistry().bean(MongodbDocumentStore.class).get();

        ((SimpleRegistry) CamelBootInitializer.registry()).put("mongodbDocumentStore", mongodbDocumentStore);

        return new CamelContextConfiguration() {
            @Override
            public void beforeApplicationStart(CamelContext camelContext) {
//                Vertx vertx = dataStream.beanRegistry().bean(Vertx.class).get();
//                VertxComponent vertxComponent = new VertxComponent();
//                vertxComponent.setVertx(vertx);
//                camelContext.addComponent("vertx", vertxComponent);
            }
        };
    }

    @Bean
    @ConditionalOnProperty(value = "camel.labs.iot.cloudlet.document.driver.mongodb.springbootconfig", matchIfMissing = true, havingValue = "false")
    Mongo mongo() throws UnknownHostException {
        String mongodbKubernetesHost = System.getenv("MONGODB_SERVICE_HOST");
        String mongodbKubernetesPort = System.getenv("MONGODB_SERVICE_PORT");
        if (mongodbKubernetesHost != null && mongodbKubernetesPort != null) {
            LOG.info("Kubernetes MongoDB service detected - {}:{}. Connecting...", mongodbKubernetesHost, mongodbKubernetesPort);
            return new MongoClient(mongodbKubernetesHost, parseInt(mongodbKubernetesPort));
        } else {
            LOG.info("Can't find MongoDB Kubernetes service.");
            LOG.debug("Environment variables: {}", getenv());
        }

        try {
            LOG.info("Attempting to connect to the MongoDB server at mongodb:27017.");
            Mongo mongo = new MongoClient("mongodb");
            mongo.getDatabaseNames();
            return mongo;
        } catch (MongoTimeoutException e) {
            LOG.info("Can't connect to the MongoDB server at mongodb:27017. Falling back to the localhost:27017.");
            return new MongoClient();
        }
    }

    @Bean
    Filter corsFilter() {
        return new Filter() {

            public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
                HttpServletResponse response = (HttpServletResponse) res;
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
                response.setHeader("Access-Control-Max-Age", "3600");
                response.setHeader("Access-Control-Allow-Headers", "Authorization, x-requested-with");
                chain.doFilter(req, res);
            }

            public void init(FilterConfig filterConfig) {
            }

            public void destroy() {
            }

        };
    }

}