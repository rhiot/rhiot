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
package camel.labs.iot.cloudlet.document.driver.mongodb;

import com.github.camellabs.iot.cloudlet.document.driver.DriverDocumentCloudlet;
import com.github.camellabs.iot.cloudlet.document.sdk.DocumentService;
import com.github.camellabs.iot.cloudlet.document.sdk.QueryBuilder;
import com.github.camellabs.iot.cloudlet.document.sdk.RestDocumentService;
import io.rhiot.mongodb.EmbeddedMongo;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.component.netty4.http.NettyHttpEndpoint;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import camel.labs.iot.cloudlet.document.driver.mongodb.Invoice.Address;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import static com.github.camellabs.iot.cloudlet.document.sdk.Pojos.pojoClassToCollection;
import static com.github.camellabs.iot.cloudlet.document.sdk.QueryBuilder.buildQuery;
import static java.lang.Boolean.TRUE;
import static org.joda.time.DateTime.now;
import static org.springframework.util.SocketUtils.findAvailableTcpPort;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {DriverDocumentCloudlet.class, MongoDocumentServiceTestConfiguration.class})
@IntegrationTest("camel.labs.iot.cloudlet.rest.endpoint.options=connectTimeout=20000:requestTimeout=1000")
public class MongoDbDocumentServiceTest extends Assert {

    static EmbeddedMongo embeddedMongo = new EmbeddedMongo().start();

    @Autowired
    DocumentService<Invoice> documentService;

    @Autowired
    MongoTemplate mongo;

    Invoice invoice = new Invoice().invoiceId("invoice001");

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("server.port", findAvailableTcpPort() + "");
        System.setProperty("camel.labs.iot.cloudlet.rest.port", findAvailableTcpPort() + "");

