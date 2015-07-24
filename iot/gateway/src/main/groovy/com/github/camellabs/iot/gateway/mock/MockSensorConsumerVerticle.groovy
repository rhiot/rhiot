package com.github.camellabs.iot.gateway.mock

import com.github.camellabs.iot.gateway.GatewayVerticle
import com.github.camellabs.iot.vertx.PropertyResolver
import com.github.camellabs.iot.vertx.camel.GroovyCamelVerticle
import org.apache.camel.builder.RouteBuilder

import static com.github.camellabs.iot.vertx.PropertyResolver.intProperty
import static com.github.camellabs.iot.vertx.PropertyResolver.stringProperty

@GatewayVerticle(conditionProperty = 'camellabs_iot_gateway_mock_sensor_consumer')
class MockSensorConsumerVerticle extends GroovyCamelVerticle {

    def consumers = intProperty('camellabs_iot_gateway_mock_sensor_consumer_number', 15)

    def target = stringProperty('camellabs_iot_gateway_mock_sensor_consumer_target')

    def mqttQos = intProperty('camellabs_iot_gateway_mock_sensor_consumer_mqtt_qos', 0)

    def mqttBrokerUrl = stringProperty('camellabs_iot_gateway_mock_sensor_consumer_mqtt_broker_url')

    @Override
    void start() {
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            void configure() {
                from("seda:mockSensor?concurrentConsumers=${consumers}").
                        routeId("mockSensorConsumer").
                        to("${target ?: "paho:mock?qos=${mqttQos}&brokerUrl=${mqttBrokerUrl}"}");
            }
        })
    }
}
