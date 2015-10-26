/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.camellabs.iot.cloudlet.document.driver.mongodb;

import com.google.common.collect.ImmutableMap;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import io.rhiot.thingsdata.DocumentDriver;
import io.rhiot.thingsdata.FindByQueryOperation;
import io.rhiot.thingsdata.SaveOperation;
import org.apache.camel.ProducerTemplate;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
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
    public MongodbDocumentDriver(@Value("${cloudlet.document.driver.mongodb.db}") String documentsDbName,
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
    public List<Map<String, Object>> findByQuery(FindByQueryOperation findByQueryOperation) {
        Map<String, Object> universalQuery = (Map<String, Object>) findByQueryOperation.queryBuilder().getOrDefault("query", emptyMap());
        DBObject mongoQuery = new MongoQueryBuilder().jsonToMongoQuery(new BasicDBObject(universalQuery));
        Map<String, Object> headers = ImmutableMap.of(
                COLLECTION, findByQueryOperation.collection(),
                LIMIT, findByQueryOperation.queryBuilder().getOrDefault("size", 100),
                NUM_TO_SKIP, ((int) findByQueryOperation.queryBuilder().getOrDefault("page", 0)) * ((int) findByQueryOperation.queryBuilder().getOrDefault("size", 100)),
                SORT_BY, new MongoQueryBuilder().queryBuilderToSortConditions(findByQueryOperation.queryBuilder())
        );
        Object mongoOutput = producerTemplate.requestBodyAndHeaders(baseMongoDbEndpoint() + "findAll", mongoQuery, headers);

        List<DBObject> documents;
        if (mongoOutput == null) {
            return emptyList();
        } else if (mongoOutput instanceof Iterable) {
            documents = newArrayList((Iterable) mongoOutput);
        } else {
            documents = singletonList((DBObject) mongoOutput);
        }
        return documents.parallelStream().map(BsonMapper::bsonToJson).map(document -> (Map<String, Object>) document.toMap()).collect(toList());
    }

    private String baseMongoDbEndpoint() {
        // TODO:CAMEL Collection should not be required for dynamic endpoints
        return format("mongodb:mongo?database=%s&collection=default&dynamicity=true&operation=", documentsDbName);
    }

    public static DBObject jsonToBson(DBObject json) {
        checkNotNull(json, "JSON passed to the conversion can't be null.");
        DBObject bson = new BasicDBObject(json.toMap());
        Object id = bson.get("id");
        if (id != null) {
            bson.removeField("id");
            bson.put("_id", new ObjectId(id.toString()));
        }
        return bson;
    }

}
