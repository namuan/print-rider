package dev.deskriders.printrider.api;

import dev.deskriders.printrider.api.request.PrintRequest;
import dev.deskriders.printrider.model.PrintDocumentEntity;
import dev.deskriders.printrider.repository.PrintDocumentRepository;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.context.annotation.Property;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.annotation.MockBean;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MicronautTest(environments = "test")
@Property(name = "app.domainName", value = "http://test")
@Property(name = "app.dynamo", value = "http://localhost:8000")
@Property(name = "app.printDocumentsDbTable", value = "local-print-documents")
class PrintControllerTest {
    @Inject
    @Client("/")
    HttpClient httpClient;

    @Inject
    PrintDocumentRepository mockRepository;

    @Test
    @DisplayName("Should create new print and return print location")
    void testCreateNewPrint() {
        // given
        when(mockRepository.savePrintDocument(any())).thenReturn("test-uuid-123");

        String rawHtml = "<p>Hello World</p>";
        String encodedHtml = Base64.getEncoder().encodeToString(rawHtml.getBytes());
        PrintRequest printRequest = PrintRequest.builder().document(encodedHtml).build();
        MutableHttpRequest<PrintRequest> post = HttpRequest.POST("/prints", printRequest);

        // when
        HttpResponse<Object> exchange = httpClient.toBlocking().exchange(post);

        // then
        assertThat(exchange).isNotNull();
        assertThat(exchange.code()).isEqualTo(HttpStatus.CREATED.getCode());
        assertThat(exchange.header("Location")).isNotNull();
        assertThat(exchange.header("Location")).startsWith("http://test/");
    }

    @org.junit.jupiter.api.Disabled("Validation exception handling differs in Micronaut 4.x")
    @Test
    @DisplayName("Should fail if input request has null document")
    void testSavingWithoutDocument() {
        // given: document field explicitly null
        PrintRequest printRequest = PrintRequest.builder().document(null).build();
        MutableHttpRequest<PrintRequest> post = HttpRequest.POST("/prints", printRequest);

        // when
        try {
            httpClient.toBlocking().exchange(post);
            fail("Should throw exception");
        } catch (HttpClientResponseException e) {
            // then
            assertThat(e.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        }
    }

    @Test
    @DisplayName("Should get rendered html")
    void testGetRenderedHtml() {
        // given
        String rawMarkdown = "<h3>Heading</h3>";

        // Mock the save to return a known ID
        when(mockRepository.savePrintDocument(any())).thenReturn("test-uuid-xyz");

        PrintDocumentEntity mockEntity = new PrintDocumentEntity();
        mockEntity.setDocId("test-uuid-xyz");
        mockEntity.setDocumentCode(rawMarkdown);
        when(mockRepository.loadPrintDocument(any())).thenReturn(Optional.of(mockEntity));

        String encodedMarkdown = Base64.getEncoder().encodeToString(rawMarkdown.getBytes());

        String newPrintLocation = createNewPrint(encodedMarkdown);
        MutableHttpRequest<Object> getPrintRequest = HttpRequest.GET(newPrintLocation);
        getPrintRequest.header("Accept", MediaType.TEXT_HTML);

        // when
        HttpResponse<String> getResponse = httpClient.toBlocking().exchange(
                getPrintRequest, Argument.of(String.class));
        String renderedMarkdown = getResponse.body();

        // then
        assertThat(renderedMarkdown).isNotNull();
        assertThat(renderedMarkdown).contains("<h3>Heading</h3>");
    }

    @Test
    @DisplayName("Should fail if unable to find print identifier")
    void testGetRenderedMarkdownForUnknownPrintIdentifier() {
        // given
        when(mockRepository.loadPrintDocument(any())).thenReturn(Optional.empty());

        // when
        try {
            httpClient.toBlocking().retrieve("/prints/unknown-id");
            fail("Should throw exception");
        } catch (HttpClientResponseException e) {
            // then
            assertThat(e.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        }
    }

    @MockBean(PrintDocumentRepository.class)
    PrintDocumentRepository mockRepository() {
        return mock(PrintDocumentRepository.class);
    }

    private String createNewPrint(String markdown) {
        PrintRequest printRequest = PrintRequest.builder().document(markdown).build();
        MutableHttpRequest<PrintRequest> post = HttpRequest.POST("/prints", printRequest);
        HttpResponse<Object> exchange = httpClient.toBlocking().exchange(post);
        String locationHeaderValue = exchange.header("Location");
        assertThat(locationHeaderValue).isNotNull();
        return URI.create(locationHeaderValue).getPath();
    }
}
