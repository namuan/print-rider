package dev.deskriders.printrider.helper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static dev.deskriders.printrider.helper.HtmlHelper.sanitiseHtml;
import static org.assertj.core.api.Assertions.assertThat;

class HtmlHelperTest {
    @Test
    @DisplayName("Should decode and escape html characters in markdown")
    void testSaveDocumentWithEscapedHtml() {
        // given
        String rawHtml = "<strong>Hello Markdown</strong>";
        String base64Html = Base64.getEncoder().encodeToString(rawHtml.getBytes());

        // when
        String encodedMarkdown = sanitiseHtml(base64Html);

        // then
        assertThat(encodedMarkdown).contains(rawHtml);
    }
}