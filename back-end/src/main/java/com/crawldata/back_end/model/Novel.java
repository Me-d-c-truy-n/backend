package com.crawldata.back_end.model;

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
    String description;

    /**
     * The total number of chapters or sections in the novel.
     */
    Integer total;

    /**
     * The author of the novel.
     */
    Author author;
}
