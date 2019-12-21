package dev.deskriders.printrider.helper;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.util.Base64;

public class HtmlHelper {
    public static String sanitiseHtml(String encodedHtml) {
        String decodedHtml = new String(Base64.getDecoder().decode(encodedHtml));
        Whitelist whitelist = Whitelist.relaxed().addAttributes(":all", "class");
        return Jsoup.clean(decodedHtml, whitelist);
    }
}
