/*
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.camellabs.iot.cloudlet.geofencing;

import com.github.camellabs.iot.cloudlet.geofencing.service.DefaultRouteService;
import io.rhiot.cloudlets.geofencing.GeofencingRoutes;
import io.rhiot.datastream.document.mongodb.MongodbDocumentStore;
import io.rhiot.datastream.engine.DataStream;
import io.rhiot.steroids.camel.CamelBootInitializer;
import org.apache.camel.Exchange;
import org.apache.camel.impl.RouteService;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.spring.boot.FatJarRouter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.CustomConversions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.camel.model.rest.RestBindingMode.off;

@SpringBootApplication
@ComponentScan("com.github.camellabs.iot.cloudlet")
public class GeofencingCloudlet extends FatJarRouter {

    @Bean
    public CustomConversions customConversions() throws Exception {
        List<Converter<?, ?>> converterList = new ArrayList<Converter<?, ?>>();
        converterList.add(new Converter<Long, Date>() {
            @Override
            public Date convert(Long source) {
                return new Date(source);
            }
        });
        return new CustomConversions(converterList);
    }

    @Bean
    MongodbDocumentStore mongodbDocumentStore(DataStream dataStream) {
        return dataStream.beanRegistry().bean(MongodbDocumentStore.class).get();
    }

    @Bean
    String initalizer(DefaultRouteService routeService) throws Exception {
        CamelBootInitializer.registry().put("routeService", routeService);
        CamelBootInitializer.camelContext().addRoutes(new GeofencingRoutes());
        return "init";
    }

}