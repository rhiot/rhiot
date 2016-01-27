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

import com.google.common.collect.ImmutableMap
import io.rhiot.cloudplatform.runtime.spring.test.CloudPlatformTest
import org.junit.Test
import org.springframework.web.client.RestTemplate

import static com.google.common.truth.Truth.assertThat
import static io.rhiot.utils.Networks.findAvailableTcpPort
import static io.rhiot.utils.Properties.setBooleanProperty
import static io.rhiot.utils.Properties.setIntProperty
import static io.rhiot.utils.Uuids.uuid

class MongodbDocumentStoreTest extends CloudPlatformTest {

    static def httpPort = findAvailableTcpPort()

    def collection = uuid()

    def invoice = new Invoice(invoiceId: 'foo')

    @Override
    protected void beforeCloudPlatformStarted() {
        System.setProperty("spring.data.mongodb.port", findAvailableTcpPort() + '');
        setBooleanProperty('MQTT_ENABLED', false)
        setIntProperty('AMQP_PORT', findAvailableTcpPort())
        setIntProperty('rest.port', httpPort)
    }

    @Test
    void shouldCountInvoice() {
        // Given
        connector.toBus("document.save.${collection}", invoice)

        // When
        def count = connector.fromBus("document.count.${collection}", int.class)

        // Then
        assertThat(count).isEqualTo(1)
    }

    @Test
    public void shouldFindOne() {
        // Given
        def id = connector.fromBus("document.save.${collection}", invoice, String.class)

        // When
        def loadedInvoice = connector.fromBus("document.findOne.${collection}.${id}", Map.class)

        // Then
        assertThat(loadedInvoice).isNotNull()
    }

    @Test
    public void shouldUpdateDocument() {
        // Given
        def id = connector.fromBus("document.save.${collection}", invoice, String.class)
        invoice.id = id
        invoice.invoiceId = 'newValue'

        // When
        connector.toBus("document.save.${collection}", invoice)

        // Then
        def updatedInvoice = connector.fromBus("document.findOne.${collection}.${id}", Map.class)
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

    @Test
    public void shouldReturnEmptyList() {
        Map<String, Object> query = ImmutableMap.of("invoiceId", "someRandomName");
        Map<String, Object> queryBuilder = ImmutableMap.of("query", query);

        // When
        def invoices = payloadEncoding.decode(new RestTemplate().postForObject("http://localhost:${httpPort}/document/findByQuery/${collection}", payloadEncoding.encode(queryBuilder), byte[].class))

        // Then
        assertEquals(0, invoices.size());
    }

    @Test
    public void shouldFindByQuery() {
        // Given
        new RestTemplate().postForLocation("http://localhost:${httpPort}/document/save/${collection}", payloadEncoding.encode(invoice))
        def query = [query: new InvoiceQuery().invoiceId(invoice.invoiceId)]

        // When
        def invoices = payloadEncoding.decode(new RestTemplate().postForObject("http://localhost:${httpPort}/document/findByQuery/${collection}", payloadEncoding.encode(query), byte[].class))

        // Then
        assertEquals(1, invoices.size());
        assertEquals(invoice.invoiceId, invoices.get(0).invoiceId);
    }

    @Test
    public void shouldFindAllByQuery() {
        // Given
        new RestTemplate().postForLocation("http://localhost:${httpPort}/document/save/${collection}", payloadEncoding.encode(invoice))
        new RestTemplate().postForLocation("http://localhost:${httpPort}/document/save/${collection}", payloadEncoding.encode(invoice))

        // When
        def invoices = payloadEncoding.decode(new RestTemplate().postForObject("http://localhost:${httpPort}/document/findByQuery/${collection}", payloadEncoding.encode([query:[:]]), byte[].class))

        // Then
        assertEquals(2, invoices.size());
    }

    @Test
    public void shouldNotFindByQuery() {
        // Given
        invoice.invoiceId = 'invoice001'
        new RestTemplate().postForLocation("http://localhost:${httpPort}/document/save/${collection}", payloadEncoding.encode(invoice))
        InvoiceQuery query = new InvoiceQuery().invoiceId("randomValue");

        // When
        def invoices = payloadEncoding.decode(new RestTemplate().postForObject("http://localhost:${httpPort}/document/findByQuery/${collection}", payloadEncoding.encode([query: query]), byte[].class))

        // Then
        assertEquals(0, invoices.size());
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

    static class InvoiceQuery {

        private String invoiceId;

        private String invoiceIdContains;

        private String[] invoiceIdIn;

        private String[] invoiceIdNotIn;

        private Date timestampLessThan;

        private Date timestampGreaterThanEqual;

        private String address_street;

        public String getInvoiceId() {
            return invoiceId;
        }

        public void setInvoiceId(String invoiceId) {
            this.invoiceId = invoiceId;
        }

        public InvoiceQuery invoiceId(String invoiceId) {
            this.invoiceId = invoiceId;
            return this;
        }

        public String getInvoiceIdContains() {
            return invoiceIdContains;
        }

        public void setInvoiceIdContains(String invoiceIdLike) {
            this.invoiceIdContains = invoiceIdLike;
        }

        public String[] getInvoiceIdIn() {
            return invoiceIdIn;
        }

        public void setInvoiceIdIn(String[] invoiceIdIn) {
            this.invoiceIdIn = invoiceIdIn;
        }

        public InvoiceQuery invoiceIdIn(String... invoiceIdIn) {
            this.invoiceIdIn = invoiceIdIn;
            return this;
        }

        public String[] getInvoiceIdNotIn() {
            return invoiceIdNotIn;
        }

        public void setInvoiceIdNotIn(String[] invoiceIdNotIn) {
            this.invoiceIdNotIn = invoiceIdNotIn;
        }

        public InvoiceQuery invoiceIdNotIn(String... invoiceIdNotIn) {
            this.invoiceIdNotIn = invoiceIdNotIn;
            return this;
        }

        public Date getTimestampLessThan() {
            return timestampLessThan;
        }

        public void setTimestampLessThan(Date timestampLessThan) {
            this.timestampLessThan = timestampLessThan;
        }

        public Date getTimestampGreaterThanEqual() {
            return timestampGreaterThanEqual;
        }

        public void setTimestampGreaterThanEqual(Date timestampGreaterThanEqual) {
            this.timestampGreaterThanEqual = timestampGreaterThanEqual;
        }

        public String getAddress_street() {
            return address_street;
        }

        public void setAddress_street(String address_street) {
            this.address_street = address_street;
        }

        public InvoiceQuery address_street(String address_street) {
            this.setAddress_street(address_street);
            return this;
        }

    }

}
