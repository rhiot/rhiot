package com.github.camellabs.iot.gateway.app;

import com.github.camellabs.iot.gateway.GatewayVerticle;
import com.github.camellabs.iot.gateway.Gateway;
import com.github.camellabs.iot.vertx.camel.JavaCamelVerticle;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.github.camellabs.iot.vertx.camel.CamelContextFactories.camelContext;
import static java.util.concurrent.TimeUnit.MINUTES;

public class CustomJavaCamelVerticleTest {

    static Gateway vertxGateway = new Gateway();

    @BeforeClass
    public static void beforeClass() {
        vertxGateway.start();
    }

    @AfterClass
    public static void afterClass() {
        vertxGateway.stop();
    }

    @Test
    public void shouldReceiveHeartbeat() throws InterruptedException {
        // Given
        MockEndpoint mockEndpoint = camelContext().getEndpoint("mock:customJava", MockEndpoint.class);
        mockEndpoint.setMinimumExpectedMessageCount(1);

        // Then
        MockEndpoint.assertIsSatisfied(1, MINUTES, mockEndpoint);
    }

}

@GatewayVerticle
class CustomJavaHeartbeatConsumerVerticle extends JavaCamelVerticle {

    @Override
    public void start() throws Exception {
        fromEventBus("heartbeat", route -> route.to("mock:customJava"));
    }

}