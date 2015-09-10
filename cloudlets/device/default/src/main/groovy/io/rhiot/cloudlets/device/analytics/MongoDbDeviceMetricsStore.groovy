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
package io.rhiot.cloudlets.device.analytics

import com.mongodb.BasicDBObject
import com.mongodb.Mongo
import io.rhiot.utils.Networks

import static io.rhiot.utils.Networks.isReachable
import static io.rhiot.utils.Networks.serviceHost
import static io.rhiot.utils.Networks.servicePort
import static io.rhiot.utils.Properties.intProperty
import static io.rhiot.utils.Properties.stringProperty

class MongoDbDeviceMetricsStore implements DeviceMetricsStore {

    Mongo mongo

    String db = 'DeviceCloudlet'

    String collection = 'DeviceMetrics'

    MongoDbDeviceMetricsStore() {
        mongo = new Mongo(serviceHost('mongodb'), servicePort('mongodb', 27017))
    }

    @Override
    void saveDeviceMetric(String deviceId, String metric, Object value) {
        mongo.getDB(db).getCollection(collection).save(new BasicDBObject([deviceId: deviceId, metric: metric, value: value, timestamp: new Date()]))
    }

    @Override
    def <T> T readDeviceMetric(String deviceId, String metric, Class<T> type) {
        def cursor = mongo.getDB(db).getCollection(collection).find(new BasicDBObject([deviceId: deviceId, metric: metric])).sort(new BasicDBObject("_id",-1)).limit(1)
        cursor.hasNext() ? cursor.next().value : null
    }

}