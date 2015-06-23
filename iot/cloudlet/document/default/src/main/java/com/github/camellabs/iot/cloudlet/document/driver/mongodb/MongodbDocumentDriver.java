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
package com.github.camellabs.iot.cloudlet.document.driver.mongodb;

import com.github.camellabs.iot.cloudlet.document.driver.spi.DocumentDriver;
import com.github.camellabs.iot.cloudlet.document.driver.spi.FindByQueryOperation;
import com.github.camellabs.iot.cloudlet.document.driver.spi.SaveOperation;
import com.mongodb.DBObject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.github.camellabs.iot.cloudlet.document.driver.mongodb.BsonMapper.bsonToJson;
import static com.github.camellabs.iot.cloudlet.document.driver.mongodb.MongoQueryBuilderProcessor.queryBuilder;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.apache.camel.component.mongodb.MongoDbConstants.COLLECTION;
import static org.apache.camel.component.mongodb.MongoDbConstants.LIMIT;
import static org.apache.camel.component.mongodb.MongoDbConstants.NUM_TO_SKIP;
import static org.apache.camel.component.mongodb.MongoDbConstants.OID;
import static org.apache.camel.component.mongodb.MongoDbConstants.SORT_BY;

@Component
public class MongodbDocumentDriver implements DocumentDriver {

    private final String documentsDbName;

    private final ProducerTemplate producerTemplate;

    private final BsonMapper bsonMapper;

    @Autowired
    public MongodbDocumentDriver(@Value("${camel.labs.iot.cloudlet.document.driver.mongodb.db}") String documentsDbName,
                                 ProducerTemplate producerTemplate,
                                 BsonMapper bsonMapper) {
        this.documentsDbName = documentsDbName;
        this.producerTemplate = producerTemplate;
        this.bsonMapper = bsonMapper;
    }

    @Override
    public String save(SaveOperation saveOperation) {
        return producerTemplate.request(baseMongoDbEndpoint() + "save",
                exchange -> {
                    exchange.getIn().setHeader(COLLECTION, saveOperation.collection());
                    exchange.getIn().setBody(bsonMapper.pojoToBson(saveOperation.pojo()));
                }).getIn().getHeader(OID, String.class);
    }

    @Override
    public <T> List<T> findByQuery(FindByQueryOperation<T> findByQueryOperation) {
        try {
            Exchange exc = producerTemplate.request(baseMongoDbEndpoint() + "findAll",
                    exchange -> {
                        exchange.getIn().setHeader(COLLECTION, findByQueryOperation.collection());
                        exchange.getIn().setHeader(LIMIT, findByQueryOperation.queryBuilder().get("size"));
                        exchange.getIn().setHeader(NUM_TO_SKIP, ((int) findByQueryOperation.queryBuilder().get("page")) * ((int) findByQueryOperation.queryBuilder().get("size")));
                        exchange.getIn().setHeader(SORT_BY, new MongoQueryBuilder().queryBuilderToSortConditions(findByQueryOperation.queryBuilder()));
                        exchange.getIn().setBody(findByQueryOperation.queryBuilder().get("query"));
                        queryBuilder().process(exchange);
                    });
            Object result = exc.getOut().getBody();
            if(result instanceof Iterable) {
                List<DBObject> documents =  newArrayList((Iterable) result);
                return (List<T>) documents.parallelStream().map(BsonMapper::bsonToJson).collect(toList());
            } else {
                result = bsonToJson((DBObject) result);
                return Arrays.asList((T)result);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String baseMongoDbEndpoint() {
        // TODO:CAMEL Collection should not be required for dynamic endpoints
        return format("mongodb:mongo?database=%s&collection=default&dynamicity=true&operation=", documentsDbName);
    }

}
