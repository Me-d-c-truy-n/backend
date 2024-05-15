package com.crawldata.back_end.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a chapter entity within a novel.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chapter {

    /**
     * The unique identifier of the novel to which the chapter belongs.
     */
    String novelId;

    /**
     * The name of the novel to which the chapter belongs.
     */
    String novelName;

    /**
     * The unique identifier of the chapter.
     */
    String chapterId;

    /**
     * The name of the chapter.
     */
    String name;

    /**
     * The total number of pages or sections in the chapter.
     */
    Integer total;

    /**
     * The author of the novel containing the chapter.
     */
    Author author;

    /**
     * The content of the chapter.
     */
    String content;
}