        System.setProperty("camel.labs.iot.cloudlet.document.driver.mongodb.springbootconfig", TRUE.toString());
        System.setProperty("spring.data.mongodb.port", embeddedMongo.port() + "");
    }

    @Before
    public void before() {
        mongo.dropCollection(pojoClassToCollection(Invoice.class));
    }

    // Tests

    @Test
    public void shouldCreatePojo() {
        // When
        invoice = documentService.save(invoice);

        // Then
        Invoice loadedInvoice = documentService.findOne(Invoice.class, invoice.id);
        assertNotNull(loadedInvoice);
    }

    @Test
    public void shouldUpdatePojoWithAssignedId() {
        // Given
        invoice = documentService.save(invoice);

        // When
        documentService.save(invoice);

        // Then
        assertEquals(1, documentService.count(Invoice.class));
    }

    @Test
    public void shouldUpdateLoadedDocument() {
        // Given
        invoice = documentService.save(invoice);
        Invoice loadedInvoice = documentService.findOne(Invoice.class, invoice.id);

        // When
        documentService.save(loadedInvoice);

        // Then
        assertEquals(1, documentService.count(Invoice.class));
    }

    @Test
    public void shouldGenerateId() {
        // When
        invoice = documentService.save(invoice);

        // Then
        assertNotNull(invoice.id);
    }

    @Test
    public void shouldFindOne() {
        // Given
        invoice = documentService.save(invoice);

        // When
        Invoice invoiceFound = documentService.findOne(Invoice.class, invoice.id);

        // Then
        assertEquals(invoice.id, invoiceFound.id);
    }

    @Test
    public void shouldFindMany() {
        // Given
        Invoice firstInvoice = documentService.save(new Invoice().invoiceId("invoice001"));
        Invoice secondInvoice = documentService.save(new Invoice().invoiceId("invoice002"));

        // When
        List<Invoice> invoices = documentService.findMany(Invoice.class, firstInvoice.id, secondInvoice.id);

        // Then
        assertEquals(2, invoices.size());
        assertEquals(firstInvoice.id, invoices.get(0).id);
        assertEquals(secondInvoice.id, invoices.get(1).id);
    }

    @Test
    public void shouldNotFindMany() {
        // When
        List<Invoice> invoices = documentService.findMany(Invoice.class, ObjectId.get().toString(), ObjectId.get().toString());

        // Then
        assertEquals(0, invoices.size());
    }

    @Test
    public void shouldNotFindOne() {
        // When
        Invoice invoice = documentService.findOne(Invoice.class, ObjectId.get().toString());

        // Then
        assertNull(invoice);
    }

    @Test
    public void shouldCount() throws UnknownHostException, InterruptedException {
        // Given
        documentService.save(new Invoice().invoiceId("invoice001"));

        // When
        long invoices = documentService.count(Invoice.class);

        // Then
        assertEquals(1, invoices);
    }

    @Test
    public void shouldFindByQuery() {
        // Given
        documentService.save(invoice);
        InvoiceQuery query = new InvoiceQuery().invoiceId(invoice.invoiceId);

        // When
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices.size());
        assertEquals(invoice.invoiceId, invoices.get(0).invoiceId);
    }

    @Test
    public void shouldFindAllByQuery() {
        // Given
        documentService.save(new Invoice());
        documentService.save(new Invoice());

        // When
        InvoiceQuery query = new InvoiceQuery();
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(2, invoices.size());
    }

    @Test
    public void shouldNotFindByQuery() {
        // Given
        documentService.save(new Invoice().invoiceId("invoice001"));
        InvoiceQuery query = new InvoiceQuery().invoiceId("randomValue");

        // When
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(0, invoices.size());
    }

    @Test
    public void shouldFindByNestedQuery() {
        // Given
        String street = "someStreet";
        invoice.address = new Address().street(street);
        invoice = documentService.save(invoice);

        InvoiceQuery query = new InvoiceQuery().invoiceId(invoice.invoiceId).address_street(street);

        // When
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices.size());
        assertEquals(street, invoices.get(0).address.street);
    }

    @Test
    public void shouldNotFindByNestedQuery() {
        // Given
        String street = "someStreet";
        invoice.address = new Address().street(street);
        invoice = documentService.save(invoice);

        InvoiceQuery query = new InvoiceQuery().invoiceId(invoice.invoiceId).address_street("someRandomStreet");

        // When
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(0, invoices.size());
    }

    @Test
    public void shouldReturnPageByQuery() {
        // Given
        Invoice firstInvoice = documentService.save(new Invoice());
        Invoice secondInvoice = documentService.save(new Invoice());
        Invoice thirdInvoice = documentService.save(new Invoice());

        // When
        List<Invoice> firstPage = documentService.findByQuery(Invoice.class, buildQuery(new InvoiceQuery()).page(0).size(2));
        List<Invoice> secondPage = documentService.findByQuery(Invoice.class, buildQuery(new InvoiceQuery()).page(1).size(2));

        // Then
        assertEquals(2, firstPage.size());
        assertEquals(1, secondPage.size());
        assertEquals(firstInvoice.id, firstPage.get(0).id);
        assertEquals(secondInvoice.id, firstPage.get(1).id);
        assertEquals(thirdInvoice.id, secondPage.get(0).id);
    }

    @Test
    public void shouldSortDescending() {
        // Given
        Invoice firstInvoice = documentService.save(new Invoice().invoiceId("1"));
        Invoice secondInvoice = documentService.save(new Invoice().invoiceId("2"));
        Invoice thirdInvoice = documentService.save(new Invoice().invoiceId("3"));

        // When
        List<Invoice> firstPage = documentService.findByQuery(Invoice.class, buildQuery(
                new InvoiceQuery()).size(2).orderBy("invoiceId").sortAscending(false).page(0));
        List<Invoice> secondPage = documentService.findByQuery(Invoice.class, buildQuery(
                new InvoiceQuery()).size(2).orderBy("invoiceId").sortAscending(false).page(1));

        // Then
        assertEquals(2, firstPage.size());
        assertEquals(1, secondPage.size());
        assertEquals(thirdInvoice.id, firstPage.get(0).id);
        assertEquals(secondInvoice.id, firstPage.get(1).id);
        assertEquals(firstInvoice.id, secondPage.get(0).id);
    }

    @Test
    public void shouldFindByQueryWithContains() {
        // Given
        documentService.save(invoice);
        InvoiceQuery query = new InvoiceQuery();
        query.setInvoiceIdContains("voice");

        // When
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices.size());
        assertEquals(invoice.invoiceId, invoices.get(0).invoiceId);
    }

    @Test
    public void shouldNotFindByQueryWithContains() {
        // Given
        documentService.save(invoice);
        InvoiceQuery query = new InvoiceQuery();
        query.setInvoiceIdContains("randomString");

        // When
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(0, invoices.size());
    }

    @Test
    public void shouldFindByQueryWithIn() {
        // Given
        documentService.save(invoice);
        InvoiceQuery query = new InvoiceQuery().invoiceIdIn(invoice.invoiceId, "foo", "bar");

        // When
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices.size());
        assertEquals(invoice.invoiceId, invoices.get(0).invoiceId);
    }

    @Test
    public void shouldNotFindByQueryWithIn() {
        // Given
        documentService.save(invoice);
        InvoiceQuery query = new InvoiceQuery().invoiceIdIn("foo", "bar");

        // When
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(0, invoices.size());
    }

    @Test
    public void shouldFindByQueryWithNotIn() {
        // Given
        documentService.save(invoice);
        InvoiceQuery query = new InvoiceQuery().invoiceIdNotIn("foo", "bar");

        // When
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices.size());
        assertEquals(invoice.invoiceId, invoices.get(0).invoiceId);
    }

    @Test
    public void shouldNotFindByQueryWithNotIn() {
        // Given
        documentService.save(invoice);
        InvoiceQuery query = new InvoiceQuery().invoiceIdNotIn(invoice.invoiceId, "foo", "bar");

        // When
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(0, invoices.size());
    }

    @Test
    public void shouldFindByQueryBetweenDateRange() {
        // Given
        Invoice todayInvoice = documentService.save(invoice);
        invoice = new Invoice();
        invoice.timestamp = now().minusDays(2).toDate();
        documentService.save(invoice);
        invoice = new Invoice();
        invoice.timestamp = now().plusDays(2).toDate();
        documentService.save(invoice);

        InvoiceQuery query = new InvoiceQuery();
        query.setTimestampGreaterThanEqual(now().minusDays(1).toDate());
        query.setTimestampLessThan(now().plusDays(1).toDate());

        // When
        List<Invoice> invoices = documentService.findByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices.size());
        assertEquals(todayInvoice.id, invoices.get(0).id);
    }

    @Test
    public void shouldCountPositiveByQuery() {
        // Given
        documentService.save(invoice);
        InvoiceQuery query = new InvoiceQuery().invoiceId(invoice.invoiceId);

        // When
        long invoices = documentService.countByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices);
    }

    @Test
    public void shouldCountNegativeByQuery() {
        // Given
        documentService.save(invoice);
        InvoiceQuery query = new InvoiceQuery().invoiceId("randomValue");

        // When
        long invoices = documentService.countByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(0, invoices);
    }

    @Test
    public void shouldCountPositiveByQueryWithContains() {
        // Given
        documentService.save(invoice);
        InvoiceQuery query = new InvoiceQuery();
        query.setInvoiceIdContains("voice");

        // When
        long invoices = documentService.countByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(1, invoices);
    }

    @Test
    public void shouldCountNegativeByQueryWithContains() {
        // Given
        documentService.save(invoice);
        InvoiceQuery query = new InvoiceQuery();
        query.setInvoiceIdContains("randomString");

        // When
        long invoices = documentService.countByQuery(Invoice.class, new QueryBuilder(query));

        // Then
        assertEquals(0, invoices);
    }

    @Test
    public void shouldRemoveDocument() {
        // Given
        invoice = documentService.save(invoice);

        // When
        documentService.remove(Invoice.class, invoice.id);

        // Then
        long count = documentService.count(Invoice.class);
        assertEquals(0, count);
    }

}

@Configuration
class MongoDocumentServiceTestConfiguration {

    @Bean
    DocumentService documentService(@Value("${camel.labs.iot.cloudlet.rest.port}") int restApiPort) {
        return new RestDocumentService(restApiPort);
    }

}

class Invoice {

    public String id;

    public Date timestamp = new Date();

    public String invoiceId;

    public Address address;

    Invoice invoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
        return this;
    }

    static class Address {

        public String street;

        Address street(String street) {
            this.street = street;
            return this;
        }

    }

}

class InvoiceQuery {

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