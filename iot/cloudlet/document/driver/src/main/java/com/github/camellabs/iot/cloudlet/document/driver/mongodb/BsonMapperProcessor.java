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

    public static BsonMapperProcessor mapJsonToBson() {
        return new BsonMapperProcessor(false);
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
