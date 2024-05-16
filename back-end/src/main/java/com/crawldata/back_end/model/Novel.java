package com.crawldata.back_end.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a novel entity.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Novel {
     /**
     * The unique identifier of the novel.
     */
    String novelId;
    /**
     * The name of the novel.
     */
    String name;
    /**
     * The URL or path to the image associated with the novel.
     */
    String image;
    /**
     * A brief description of the novel.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String description;
    /**
     * The author of the novel.
     */
    Author author;
    /**
     * The novel's first chapter
     * **/
    String firstChapter;

}
