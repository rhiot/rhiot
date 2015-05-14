/**
 * Licensed to the Camel Labs under one or more
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

import com.github.camellabs.iot.cloudlet.sdk.ServiceDiscoveryException;
import org.junit.Assert;
import org.junit.Test;

import static com.github.camellabs.iot.cloudlet.document.sdk.RestDocumentService.DEFAULT_DOCUMENT_SERVICE_URL;
import static com.github.camellabs.iot.cloudlet.document.sdk.RestDocumentService.baseUrlWithContextPath;
import static com.github.camellabs.iot.cloudlet.document.sdk.RestDocumentService.discoverOrDefault;

public class RestDocumentServiceTest extends Assert {

    @Test
    public void shouldTrimUrl() {
        // Given
        String urlWithSpaces = " http://app.com ";

        // When
        String normalizedUrl = baseUrlWithContextPath(urlWithSpaces);

        // Then
        assertEquals("http://app.com/api/document", normalizedUrl);
    }

    @Test
    public void shouldSuggestWrongConnectionUrl() {
        try {
            RestDocumentService.discover();
        } catch (ServiceDiscoveryException e) {
            assertTrue(e.getMessage().contains("Are you sure"));
            assertTrue(e.getMessage().contains("default connection URL for document service"));
            return;
        }
        fail();
    }

    @Test
    public void shouldFallbackToDefault() {
        RestDocumentService service = discoverOrDefault();
        assertEquals(DEFAULT_DOCUMENT_SERVICE_URL + "/api/document", service.baseUrl);
    }

}
