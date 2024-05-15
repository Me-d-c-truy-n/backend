package com.crawldata.back_end.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
