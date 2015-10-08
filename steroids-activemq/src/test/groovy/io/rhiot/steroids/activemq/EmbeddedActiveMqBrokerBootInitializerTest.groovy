/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rhiot.steroids.activemq

import io.rhiot.steroids.bootstrap.Bootstrap
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.junit.AfterClass
import org.junit.Test

import static io.rhiot.steroids.activemq.EmbeddedActiveMqBrokerBootInitializer.amqp
import static io.rhiot.steroids.activemq.EmbeddedActiveMqBrokerBootInitializer.amqpJmsBridge
import static io.rhiot.steroids.activemq.EmbeddedActiveMqBrokerBootInitializer.mqtt
import static io.rhiot.steroids.activemq.EmbeddedActiveMqBrokerBootInitializer.mqttJmsBridge
import static io.rhiot.steroids.camel.CamelBootInitializer.camelContext
import static io.rhiot.utils.Uuids.uuid

class EmbeddedActiveMqBrokerBootInitializerTest {

    def queue = uuid()

    def topic = uuid()

    def seda = "seda:${uuid()}"

    static def bootstrap = new Bootstrap().start()

    @AfterClass
    static void AfterClass() {
        bootstrap.stop()
    }

    // Tests

    @Test
    void shouldReadMessagesFromMqttTopic() {
        // Given
        camelContext().addRoutes(new RouteBuilder() {
            @Override
            void configure() {
                from(mqtt(queue)).to("mock:${queue}")
            }
        })
        def mock = camelContext().getEndpoint("mock:${queue}", MockEndpoint.class)
        mock.expectedBodiesReceived('foo')

        // When
        camelContext().createProducerTemplate().sendBody(mqtt(queue), 'foo')

        // Then
        mock.assertIsSatisfied()
    }

    @Test
    void shouldWriteMessagesToMqttTopic() {
        // Given
        camelContext().addRoutes(new RouteBuilder() {
            @Override
            void configure() {
                from('seda:test').to(mqtt(queue))

                from(mqtt(queue)).to("mock:${queue}")
            }
        })
        def mock = camelContext().getEndpoint("mock:${queue}", MockEndpoint.class)
        mock.expectedBodiesReceived('foo')

        // When
        camelContext().createProducerTemplate().sendBody('seda:test', 'foo')

        // Then
        mock.assertIsSatisfied()
    }

    @Test
    void shouldReadMessageFromMqttJmsBridgeTopic() {
        // Given
        camelContext().addRoutes(new RouteBuilder() {
            @Override
            void configure() {
                from(mqttJmsBridge(queue)).to("mock:${queue}")
            }
        })
        sleep(2000)
        def mock = camelContext().getEndpoint("mock:${queue}", MockEndpoint.class)
        mock.expectedBodiesReceived('foo')

        // When
        camelContext().createProducerTemplate().sendBody(mqtt(queue), 'foo')

        // Then
        mock.assertIsSatisfied()
    }

    @Test
    void shouldWriteMessageToMqttJmsBridgeTopic() {
        // Given
        camelContext().addRoutes(new RouteBuilder() {
            @Override
            void configure() {
                from('seda:test2').to(mqttJmsBridge(queue))

                from(mqtt(queue)).to("mock:${queue}")
            }
        })
        def mock = camelContext().getEndpoint("mock:${queue}", MockEndpoint.class)
        mock.expectedBodiesReceived('foo')

        // When
        camelContext().createProducerTemplate().sendBody('seda:test2', 'foo')

        // Then
        mock.assertIsSatisfied()
    }

    @Test
    void shouldReadMessagesFromAmqpQueue() {
        // Given
        def message = uuid()
        camelContext().addRoutes(new RouteBuilder() {
            @Override
            void configure() {
                from(amqp("queue:${queue}")).to("mock:${queue}")
            }
        })
        def mock = camelContext().getEndpoint("mock:${queue}", MockEndpoint.class)
        mock.expectedBodiesReceived(message)

        // When
        camelContext().createProducerTemplate().sendBody(amqp("queue:${queue}"), message)

        // Then
        mock.assertIsSatisfied()
    }

    @Test
    void shouldReadMessagesFromAmqpTopic() {
        // Given
        def message = uuid()
        camelContext().addRoutes(new RouteBuilder() {
            @Override
            void configure() {
                from(amqp("topic:${topic}")).to("mock:${topic}")
            }
        })
        def mock = camelContext().getEndpoint("mock:${topic}", MockEndpoint.class)
        mock.expectedBodiesReceived(message)

        // When
        camelContext().createProducerTemplate().sendBody(amqp("topic:${topic}"), message)

        // Then
        mock.assertIsSatisfied()
    }

    @Test
    void shouldWriteMessagesToAmqpQueue() {
        // Given
        camelContext().addRoutes(new RouteBuilder() {
            @Override
            void configure() {
                from("seda:${queue}").to(amqp("queue:${queue}"))

                from(amqp("queue:${queue}")).to("mock:${queue}")
            }
        })
        def mock = camelContext().getEndpoint("mock:${queue}", MockEndpoint.class)
        mock.expectedBodiesReceived('foo')

        // When
        camelContext().createProducerTemplate().sendBody("seda:${queue}", 'foo')

        // Then
        mock.assertIsSatisfied()
    }

    @Test
    void shouldWriteMessagesToAmqpTopic() {
        // Given
        camelContext().addRoutes(new RouteBuilder() {
            @Override
            void configure() {
                from("seda:${topic}").to(amqp("topic:${topic}"))

                from(amqp("topic:${topic}")).to("mock:${topic}")
            }
        })
        def mock = camelContext().getEndpoint("mock:${topic}", MockEndpoint.class)
        mock.expectedBodiesReceived('foo')

        // When
        camelContext().createProducerTemplate().sendBody("seda:${topic}", 'foo')

        // Then
        mock.assertIsSatisfied()
    }

    @Test
    void shouldReadMessageFromAmqpJmsBridgeTopic() {
        // Given
        camelContext().addRoutes(new RouteBuilder() {
            @Override
            void configure() {
                from(amqpJmsBridge(queue)).to("mock:${queue}")
            }
        })
        sleep(2000)
        def mock = camelContext().getEndpoint("mock:${queue}", MockEndpoint.class)
        mock.expectedBodiesReceived('foo')

        // When
        camelContext().createProducerTemplate().sendBody(amqp(queue), 'foo')

        // Then
        mock.assertIsSatisfied()
    }

    @Test
    void shouldWriteMessageToAmqpJmsBridgeTopic() {
        // Given
        camelContext().addRoutes(new RouteBuilder() {
            @Override
            void configure() {
                from(seda).to(amqpJmsBridge(queue))

                from(amqp(queue)).to("mock:${queue}")
            }
        })
        def mock = camelContext().getEndpoint("mock:${queue}", MockEndpoint.class)
        mock.expectedBodiesReceived('foo')

        // When
        camelContext().createProducerTemplate().sendBody(seda, 'foo')

        // Then
        mock.assertIsSatisfied()
    }

}