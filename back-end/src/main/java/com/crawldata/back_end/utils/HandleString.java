package com.crawldata.back_end.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class HandleString {
    public  static  String makeSlug(String input)
    {
        String lowercase = input.toLowerCase();
        String hyphenated = lowercase.replaceAll("\\s+", "-");
        String normalized = Normalizer.normalize(hyphenated, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String slug = pattern.matcher(normalized).replaceAll("");
        return slug;
    }
}

