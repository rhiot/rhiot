package com.github.camellabs.iot.cloudlet.document.driver;

import org.apache.camel.spring.boot.FatJarRouter;
import org.apache.camel.spring.boot.FatWarInitializer;

public class DriverDocumentCloudletWarInitializer extends FatWarInitializer {

    @Override
    protected Class<? extends FatJarRouter> routerClass() {
        return DriverDocumentCloudlet.class;
    }

}
