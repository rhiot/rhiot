package com.github.camellabs.iot.gateway.heartbeat

import com.github.camellabs.iot.gateway.GatewayVerticle
import com.github.camellabs.iot.vertx.camel.GroovyCamelVerticle
import org.apache.camel.model.RouteDefinition

import static com.github.camellabs.iot.vertx.PropertyResolver.stringProperty
import static java.lang.System.currentTimeMillis
import static java.net.InetAddress.getLocalHost

@GatewayVerticle(conditionProperty = 'camellabs.iot.gateway.heartbeat.mqtt')
class MqttHeartbeatVerticle extends GroovyCamelVerticle {

    def topic = stringProperty('camellabs.iot.gateway.heartbeat.mqtt.topic', 'heartbeat')

    def brokerUrl = stringProperty('camellabs.iot.gateway.heartbeat.mqtt.broker.url')

    @Override
    void start() {
        super.start()
        fromEventBus('heartbeat') { RouteDefinition route ->
            route.transform().simple(generateHeartBeatMessage()).
                    to("paho:${topic}?brokerUrl=${brokerUrl}")
        }
    }

    // Private helpers

    private String generateHeartBeatMessage() {
        try {
            return getLocalHost().getHostName() + ":" + currentTimeMillis();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

}
