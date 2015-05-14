package com.github.camellabs.iot.cloudlet.document.driver;

import boot.mongo.MongoDbEndpoint;
import boot.mongo.MongoDbMvcEndpoint;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoTimeoutException;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import org.apache.camel.component.swagger.DefaultCamelSwaggerServlet;
import org.apache.camel.spring.boot.FatJarRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.UnknownHostException;

import static de.flapdoodle.embed.mongo.distribution.Version.V2_6_1;
import static de.flapdoodle.embed.process.runtime.Network.localhostIsIPv6;
import static java.lang.Integer.parseInt;
import static java.lang.System.getenv;

@SpringBootApplication
public class DriverDocumentCloudlet extends FatJarRouter {

    static final Logger LOG = LoggerFactory.getLogger(DriverDocumentCloudlet.class);

    @ConditionalOnProperty(value = "camel.labs.iot.cloudlet.document.driver.mongodb.embedded", havingValue = "true")
    @Bean(initMethod = "start", destroyMethod = "stop")
    public MongodExecutable embeddedMongoDB(
            @Value("${camel.labs.iot.cloudlet.document.driver.mongodb.embedded.port:27017}") int mongodbPort
    ) throws IOException {
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(V2_6_1)
                .net(new Net(mongodbPort, localhostIsIPv6()))
                .build();
        return MongodStarter.getDefaultInstance().prepare(mongodConfig);
    }

    @Bean
    @ConditionalOnProperty(value = "camel.labs.iot.cloudlet.document.driver.mongodb.springbootconfig", matchIfMissing = true, havingValue = "false")
    Mongo mongo() throws UnknownHostException {
        String mongodbKubernetesHost = System.getenv("MONGODB_SERVICE_HOST");
        String mongodbKubernetesPort = System.getenv("MONGODB_SERVICE_PORT");
        if (mongodbKubernetesHost != null && mongodbKubernetesPort != null) {
            LOG.info("Kubernetes MongoDB service detected - {}:{}. Connecting...", mongodbKubernetesHost, mongodbKubernetesPort);
            return new MongoClient(mongodbKubernetesHost, parseInt(mongodbKubernetesPort));
        } else {
            LOG.info("Can't find MongoDB Kubernetes service.");
            LOG.debug("Environment variables: {}", getenv());
        }

        try {
            LOG.info("Attempting to connect to the MongoDB server at mongodb:27017.");
            Mongo mongo = new MongoClient("mongodb");
            mongo.getDatabaseNames();
            return mongo;
        } catch (MongoTimeoutException e) {
            LOG.info("Can't connect to the MongoDB server at mongodb:27017. Falling back to the localhost:27017.");
            return new MongoClient();
        }
    }

    @Bean
    MongoDbEndpoint mongoDbEndpoint(MongoTemplate mongoTemplate) {
        return new MongoDbEndpoint(mongoTemplate);
    }

    @Bean
    MongoDbMvcEndpoint mongoDbMvcEndpoint(MongoDbEndpoint mongoDbEndpoint) {
        return new MongoDbMvcEndpoint(mongoDbEndpoint);
    }

    @Bean
    ServletRegistrationBean swaggerServlet(@Value("${server.port:15000}") int serverPort, @Value("${camel.labs.iot.cloudlet.rest.port:15001}") int restPort) {
        ServletRegistrationBean swaggerServlet = new ServletRegistrationBean();
        swaggerServlet.setName("ApiDeclarationServlet");
        swaggerServlet.setServlet(new DefaultCamelSwaggerServlet());
        swaggerServlet.addInitParameter("base.path", String.format("http://localhost:%d/api", restPort));
        swaggerServlet.addInitParameter("api.path", String.format("http://localhost:%d/api/contract", serverPort));
        swaggerServlet.addInitParameter("api.version", "1.2.3");
        swaggerServlet.addInitParameter("api.title", "User Services");
        swaggerServlet.addInitParameter("api.description", "Camel Rest Example with Swagger that provides an User REST service");
        swaggerServlet.addInitParameter("cors", "true");
        swaggerServlet.setLoadOnStartup(2);
        swaggerServlet.addUrlMappings("/api/contract/*");
        return swaggerServlet;
    }

    @Bean
    Filter corsFilter() {
        return new Filter() {

            public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
                HttpServletResponse response = (HttpServletResponse) res;
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
                response.setHeader("Access-Control-Max-Age", "3600");
                response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
                chain.doFilter(req, res);
            }

            public void init(FilterConfig filterConfig) {
            }

            public void destroy() {
            }

        };
    }

}