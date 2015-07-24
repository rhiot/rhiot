package com.github.camellabs.iot.gateway.mock

import com.github.camellabs.iot.gateway.GatewayVerticle
import com.github.camellabs.iot.vertx.camel.GroovyCamelVerticle
import org.apache.camel.builder.RouteBuilder

import static com.github.camellabs.iot.vertx.PropertyResolver.intProperty

@GatewayVerticle(conditionProperty = 'camellabs_iot_gateway_mock_sensor')
class MockSensorVerticle extends GroovyCamelVerticle {

    def sourcesNumber = intProperty('camellabs_iot_gateway_mock_sensor_number', 10)

    def period = intProperty('camellabs_iot_gateway_mock_sensor_period', 10)

    @Override
    void start() {
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            void configure()  {
                for (int i = 1; i <= sourcesNumber; i++) {
                    String sourceId = "mockSensor-" + i;
                    from("timer:" + sourceId + "?period=${period}").routeId(sourceId).
                            transform().groovy("UUID.randomUUID().toString()").
                            to("seda:mockSensor?blockWhenFull=true");
                }
            }
        })
    }

}
