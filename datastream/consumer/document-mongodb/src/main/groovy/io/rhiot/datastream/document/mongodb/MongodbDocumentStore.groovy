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
package io.rhiot.datastream.document.mongodb;

import com.mongodb.BasicDBObject
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo
import io.rhiot.cloudplatform.service.document.api.CountByQueryOperation
import io.rhiot.cloudplatform.service.document.api.DocumentStore;
import org.bson.types.ObjectId;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyMap;

public class MongodbDocumentStore implements DocumentStore {

    private static final def MONGO_ID = '_id'

    private final String documentsDbName

    private final Mongo mongo

    MongodbDocumentStore(Mongo mongo, String documentsDbName) {
        this.mongo = mongo
        this.documentsDbName = documentsDbName
    }

    @Override
    String save(String documentCollection, Map<String, Object> pojo) {
        def document = canonicalToMongo(new BasicDBObject(pojo))
        collection(documentCollection).save(document)
        document.get(MONGO_ID).toString()
    }

    @Override
    long count(String documentCollection) {
        collection(documentCollection).count()
    }

    @Override
    List<Map<String,Object>> findByQuery(String documentCollection, Map<String, Object> queryBuilder) {
        def universalQuery = (Map<String, Object>) queryBuilder.getOrDefault("query", emptyMap());
        DBObject mongoQuery = new MongoQueryBuilder().jsonToMongoQuery(new BasicDBObject(universalQuery));
        int skip = ((int) queryBuilder.getOrDefault("page", 0)) * ((int) queryBuilder.getOrDefault("size", 100));
        DBCursor results = collection(documentCollection).find(mongoQuery).
                limit((Integer) queryBuilder.getOrDefault("size", 100)).skip(skip).sort(new MongoQueryBuilder().queryBuilderToSortConditions(queryBuilder));
        results.toArray().collect{ mongoToCanonical(it).toMap() }
    }

    @Override
    public long countByQuery(CountByQueryOperation findByQueryOperation) {
        Map<String, Object> universalQuery = (Map<String, Object>) findByQueryOperation.query.getOrDefault("query", emptyMap());
        DBObject mongoQuery = new MongoQueryBuilder().jsonToMongoQuery(new BasicDBObject(universalQuery));
        int skip = ((int) findByQueryOperation.query.getOrDefault("page", 0)) * ((int) findByQueryOperation.query.getOrDefault("size", 100));
        DBCursor results = collection(findByQueryOperation.collection).find(mongoQuery).
                limit((Integer) findByQueryOperation.query.getOrDefault("size", 100)).skip(skip).sort(new MongoQueryBuilder().queryBuilderToSortConditions(findByQueryOperation.query));
        return results.count();
    }

    @Override
    public Map<String, Object> findOne(String documentCollection, String documentId) {
        ObjectId id = new ObjectId(documentId);
        DBObject document = collection(documentCollection).findOne(id);
        if(document == null) {
            return null;
        }
        return mongoToCanonical(document).toMap();
    }

    @Override
    List<Map<String, Object>> findMany(String documentCollection, List<String> ids) {
        def mongoIds = new BasicDBObject('$in', ids.collect{new ObjectId(it)})
        def query = new BasicDBObject('_id', mongoIds)
        collection(documentCollection).find(query).toArray().collect { mongoToCanonical(it).toMap() }
    }

    @Override
    void remove(String documentCollection, String identifier) {
        collection(documentCollection).remove(new BasicDBObject(MONGO_ID, new ObjectId(identifier)))
    }

    // Helpers

    private DBCollection collection(String collection) {
        mongo.getDB(documentsDbName).getCollection(collection)
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
