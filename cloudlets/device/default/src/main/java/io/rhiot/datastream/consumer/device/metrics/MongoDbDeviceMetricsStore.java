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
package io.rhiot.datastream.consumer.device.metrics;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;

import java.util.Date;

public class MongoDbDeviceMetricsStore implements DeviceMetricsStore {

    private final Mongo mongo;

    private final String db;

    private final String collection;

    public MongoDbDeviceMetricsStore(Mongo mongo, String db, String collection) {
        this.mongo = mongo;
        this.db = db;
        this.collection = collection;
    }

    public MongoDbDeviceMetricsStore(Mongo mongo) {
        this(mongo, "rhiot", "DeviceMetrics");
    }

    @Override
    public void write(String deviceId, String metric, Object value) {
        BasicDBObject metricRecord = new BasicDBObject();
        metricRecord.put("deviceId", deviceId);
        metricRecord.put("metric", metric);
        metricRecord.put("value", value);
        metricRecord.put("timestamp", new Date());
        mongo.getDB(db).getCollection(collection).save(metricRecord);
    }

    @Override
    public Object read(String deviceId, String metric) {
        BasicDBObject metricQuery = new BasicDBObject();
        metricQuery.put("deviceId", deviceId);
        metricQuery.put("metric", metric);
        DBCursor cursor = mongo.getDB(db).getCollection(collection).find(metricQuery).sort(new BasicDBObject("_id",-1)).limit(1);
        return cursor.hasNext() ? cursor.next().get("value") : null;
    }

}