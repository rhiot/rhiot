package com.github.camellabs.iot.cloudlet.document.sdk;

public final class Pojos {

    private Pojos() {
    }

    public static String pojoClassToCollection(Class<?> pojoClass) {
        return pojoClass.getSimpleName();
    }

}
