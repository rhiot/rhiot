package io.rhiot.datastream.camel.rest

import org.apache.camel.CamelContext
import org.apache.camel.ServiceStatus
import org.apache.camel.builder.RouteBuilder

class CamelRestEndpoint {

    static boolean started

    static void startCamelRestEndpoint(CamelContext camelContext, RouteBuilder routeBuilder) {
        if(!started) {
            routeBuilder.restConfiguration().component("netty4-http").
                    host("0.0.0.0").port(8080)
            if(camelContext.status == ServiceStatus.Started) {
                def components = camelContext.componentNames.collect {
                    [it, camelContext.getComponent(it)]
                }
                camelContext.stop()
                components.each { camelContext.addComponent(it[0], it[1]) }
                camelContext.start()
            }
            started = true
        }
    }

}
