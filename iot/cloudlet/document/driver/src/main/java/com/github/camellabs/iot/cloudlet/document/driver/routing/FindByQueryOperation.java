package com.github.camellabs.iot.cloudlet.document.driver.routing;

import java.util.Map;

public class FindByQueryOperation {

    private final String collection;

    private final Map<String, Object> queryBuilder;

    public FindByQueryOperation(String collection, Map<String, Object> queryBuilder) {
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