package zed.service.document.mongo;

import com.github.camellabs.iot.cloudlet.document.driver.MongoDbDocumentServiceConfiguration;
import com.mongodb.Mongo;
import com.mongodb.MongoTimeoutException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static java.lang.Boolean.TRUE;
import static org.springframework.util.SocketUtils.findAvailableTcpPort;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {MongoDbDocumentServiceConfiguration.class, EmbeddedMongoDbTest.class})
@IntegrationTest("zed.service.document.mongodb.embedded=true")
public class EmbeddedMongoDbTest extends Assert {

    // Collaborator fixtures

    @Autowired
    Mongo mongo;

    // Configuration fixtures

    @BeforeClass
    public static void beforeClass() {
        int mongodbPort = findAvailableTcpPort();
        System.setProperty("zed.service.document.mongodb.embedded.port", mongodbPort + "");
        System.setProperty("zed.service.document.mongodb.springbootconfig", TRUE.toString());
        System.setProperty("spring.data.mongodb.port", mongodbPort + "");

        System.setProperty("server.port", findAvailableTcpPort() + "");
        System.setProperty("zed.service.api.port", findAvailableTcpPort() + "");
    }

    // Tests

    @Test
    public void shouldStartEmbeddedMongodb() {
        try {
            mongo.getDatabaseNames();
        } catch (MongoTimeoutException ex) {
            fail("Embedded mongodb not started");
        }
    }

}