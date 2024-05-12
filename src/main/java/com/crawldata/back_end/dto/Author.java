package com.crawldata.back_end.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.Normalizer;
import java.util.regex.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Author {
    String id;
    String name;
    public  void setIdSlug(String nameAuthor)
    {
        String lowercase = nameAuthor.toLowerCase();
        String hyphenated = lowercase.replaceAll("\\s+", "-");
        String normalized = Normalizer.normalize(hyphenated, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String slug = pattern.matcher(normalized).replaceAll("");
        this.id=slug;
    }
}
