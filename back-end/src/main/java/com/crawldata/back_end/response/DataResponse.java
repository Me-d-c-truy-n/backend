package com.crawldata.back_end.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataResponse {
    String status;
    Integer totalPage;
    Integer currentPage;
    Integer perPage;
    String searchValue;
    Object data;

    public void setCurrentPage(Integer currentPage) {
        if(currentPage>totalPage)
        {
            this.currentPage=totalPage;
        }
        else
        {
            this.currentPage=currentPage;
        }
    }
}
