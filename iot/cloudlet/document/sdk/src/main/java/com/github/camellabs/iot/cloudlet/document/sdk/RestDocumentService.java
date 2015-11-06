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
package com.github.camellabs.iot.cloudlet.document.sdk;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.camellabs.iot.cloudlet.sdk.HealthCheck;
import com.github.camellabs.iot.cloudlet.sdk.ServiceDiscoveryException;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.github.camellabs.iot.cloudlet.sdk.Discoveries.discoverServiceUrl;
import static com.github.camellabs.iot.cloudlet.sdk.RestTemplates.defaultRestTemplate;
import static io.rhiot.utils.Reflections.classOfArrayOfClass;
import static io.rhiot.utils.Reflections.writeField;
import static java.lang.String.format;
import static com.github.camellabs.iot.cloudlet.document.sdk.Pojos.pojoClassToCollection;

public class RestDocumentService<T> implements DocumentService<T> {

    private static final Logger LOG = LoggerFactory.getLogger(RestDocumentService.class);

    // Constants

    public static final int DEFAULT_DOCUMENT_SERVICE_PORT = 15001;

    public static final String DEFAULT_DOCUMENT_SERVICE_URL = "http://localhost:" + DEFAULT_DOCUMENT_SERVICE_PORT;

    // Configuration members

    @VisibleForTesting
    final String baseUrl;

    // Collaborators members

    private final RestOperations restClient;

    // Constructors

    public RestDocumentService(String baseUrl, RestOperations restClient) {
        this.baseUrl = baseUrlWithContextPath(baseUrl);
        this.restClient = restClient;
    }

    public RestDocumentService(String baseUrl) {
        this(baseUrl, defaultRestTemplate());
    }

    public RestDocumentService(int restApiPort) {
        this("http://localhost:" + restApiPort);
    }

    // Factory methods

    public static RestDocumentService discover() {
        String serviceUrl = discoverServiceUrl("document", DEFAULT_DOCUMENT_SERVICE_PORT, new HealthCheck() {
            @Override
            public void check(String serviceUrl) {
                new RestDocumentService<>(serviceUrl).count(RestDocumentServiceConnectivityTest.class);
            }
        });
        return new RestDocumentService<>(serviceUrl);
    }

    public static RestDocumentService discoverOrDefault() {
        try {
            return discover();
        } catch (ServiceDiscoveryException ex) {
            LOG.debug("Can't discover document service. Falling back to default URL {}.", DEFAULT_DOCUMENT_SERVICE_URL);
            return new RestDocumentService(DEFAULT_DOCUMENT_SERVICE_PORT);
        }
    }

    // Overridden

    @Override
    public T save(T document) {
        try {
            String response = restClient.postForObject(format("%s/save/%s", baseUrl, pojoClassToCollection(document.getClass())), document, String.class);
            Map responseMap = new ObjectMapper().readValue(response, Map.class);
            writeField(document, "id", responseMap.get("result"));
            return document;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T findOne(Class<T> documentClass, String id) {
        ObjectMapper mapper  = new ObjectMapper();
        mapper.setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY));
        try {
            String response = restClient.getForObject(format("%s/findOne/%s/%s", baseUrl, pojoClassToCollection(documentClass), id), String.class);
            return mapper.readValue(response, documentClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<T> findMany(Class<T> documentClass, String... ids) {
        ObjectMapper mapper  = new ObjectMapper();
        mapper.setVisibilityChecker(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY));
        try {
            String response = restClient.postForObject(format("%s/findMany/%s", baseUrl, pojoClassToCollection(documentClass)), new Ids(ids), String.class);
            T[] arrayResponse =  mapper.readValue(response, classOfArrayOfClass(documentClass));
            return ImmutableList.copyOf(arrayResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long count(Class<?> documentClass) {
        try {
            String response = restClient.getForObject(format("%s/count/%s", baseUrl, pojoClassToCollection(documentClass)), String.class);
            Map responseMap = new ObjectMapper().readValue(response, Map.class);
            return (int) responseMap.get("result");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<T> findByQuery(Class<T> documentClass, QueryBuilder queryBuilder) {
        String collection = pojoClassToCollection(documentClass);
        T[] documents = restClient.postForObject(format("%s/findByQuery/%s", baseUrl, collection), queryBuilder, classOfArrayOfClass(documentClass));
        return ImmutableList.copyOf(documents);
    }

    @Override
    public long countByQuery(Class<T> documentClass, QueryBuilder queryBuilder) {
        return restClient.postForObject(format("%s/countByQuery/%s", baseUrl, pojoClassToCollection(documentClass)), queryBuilder, Long.class);
    }

    @Override
    public void remove(Class<T> documentClass, String id) {
        restClient.delete(format("%s/remove/%s/%s", baseUrl, pojoClassToCollection(documentClass), id));
    }

    // Helpers

    static String baseUrlWithContextPath(String baseUrl) {
        baseUrl = baseUrl.trim();
        return baseUrl + "/api/document";
    }

    private static final class RestDocumentServiceConnectivityTest {
    }

}

class Ids {

    String[] ids;

    public Ids(String[] ids) {
        this.ids = ids;
    }

    public Ids() {
    }

    public String[] getIds() {
        return ids;
    }

    public void setIds(String[] ids) {
        this.ids = ids;
    }

}