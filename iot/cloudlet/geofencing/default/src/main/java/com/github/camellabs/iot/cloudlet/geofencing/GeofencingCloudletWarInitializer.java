package com.github.camellabs.iot.cloudlet.geofencing;

import org.apache.camel.spring.boot.FatJarRouter;
import org.apache.camel.spring.boot.FatWarInitializer;

public class GeofencingCloudletWarInitializer extends FatWarInitializer {

    @Override
    protected Class<? extends FatJarRouter> routerClass() {
        return GeofencingCloudlet.class;
    }

}
