package zed.service.document.sdk;

import com.github.camellabs.iot.cloudlet.sdk.ServiceDiscoveryException;
import org.junit.Assert;
import org.junit.Test;

import static zed.service.document.sdk.RestDocumentService.DEFAULT_DOCUMENT_SERVICE_URL;
import static zed.service.document.sdk.RestDocumentService.baseUrlWithContextPath;
import static zed.service.document.sdk.RestDocumentService.discoverOrDefault;

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
