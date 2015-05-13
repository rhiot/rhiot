package com.github.camellabs.iot.cloudlet.document.driver.routing;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class FindManyOperation {

    private final String collection;

    private final List<String> ids;

    public FindManyOperation(String collection, List<String> ids) {
        this.collection = collection;
        this.ids = ImmutableList.copyOf(ids);
    }

    public String collection() {
        return collection;
    }

    public List<String> ids() {
        return ids;
    }

}
