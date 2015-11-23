package io.rhiot.datastream.spark

import io.rhiot.steroids.camel.Route
import io.vertx.core.json.Json
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.jms.JmsMessage

import static io.rhiot.steroids.activemq.EmbeddedActiveMqBrokerBootInitializer.amqpByPrefix

@Route
class CamelSparkStreamConsumer extends RouteBuilder {

    public static final String CHANNEL = 'spark'

    @Override
    void configure() {
        from(amqpByPrefix(CHANNEL)).
                process {
                    def channel = it.getIn(JmsMessage.class).jmsMessage.properties._to.toString()
                    def rawChannel = channel.substring(channel.indexOf('.') + 1)
                    def channelParts = rawChannel.split(/\./)
                    def rdd = channelParts[0]
                    def rddCallback = channelParts[1]
                    it.in.headers.target = "spark:rhiot?rdd=#${rdd}&rddCallback=#${rddCallback}"

                    it.in.body = Json.decodeValue(it.in.getBody(String.class), Map.class).input
                }.recipientList().header('target').
                transform{ Json.encode([result: it.in.body]) }
    }

}