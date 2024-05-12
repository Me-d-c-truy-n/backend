package com.crawldata.back_end.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jsoup.select.Elements;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChapterDetail {
    String novelId;
    String novelName;
    String chapterId;
    String name;
    Integer total;
    Author author;
    String content;
}
