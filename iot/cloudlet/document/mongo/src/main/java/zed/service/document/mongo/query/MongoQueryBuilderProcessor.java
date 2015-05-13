package zed.service.document.mongo.query;

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