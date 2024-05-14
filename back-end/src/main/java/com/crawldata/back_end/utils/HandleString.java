package com.crawldata.back_end.utils;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;
import java.io.UnsupportedEncodingException;

public class HandleString {
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public static String makeSlug(String input) {
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }
    public static String getValidURL(String invalidURLString){
        try {
            // Convert the String and decode the URL into the URL class
            URL url = new URL(URLDecoder.decode(invalidURLString, StandardCharsets.UTF_8.toString()));
            // Use the methods of the URL class to achieve a generic solution
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            // return String
            return uri.toString();
        } catch (URISyntaxException | UnsupportedEncodingException | MalformedURLException ignored) {
            return null;
        }
    }
}

