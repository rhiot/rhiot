package io.rhiot.datastream.camel.rest

import org.apache.camel.ServiceStatus
import org.apache.camel.builder.RouteBuilder

class CamelRestEndpoint {

    static boolean started

    static void startCamelRestEndpoint(RouteBuilder routeBuilder) {
        if(!started) {
            routeBuilder.restConfiguration().component("netty4-http").
                    host("0.0.0.0").port(8080)
            if(routeBuilder.context.status == ServiceStatus.Started) {
                def components = routeBuilder.context.componentNames.collect {
                    [it, routeBuilder.context.getComponent(it)]
                }
                routeBuilder.context.stop()
                components.each { routeBuilder.context.addComponent(it[0], it[1]) }
                routeBuilder.context.start()
            }
            started = true
        }
    }

}
