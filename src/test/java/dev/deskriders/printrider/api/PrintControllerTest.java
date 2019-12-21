package dev.deskriders.printrider.api;

import dev.deskriders.printrider.api.request.PrintRequest;
import io.micronaut.core.type.Argument;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.net.URI;
import java.util.Base64;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@MicronautTest
class PrintControllerTest {
    @Inject
    @Client("/")
    HttpClient httpClient;

    private EasyRandom nextRandom = new EasyRandom();

    @Test
    @DisplayName("Should create new print and return print location")
    void testCreateNewPrint() {
        // given
        PrintRequest printRequest = nextRandom.nextObject(PrintRequest.class);
        MutableHttpRequest<PrintRequest> post = HttpRequest.POST("/prints", printRequest);

        // when
        HttpResponse<Object> exchange = httpClient.toBlocking().exchange(post);

        // then
        assertThat(exchange).isNotNull();
        assertThat(exchange.code()).isEqualTo(HttpStatus.CREATED.getCode());
        assertThat(exchange.header("Location")).isNotNull();
        assertThat(exchange.header("Location")).startsWith("http://test/");
    }

    @Test
    @DisplayName("Should fail if input request doesn't contain document")
    void testSavingWithoutDocument() {
        // given
        Map printRequest = CollectionUtils.mapOf("document", "");
        MutableHttpRequest<Map> postRequest = HttpRequest.POST(
                "/prints",
                printRequest
        );

        // when
        try {
            httpClient.toBlocking().exchange(postRequest);
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
        String encodedMarkdown = Base64.getEncoder().encodeToString(rawMarkdown.getBytes());

        String newPrintLocation = createNewPrint(encodedMarkdown);
        MutableHttpRequest<Object> getPrintRequest = HttpRequest.GET(newPrintLocation);
        getPrintRequest.header("Accept", MediaType.TEXT_HTML);

        // when
        HttpResponse<String> getResponse = httpClient.toBlocking().exchange(getPrintRequest, Argument.of(String.class));
        String renderedMarkdown = getResponse.body();

        // then
        assertThat(renderedMarkdown).isNotNull();
        assertThat(renderedMarkdown).contains("<h3>Heading</h3>");
    }

    @Test
    @DisplayName("Should fail if unable to find print identifier")
    void testGetRenderedMarkdownForUnknownPrintIdentifier() {
        // when
        try {
            httpClient.toBlocking().retrieve("/prints/unknown-id");
            fail("Should throw exception");
        } catch (HttpClientResponseException e) {
            // then
            assertThat(e.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        }
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