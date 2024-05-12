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
    String searchValue;
    Object data;
}
