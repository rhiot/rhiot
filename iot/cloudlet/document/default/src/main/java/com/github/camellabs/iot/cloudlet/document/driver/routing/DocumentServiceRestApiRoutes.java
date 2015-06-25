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
package com.github.camellabs.iot.cloudlet.document.driver.routing;

import com.github.camellabs.iot.cloudlet.document.driver.mongodb.MongoQueryBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestConfigurationDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static java.lang.String.format;
import static org.apache.camel.component.mongodb.MongoDbConstants.COLLECTION;
import static org.apache.camel.model.rest.RestBindingMode.json;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static com.github.camellabs.iot.cloudlet.document.driver.mongodb.BsonMapperProcessor.mapBsonToJson;

@Component
public class DocumentServiceRestApiRoutes extends RouteBuilder {

    // Configuration members

    private final String documentsDbName;

    private final int restPort;

    private final boolean enableCors;

    private final int maxAttachmentSize;

    private final String apiEndpointOptions;

    // Constructors

    @Autowired
    public DocumentServiceRestApiRoutes(
            @Value("${camel.labs.iot.cloudlet.document.driver.mongodb.db}") String documentsDbName,
            @Value("${camel.labs.iot.cloudlet.rest.port:15001}") int restPort,
            @Value("${camel.labs.iot.cloudlet.rest.cors:true}") boolean enableCors,
            @Value("${camel.labs.iot.cloudlet.rest.attachment.size.max:26214400}") int maxAttachmentSize, // Default attachment size = 25 MB
            @Value("${camel.labs.iot.cloudlet.rest.endpoint.options:}") String apiEndpointOptions) {
        this.documentsDbName = documentsDbName;
        this.restPort = restPort;
        this.enableCors = enableCors;
        this.maxAttachmentSize = maxAttachmentSize;
        this.apiEndpointOptions = apiEndpointOptions;
    }

    // Routes

    @Override
    public void configure() throws Exception {

        // REST API facade

        RestConfigurationDefinition restConfiguration = restConfiguration().component("netty4-http").
                host("0.0.0.0").port(restPort).bindingMode(json).enableCORS(enableCors).
                endpointProperty("chunkedMaxContentLength", maxAttachmentSize + "").endpointProperty("matchOnUriPrefix", "true");
        if (isNotBlank(apiEndpointOptions)) {
            for (String apiEndpointOption : apiEndpointOptions.split(":")) {
                String[] splittedOption = apiEndpointOption.split("=");
                restConfiguration.endpointProperty(splittedOption[0], splittedOption[1]);
            }
        }

        rest("/api/document").
                post("/save/{collection}").type(Object.class).route().
                setBody().groovy("new com.github.camellabs.iot.cloudlet.document.driver.spi.SaveOperation(headers['collection'], body)").
                to("bean:mongodbDocumentDriver?method=save");

        rest("/api/document").
                get("/count/{collection}").route().
                setBody().groovy("new com.github.camellabs.iot.cloudlet.document.driver.routing.CountOperation(headers['collection'])").
                to("direct:count");

        rest("/api/document").
                get("/findOne/{collection}/{id}").route().
                setBody().groovy("new com.github.camellabs.iot.cloudlet.document.driver.routing.FindOneOperation(headers['collection'], headers['id'])").
                to("direct:findOne");

        rest("/api/document").
                post("/findMany/{collection}").route().
                setBody().groovy("new com.github.camellabs.iot.cloudlet.document.driver.routing.FindManyOperation(headers['collection'], body.ids)").
                to("direct:findMany");

        rest("/api/document").
                post("/findByQuery/{collection}").route().
                setBody().groovy("new com.github.camellabs.iot.cloudlet.document.driver.spi.FindByQueryOperation(headers['collection'], body)").
                to("bean:mongodbDocumentDriver?method=findByQuery");

        rest("/api/document")
                .verb("OPTIONS", "/").route()
                .setHeader("Access-Control-Allow-Origin", constant("*"))
                .setHeader("Access-Control-Allow-Methods", constant("GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, CONNECT, PATCH"))
                .setHeader("Access-Control-Allow-Headers", constant("Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers"))
                .setHeader("Allow", constant("GET, OPTIONS, POST, PATCH"));

        rest("/api/document").
                post("/countByQuery/{collection}").route().
                setBody().groovy("new com.github.camellabs.iot.cloudlet.document.driver.routing.CountByQueryOperation(headers['collection'], body)").
                to("direct:countByQuery");

        rest("/api/document").
                delete("/remove/{collection}/{id}").route().
                setBody().groovy("new com.github.camellabs.iot.cloudlet.document.driver.routing.RemoveOperation(headers['collection'], headers['id'])").
                to("direct:remove");

        // Operations handlers

        from("direct:findOne").
                setHeader(COLLECTION).groovy("body.collection").
                setBody().groovy("new org.bson.types.ObjectId(body.id)").
                to(baseMongoDbEndpoint() + "findById").
                process(mapBsonToJson());

        from("direct:findMany").
                setHeader(COLLECTION).groovy("body.collection").
                setBody().groovy("new com.mongodb.BasicDBObject('_id', new com.mongodb.BasicDBObject('$in', body.ids.collect{new org.bson.types.ObjectId(it)}))").
                to(baseMongoDbEndpoint() + "findAll").
                process(mapBsonToJson());

        from("direct:count").
                setHeader(COLLECTION).groovy("body.collection").
                setBody().constant(new BasicDBObject()).
                to(baseMongoDbEndpoint() + "count");

        from("direct:countByQuery").
                setHeader(COLLECTION).groovy("body.collection").
                setBody().groovy("body.queryBuilder.query").
                process(it -> it.getIn().setBody(new MongoQueryBuilder().jsonToMongoQuery(it.getIn().getBody(DBObject.class)))).
                to(baseMongoDbEndpoint() + "count");

        from("direct:remove").
                setHeader(COLLECTION).groovy("body.collection").
                setBody().groovy("new com.mongodb.BasicDBObject('_id', new org.bson.types.ObjectId(body.id))").
                to(baseMongoDbEndpoint() + "remove").
                process(mapBsonToJson());

    }

    // Helpers

    private String baseMongoDbEndpoint() {
        // TODO:CAMEL Collection should not be required for dynamic endpoints
        return format("mongodb:mongo?database=%s&collection=default&dynamicity=true&operation=", documentsDbName);
    }

}
