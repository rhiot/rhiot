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
package io.rhiot.datastream.document.mongodb

import io.rhiot.datastream.engine.test.DataStreamTest
import io.rhiot.mongodb.EmbeddedMongo
import org.junit.Test
import org.springframework.web.client.RestTemplate

import static com.google.common.truth.Truth.assertThat
import static io.rhiot.utils.Networks.findAvailableTcpPort
import static io.rhiot.utils.Properties.setBooleanProperty
import static io.rhiot.utils.Properties.setIntProperty
import static io.rhiot.utils.Uuids.uuid

class MongodbDocumentStoreTest extends DataStreamTest {

    static mongo = new EmbeddedMongo().start()

    static def httpPort = findAvailableTcpPort()

    def collection = uuid()

    def invoice = new Invoice(invoiceId: 'foo')

    @Override
    protected void beforeDataStreamStarted() {
        setBooleanProperty('MQTT_ENABLED', false)
        setIntProperty('AMQP_PORT', findAvailableTcpPort())
        setIntProperty('http_port', httpPort)
    }

    @Test
    void shouldCountInvoice() {
        // Given
        toBus("document.save.${collection}", invoice)

        // When
        def count = fromBus("document.count.${collection}", int.class)

        // Then
        assertThat(count).isEqualTo(1)
    }

    @Test
    public void shouldFindOne() {
        // Given
        def id = fromBus("document.save.${collection}", invoice, String.class)

        // When
        def loadedInvoice = fromBus("document.findOne.${collection}.${id}", Map.class)

        // Then
        assertThat(loadedInvoice).isNotNull()
    }

    @Test
    public void shouldUpdateDocument() {
        // Given
        def id = fromBus("document.save.${collection}", invoice, String.class)
        invoice.id = id
        invoice.invoiceId = 'newValue'

        // When
        toBus("document.save.${collection}", invoice)

        // Then
        def updatedInvoice = fromBus("document.findOne.${collection}.${id}", Map.class)
        assertThat(updatedInvoice.invoiceId).isEqualTo(invoice.invoiceId)
    }

    // REST tests

    @Test
    void rest_shouldCountInvoice() {
        // Given
        new RestTemplate().postForLocation("http://localhost:${httpPort}/document/save/${collection}", payloadEncoding.encode(invoice))

        // When
        def count = payloadEncoding.decode(new RestTemplate().getForObject("http://localhost:${httpPort}/document/count/${collection}", byte[].class))

        // Then
        assertThat(count).isEqualTo(1)
    }

    @Test
    public void rest_shouldFindOne() {
        // Given
        def id = payloadEncoding.decode(new RestTemplate().postForObject("http://localhost:${httpPort}/document/save/${collection}", payloadEncoding.encode(invoice), byte[].class))

        // When
        def loadedInvoice = payloadEncoding.decode(new RestTemplate().getForObject("http://localhost:${httpPort}/document/findOne/${collection}/${id}", byte[].class))

        // Then
        assertThat(loadedInvoice).isNotNull()
    }

    @Test
    public void rest_shouldUpdateDocument() {
        // Given
        def id = payloadEncoding.decode(new RestTemplate().postForObject("http://localhost:${httpPort}/document/save/${collection}", payloadEncoding.encode(invoice), byte[].class))
        invoice.id = id
        invoice.invoiceId = 'newValue'

        // When
        new RestTemplate().postForLocation("http://localhost:${httpPort}/document/save/${collection}", payloadEncoding.encode(invoice))

        // Then
        def updatedInvoice = payloadEncoding.decode(new RestTemplate().getForObject("http://localhost:${httpPort}/document/findOne/${collection}/${id}", byte[].class))
        assertThat(updatedInvoice.invoiceId).isEqualTo(invoice.invoiceId)
    }


    // Class fixtures

    static class Invoice {

        String id

        Date timestamp = new Date()

        String invoiceId

        Address address

        static class Address {

            String street

        }

    }

}