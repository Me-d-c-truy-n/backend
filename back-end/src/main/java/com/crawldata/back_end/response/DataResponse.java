package com.crawldata.back_end.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a generic data response returned by the backend.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataResponse {

    /**
     * The status of the response.
     */
    String status;

    /**
     * The total number of pages.
     */
    Integer totalPage;

    /**
     * The current page number.
     */
    Integer currentPage;

    /**
     * The number of items per page.
     */
    Integer perPage;

    /**
     * The value used for search, if any.
     */
    String searchValue;

    /**
     * The data payload of the response.
     */
    Object data;

    /**
     * Sets the current page number, ensuring it does not exceed the total number of pages.
     *
     * @param currentPage The current page number to set.
     */
    public void setCurrentPage(Integer currentPage) {
        if (currentPage > totalPage) {
            this.currentPage = totalPage;
        } else {
            this.currentPage = currentPage;
        }
    }
}
