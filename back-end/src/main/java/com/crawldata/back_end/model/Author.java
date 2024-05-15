package com.crawldata.back_end.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an author entity.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Author {

    /**
     * The unique identifier of the author.
     */
    String authorId;

    /**
     * The name of the author.
     */
    String name;
}
