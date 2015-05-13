package com.github.camellabs.iot.cloudlet.document.driver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

public class MongoDbDocumentServiceBoot extends SpringBootServletInitializer {

    public static void main(String... args) {
        new SpringApplication(MongoDbDocumentServiceConfiguration.class).run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MongoDbDocumentServiceConfiguration.class);
    }

}