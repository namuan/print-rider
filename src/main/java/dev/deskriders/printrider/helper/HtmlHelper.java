package dev.deskriders.printrider.helper;

import dev.deskriders.printrider.api.exception.BadRequestException;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HtmlHelper {
    public static String sanitiseHtml(String encodedHtml) {
        try {
            String decodedHtml = new String(Base64.getDecoder().decode(encodedHtml), StandardCharsets.UTF_8);
            Safelist safelist = Safelist.relaxed().addAttributes(":all", "class");
            return Jsoup.clean(decodedHtml, safelist);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(String.format("Invalid base64 encoded code: %s", encodedHtml), e);
        }
    }
}
