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
