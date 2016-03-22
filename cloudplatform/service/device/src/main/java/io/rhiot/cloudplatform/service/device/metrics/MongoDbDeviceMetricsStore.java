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
package io.rhiot.cloudplatform.service.device.metrics;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import io.rhiot.cloudplatform.connector.IoTConnector;
import io.rhiot.cloudplatform.service.device.api.DeviceRegistry;

import java.util.Date;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static java.util.stream.Collectors.toMap;

public class MongoDbDeviceMetricsStore extends PollingDeviceMetricsStore {

    public static final String FIELD_DEVICE_ID = "deviceId";

    public static final String FIELD_METRIC = "metric";


    private final Mongo mongo;

    private final String db;

    private final String collection;

    // Constructors


    public MongoDbDeviceMetricsStore(IoTConnector connector, DeviceRegistry deviceRegistry, Mongo mongo, String db, String collection) {
        super(connector, deviceRegistry);
        this.mongo = mongo;
        this.db = db;
        this.collection = collection;
    }

    // Realizations

    @Override
    public void write(String deviceId, String metric, Object value) {
        BasicDBObject metricRecord = new BasicDBObject(of(
                FIELD_DEVICE_ID, deviceId, FIELD_METRIC, metric, "value", value, "timestamp", new Date()
        ));
        mongo.getDB(db).getCollection(collection).save(metricRecord);
    }

    @Override
    public Map<String, Object> readAll(String deviceId) {
        BasicDBObject metricsQuery = new BasicDBObject();
        metricsQuery.put(FIELD_DEVICE_ID, deviceId);
        return mongo.getDB(db).getCollection(collection).find(metricsQuery).toArray().stream().collect(
                toMap(metric -> (String )metric.get(FIELD_METRIC), metric -> metric.get("value"))
        );
    }

    @Override
    public Object doRead(String deviceId, String metric) {
        BasicDBObject metricQuery = new BasicDBObject(of(FIELD_DEVICE_ID, deviceId, FIELD_METRIC, metric));
        DBCursor cursor = mongo.getDB(db).getCollection(collection).find(metricQuery).sort(new BasicDBObject("_id",-1)).limit(1);
        return cursor.hasNext() ? cursor.next().get("value") : null;
    }

}