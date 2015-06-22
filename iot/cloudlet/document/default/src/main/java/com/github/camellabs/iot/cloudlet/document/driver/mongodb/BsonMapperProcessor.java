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
package com.github.camellabs.iot.cloudlet.document.driver.mongodb;

import com.google.common.collect.Lists;
import com.mongodb.DBObject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.List;

import static com.github.camellabs.iot.cloudlet.document.driver.mongodb.BsonMapper.bsonToJson;
import static com.github.camellabs.iot.cloudlet.document.driver.mongodb.BsonMapper.jsonToBson;

public class BsonMapperProcessor implements Processor {

    private final boolean bsonToJson;

    public BsonMapperProcessor(boolean bsonToJson) {
        this.bsonToJson = bsonToJson;
    }

    public static BsonMapperProcessor mapBsonToJson() {
        return new BsonMapperProcessor(true);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Object rawBody = exchange.getIn().getBody();
        if (!(rawBody instanceof Iterable)) {
            DBObject inDocument = exchange.getIn().getBody(DBObject.class);
            if (inDocument != null) {
                DBObject json = map(inDocument);
                exchange.getIn().setBody(json);
            }
        } else {
            List<DBObject> bsons = exchange.getIn().getBody(List.class);
            if (bsons != null) {
                List<DBObject> resultBsons = Lists.newArrayListWithExpectedSize(bsons.size());
                for (DBObject bs : bsons) {
                    resultBsons.add(map(bs));
                }
                exchange.getIn().setBody(resultBsons);
            }
        }
    }

    private DBObject map(DBObject dbObject) {
        return bsonToJson ? bsonToJson(dbObject) : jsonToBson(dbObject);
    }

}
