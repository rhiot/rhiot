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

import com.mongodb.DBObject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoQueryBuilderProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(MongoQueryBuilderProcessor.class);

    private final MongoQueryBuilder mongoQueryBuilder;

    public MongoQueryBuilderProcessor(MongoQueryBuilder mongoQueryBuilder) {
        this.mongoQueryBuilder = mongoQueryBuilder;
    }

    public MongoQueryBuilderProcessor() {
        this(new MongoQueryBuilder());
    }

    public static MongoQueryBuilderProcessor queryBuilder() {
        return new MongoQueryBuilderProcessor();
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        DBObject jsonQuery = exchange.getIn().getMandatoryBody(DBObject.class);
        LOG.debug("Received JSON query: {}", jsonQuery);
        DBObject mongoDbQuery = mongoQueryBuilder.jsonToMongoQuery(jsonQuery);
        LOG.debug("MongoDb query after conversion: {}", mongoDbQuery);
        exchange.getIn().setBody(mongoDbQuery);
    }

}