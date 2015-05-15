package com.github.camellabs.iot.cloudlet.geofencing;

import com.google.common.collect.ImmutableSet;
import com.mongodb.Mongo;
import org.apache.camel.spring.boot.FatJarRouter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

@SpringBootApplication
@ComponentScan("com.github.camellabs.iot.cloudlet")
public class GeofencingCloudlet extends FatJarRouter {

    @Bean
    public CustomConversions customConversions() throws Exception {
        List<Converter<?, ?>> converterList = new ArrayList<Converter<?, ?>>();
        converterList.add(new Converter<Long,Date>() {
            @Override
            public Date convert(Long source) {
                return new Date(source);
            }
        });
        return new CustomConversions(converterList);
    }


//    @Bean
//    AbstractMongoConfiguration xxx(Mongo mongo, @Value("${camel.labs.iot.cloudlet.document.driver.mongodb.db:cloudlet_document}") String documentsDbName) {
//        return new AbstractMongoConfiguration(){
//            @Override
//            protected String getDatabaseName() {
//                return documentsDbName;
//            }
//
//            @Override
//            public Mongo mongo() throws Exception {
//                return mongo;
//            }
//        };
//    }



//    String xxx(MongoTemplate mongoTemplate) {
//        Converter<Long,Date> x = new Converter<Long,Date>() {
//            @Override
//            public Date convert(Long source) {
//                return new Date(source);
//            }
//        };
//        ((MappingMongoConverter)mongoTemplate.getConverter()).setCustomConversions(new CustomConversions(Arrays.asList(x)));
//        return "xxx";
//    }


}