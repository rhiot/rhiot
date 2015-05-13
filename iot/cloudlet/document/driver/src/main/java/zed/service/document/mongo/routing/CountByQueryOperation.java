package zed.service.document.mongo.routing;

import java.util.Map;

public class CountByQueryOperation {

    private final String collection;

    private final Map<String, Object> queryBuilder;

    public CountByQueryOperation(String collection, Map<String, Object> queryBuilder) {
        this.collection = collection;
        this.queryBuilder = queryBuilder;
    }

    public String collection() {
        return collection;
    }

    public Map<String, Object> queryBuilder() {
        return queryBuilder;
    }

}