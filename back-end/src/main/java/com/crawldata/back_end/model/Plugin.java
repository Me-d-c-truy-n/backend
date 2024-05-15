package com.crawldata.back_end.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Plugin {
    private String id;
    private String name;
    private String url;
    private String className;

    private Object loadedObject;

    //Method name
   private  String getEndPageMethod;
   private  String getTotalChaptersMethod;
   private  String getDetailChapterMethod;
   private  String getAllChaptersMethod;
   private String getDetailNovelMethod;
   private  String getNovelsAuthorMethod;
   private String getAllNovelsMethod;
}
