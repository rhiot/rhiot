package com.github.camellabs.iot.cloudlet.document.driver.routing;

public class FindOneOperation {

    private final String collection;

    private final String id;

    public FindOneOperation(String collection, String id) {
        this.collection = collection;
        this.id = id;
    }

    public String collection() {
        return collection;
    }

    public String id() {
        return id;
    }

}
