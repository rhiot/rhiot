package com.github.camellabs.iot.cloudlet.document.driver.routing;

public class SaveOperation {

    private final String collection;

    private final Object pojo;

    public SaveOperation(String collection, Object pojo) {
        this.collection = collection;
        this.pojo = pojo;
    }

    public String collection() {
        return collection;
    }

    public Object pojo() {
        return pojo;
    }

}
