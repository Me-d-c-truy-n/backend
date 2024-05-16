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
     the novel's nextChapterId
     **/
    private String nextChapterId;
    /**
     the novel's preChapterId
     **/
    private String preChapterId;
    /**
     * The name of the chapter.
     */
    String name;
    /**
     * The author of the novel containing the chapter.
     */
    Author author;
    /**
     * The content of the chapter.
     */
    String content;
}
