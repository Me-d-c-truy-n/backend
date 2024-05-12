package com.crawldata.back_end.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Novel {
    String novelId;
    String novelName;
    String image;
    Integer total;
    Author author;
}
