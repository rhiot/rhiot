package com.github.camellabs.iot.gateway.app

import com.github.camellabs.iot.gateway.GatewayVerticle
import com.github.camellabs.iot.vertx.camel.GroovyCamelVerticle
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.spring.boot.FatJarRouter
import org.junit.Test

import static com.github.camellabs.iot.utils.Concurrency.runMainInNewThread
import static com.github.camellabs.iot.vertx.camel.CamelContextFactories.camelContext
import static java.util.concurrent.TimeUnit.MINUTES

class CustomCamelVerticleTest {

    static {
        runMainInNewThread(FatJarRouter.class)
    }

    @Test
    void shouldReceiveHeartbeat() {
        def mockEndpoint = camelContext().getEndpoint('mock:camelHeartbeatConsumer', MockEndpoint.class)
        mockEndpoint.setMinimumExpectedMessageCount(1)
        MockEndpoint.assertIsSatisfied(1, MINUTES, mockEndpoint)
    }

}

@GatewayVerticle
class HeartbeatConsumerVerticle extends GroovyCamelVerticle {

    @Override
    void start() {
        super.start()
        fromEventBus('heartbeat') { it.to('mock:camelHeartbeatConsumer') }
    }

}
