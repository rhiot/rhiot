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

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import io.rhiot.thingsdata.CountByQueryOperation;
import io.rhiot.thingsdata.CountOperation;
import io.rhiot.thingsdata.DocumentDriver;
import io.rhiot.thingsdata.FindByQueryOperation;
import io.rhiot.thingsdata.FindOneOperation;
import io.rhiot.thingsdata.SaveOperation;
import org.apache.camel.ProducerTemplate;
import org.bson.BSONObject;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;

@Component
public class MongodbDocumentDriver implements DocumentDriver {

    private final String documentsDbName;

    @Autowired
    private Mongo mongo;

    @Autowired
    public MongodbDocumentDriver(@Value("${cloudlet.document.driver.mongodb.db}") String documentsDbName) {
        this.documentsDbName = documentsDbName;
    }

    @Override
    public String save(SaveOperation saveOperation) {
        DBObject mongoObject = canonicalToMongo(new BasicDBObject(saveOperation.pojo()));
        mongo.getDB(documentsDbName).getCollection(saveOperation.collection()).save(mongoObject);
        return mongoObject.get("_id").toString();
    }

    @Override
    public List<Map<String,Object>> findByQuery(FindByQueryOperation findByQueryOperation) {
        Map<String, Object> universalQuery = (Map<String, Object>) findByQueryOperation.queryBuilder().getOrDefault("query", emptyMap());
        DBObject mongoQuery = new MongoQueryBuilder().jsonToMongoQuery(new BasicDBObject(universalQuery));
        int skip = ((int) findByQueryOperation.queryBuilder().getOrDefault("page", 0)) * ((int) findByQueryOperation.queryBuilder().getOrDefault("size", 100));
        DBCursor results = mongo.getDB(documentsDbName).getCollection(findByQueryOperation.collection()).find(mongoQuery).
                limit((Integer) findByQueryOperation.queryBuilder().getOrDefault("size", 100)).skip(skip).sort(new MongoQueryBuilder().queryBuilderToSortConditions(findByQueryOperation.queryBuilder()));
        List<Map> mappedResults = results.toArray().parallelStream().map(BsonMapper::bsonToJson).map(BSONObject::toMap).collect(toList());
        List answer = mappedResults;
        return answer;
    }

    @Override
    public long countByQuery(CountByQueryOperation findByQueryOperation) {
        Map<String, Object> universalQuery = (Map<String, Object>) findByQueryOperation.queryBuilder().getOrDefault("query", emptyMap());
        DBObject mongoQuery = new MongoQueryBuilder().jsonToMongoQuery(new BasicDBObject(universalQuery));
        int skip = ((int) findByQueryOperation.queryBuilder().getOrDefault("page", 0)) * ((int) findByQueryOperation.queryBuilder().getOrDefault("size", 100));
        DBCursor results = mongo.getDB(documentsDbName).getCollection(findByQueryOperation.collection()).find(mongoQuery).
                limit((Integer) findByQueryOperation.queryBuilder().getOrDefault("size", 100)).skip(skip).sort(new MongoQueryBuilder().queryBuilderToSortConditions(findByQueryOperation.queryBuilder()));
        return results.count();
    }

    @Override
    public long count(CountOperation countOperation) {
        return mongo.getDB(documentsDbName).getCollection(countOperation.collection()).count();
    }

    @Override
    public Map<String, Object> findOne(FindOneOperation findOneOperation) {
        ObjectId id = new ObjectId(findOneOperation.id());
        DBObject document = mongo.getDB(documentsDbName).getCollection(findOneOperation.collection()).findOne(id);
        if(document == null) {
            return null;
        }
        return mongoToCanonical(document).toMap();
    }

    private String baseMongoDbEndpoint() {
        // TODO:CAMEL Collection should not be required for dynamic endpoints
        return format("mongodb:mongo?database=%s&collection=default&dynamicity=true&operation=", documentsDbName);
    }

    public static DBObject canonicalToMongo(DBObject json) {
        checkNotNull(json, "JSON passed to the conversion can't be null.");
        DBObject bson = new BasicDBObject(json.toMap());
        Object id = bson.get("id");
        if (id != null) {
            bson.removeField("id");
            bson.put("_id", new ObjectId(id.toString()));
        }
        return bson;
    }

    public static DBObject mongoToCanonical(DBObject bson) {
        checkNotNull(bson, "BSON passed to the conversion can't be null.");
//        LOG.debug("Converting BSON object to JSON: {}", bson);
        DBObject json = new BasicDBObject(bson.toMap());
        Object id = json.get("_id");
        if (id != null) {
            json.removeField("_id");
            json.put("id", id.toString());
        }
        return json;
    }

}
