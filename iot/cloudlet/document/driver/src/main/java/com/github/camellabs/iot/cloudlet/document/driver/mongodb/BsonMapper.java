package com.github.camellabs.iot.cloudlet.document.driver.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.camel.TypeConverter;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BsonMapper {

    private final TypeConverter typeConverter;

    @Autowired
    public BsonMapper(TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
    }

    public static DBObject bsonToJson(DBObject bson) {
        DBObject json = new BasicDBObject(bson.toMap());
        Object id = json.get("_id");
        if (id != null) {
            json.removeField("_id");
            json.put("id", id.toString());
        }
        return json;
    }

    public static DBObject jsonToBson(DBObject json) {
        DBObject bson = new BasicDBObject(json.toMap());
        Object id = bson.get("id");
        if (id != null) {
            bson.removeField("id");
            bson.put("_id", new ObjectId(id.toString()));
        }
        return bson;
    }

    public DBObject pojoToBson(Object pojo) {
        return jsonToBson(typeConverter.convertTo(DBObject.class, pojo));
    }

}
