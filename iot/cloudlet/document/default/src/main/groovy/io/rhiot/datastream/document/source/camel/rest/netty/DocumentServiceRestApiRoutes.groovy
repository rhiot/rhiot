/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rhiot.datastream.document.source.camel.rest.netty

import io.rhiot.steroids.bootstrap.Bootstrap;
import io.rhiot.steroids.bootstrap.BootstrapAware
import io.rhiot.steroids.camel.Route;
import io.rhiot.utils.Properties;
import io.vertx.core.Vertx;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestConfigurationDefinition;
import org.apache.camel.model.rest.RestPropertyDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.Collections.singletonList;
import static org.apache.camel.model.rest.RestBindingMode.json;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Route
public class DocumentServiceRestApiRoutes extends RouteBuilder implements BootstrapAware {

    // Configuration members

    private final int restPort;

    private final boolean enableCors;

    private final int maxAttachmentSize;

    private final String apiEndpointOptions;

    Bootstrap bootstrap;

    // Constructors

    @Autowired
    public DocumentServiceRestApiRoutes(
            int restPort,
            boolean enableCors,
            int maxAttachmentSize,
            String apiEndpointOptions) {
        this.restPort = restPort;
        this.enableCors = enableCors;
        this.maxAttachmentSize = maxAttachmentSize;
        this.apiEndpointOptions = apiEndpointOptions;
    }

    public DocumentServiceRestApiRoutes() {
        this(
                Properties.intProperty("camel.labs.iot.cloudlet.rest.port", 15001),
                Properties.booleanProperty("camel.labs.iot.cloudlet.rest.cors", true),
                Properties.intProperty("camel.labs.iot.cloudlet.rest.attachment.size.max", 26214400), // Default attachment size = 25 MB
                Properties.stringProperty("camel.labs.iot.cloudlet.rest.endpoint.options", "")
        );
    }

    // Routes

    @Override
    public void configure() throws Exception {
        def vertx = new VertxProducer(bootstrap.beanRegistry().bean(Vertx.class).get())

        // REST API facade

        RestPropertyDefinition corsAllowedHeaders = new RestPropertyDefinition();
        corsAllowedHeaders.setKey("Access-Control-Allow-Headers");
        corsAllowedHeaders.setValue("Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, Authorization");

        RestConfigurationDefinition restConfiguration = restConfiguration().component("netty4-http").
                host("0.0.0.0").port(restPort).bindingMode(json).enableCORS(enableCors).
                endpointProperty("chunkedMaxContentLength", maxAttachmentSize + "").endpointProperty("matchOnUriPrefix", "true");
        restConfiguration.setCorsHeaders(singletonList(corsAllowedHeaders));
        if (isNotBlank(apiEndpointOptions)) {
            for (String apiEndpointOption : apiEndpointOptions.split(":")) {
                String[] splittedOption = apiEndpointOption.split("=");
                restConfiguration.endpointProperty(splittedOption[0], splittedOption[1]);
            }
        }

        rest("/api/document").
                post("/save/{collection}").type(Object.class).route().
                setBody().groovy("io.rhiot.datastream.engine.JsonWithHeaders.jsonWithHeaders(body, [arg0: headers['collection'], operation: 'save'])").
                process(vertx)

        rest("/api/document").
                get("/count/{collection}").route().
                setBody().groovy("new io.rhiot.datastream.engine.JsonWithHeaders(null, [arg0 : headers['collection'], operation: 'count'])").
                process(vertx)

        rest("/api/document").
                get("/findOne/{collection}/{id}").route().
                setBody().groovy("new io.rhiot.datastream.engine.JsonWithHeaders(null, [operation: 'findOne', arg0 : headers['collection'], arg1: headers['id']])").
                process(vertx)

        rest("/api/document").
                post("/findMany/{collection}").route().
                setBody().groovy("io.rhiot.datastream.engine.JsonWithHeaders.jsonWithHeaders(body, [operation: 'findMany', arg0 : headers['collection']])").
                process(vertx)

        rest("/api/document").
                post("/findByQuery/{collection}").route().
                setBody().groovy("new io.rhiot.datastream.document.FindByQueryOperation(headers['collection'], body)").
                to("bean:mongodbDocumentStore?method=findByQuery");

        rest("/api/")
                .verb("OPTIONS", "/").route()
                .setHeader("Access-Control-Allow-Origin", constant("*"))
                .setHeader("Access-Control-Allow-Methods", constant("GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, CONNECT, PATCH"))
                .setHeader("Access-Control-Allow-Headers", constant("Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, Authorization"))
                .setHeader("Allow", constant("GET, OPTIONS, POST, PATCH"));

        rest("/api/document").
                post("/countByQuery/{collection}").route().
                setBody().groovy("new io.rhiot.datastream.document.CountByQueryOperation(headers['collection'], body)").
                to("bean:mongodbDocumentStore?method=countByQuery");

        rest("/api/document").
                delete("/remove/{collection}/{id}").route().
                setBody().groovy("new io.rhiot.datastream.document.RemoveOperation(headers['collection'], headers['id'])").
                to("bean:mongodbDocumentStore?method=remove").setBody().constant("");

    }

    @Override
    public void bootstrap(Bootstrap bootstrap) {
        this.bootstrap = bootstrap
    }

}
