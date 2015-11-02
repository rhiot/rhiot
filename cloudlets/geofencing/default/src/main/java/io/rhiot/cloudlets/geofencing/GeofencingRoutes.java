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
package io.rhiot.cloudlets.geofencing;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import static org.apache.camel.model.rest.RestBindingMode.off;

public class GeofencingRoutes extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        rest("/api/geofencing/routes").
                get("/clients").route().
                beanRef("routeService", "clients").transform().groovy("[clients: request.body]");

        rest("/api/geofencing/routes").
                get("/routes/{client}").route().
                transform().header("client").
                beanRef("routeService", "routes").transform().groovy("[routes: request.body]");

        rest("/api/geofencing/routes").
                delete("/delete/{routeId}").route().
                transform().header("routeId").
                beanRef("routeService", "deleteRoute").transform().groovy("[routes: request.body]");

        rest("/api/geofencing/routes").
                get("/routeUrl/{route}").route().
                transform().header("route").
                beanRef("routeService", "renderRouteUrl").transform().groovy("[routeUrl: request.body]");

        rest("/api/geofencing/routes").
                get("/export/{client}/{format}").bindingMode(off).route().
                transform().groovy("[request.headers.get('client'), request.headers.get('format')]").
                to("bean:routeService?method=exportRoutes&multiParameterArray=true").
                setHeader(Exchange.CONTENT_TYPE, constant("application/octet-stream")).
                setHeader("Content-Disposition", constant("attachment; filename=routes.xls"));

        from("timer:analyzeRoutes?period=60000&delay={{camellabs.iot.cloudlet.geofencing.routes.analysis.delay:15000}}").
                beanRef("routeService", "clients").split().body().parallelProcessing().
                beanRef("routeService", "analyzeRoutes");

    }

}