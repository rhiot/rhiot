package io.rhiot.steroids.activemq

import io.rhiot.steroids.camel.Route
import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.camel.builder.RouteBuilder
import org.springframework.jms.connection.CachingConnectionFactory

import static io.rhiot.steroids.activemq.EmbeddedActiveMqBrokerBootInitializer.DEFAULT_BROKER_NAME
import static io.rhiot.steroids.camel.CamelBootInitializer.eventBus
import static io.rhiot.steroids.camel.CamelBootInitializer.registry

@Route
class EmbeddedActiveMqCamelRoutes extends RouteBuilder {

    @Override
    void configure() {
        def jmsConnectionFactory = new CachingConnectionFactory(new ActiveMQConnectionFactory("vm:${DEFAULT_BROKER_NAME}"))
        registry().put('jmsConnectionFactory', jmsConnectionFactory)
        from('jms:topic:>?connectionFactory=#jmsConnectionFactory').to(mqttEventBus())
    }

    static String mqttEventBus() {
        eventBus('mqtt')
    }

}
