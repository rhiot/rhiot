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
package boot.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.LinkedList;
import java.util.List;

public class MongoDbEndpoint extends AbstractEndpoint<DBObject> {

    private final MongoTemplate mongo;

    public MongoDbEndpoint(MongoTemplate mongo) {
        super("mongodb", false, true);
        this.mongo = mongo;
    }

    @Override
    public DBObject invoke() {
        BasicDBObject result = new BasicDBObject();

        CommandResult serverStatus = mongo.executeCommand("{ serverStatus: 1 }");
        result.put("serverStatus", serverStatus);

        List<DBObject> slowQueries = new LinkedList<>();
        DBCursor slowQueriesCursor = mongo.getDb().getCollection("system.profile").find();
        while (slowQueriesCursor.hasNext()) {
            slowQueries.add(slowQueriesCursor.next());
        }
        result.put("slowQueries", slowQueries);

        return result;
    }

}
