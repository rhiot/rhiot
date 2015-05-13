package com.github.camellabs.iot.cloudlet.document.driver.routing;

public class CountOperation {

    private final String collection;

    public CountOperation(String collection) {
        this.collection = collection;
    }

    public String collection() {
        return collection;
    }

}