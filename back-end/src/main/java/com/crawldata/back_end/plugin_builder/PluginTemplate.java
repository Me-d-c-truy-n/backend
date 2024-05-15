package com.crawldata.back_end.plugin_builder;

import com.crawldata.back_end.model.*;
import com.crawldata.back_end.response.DataResponse;

import java.io.IOException;
import java.util.List;

public interface PluginTemplate {
    //get information of a comic full chapters
    public int getEndPage(String url) throws IOException;

    //get total chapters
    public Integer getTotalChapters(String url) throws IOException;

    //get detail chapter
    public ChapterDetail getDetailChapter(String idNovel, String idChapter) throws IOException;

    // get list chapters of novel
    public DataResponse getAllChapters(String idNovel, int page) throws IOException;

    //get detail novel
    public NovelDetail getDetailNovel(String idNovel) throws IOException;

    //get list novel of an author base on id
    public List<Novel> getNovelsAuthor(String idAuthor) throws IOException;

    //get all novels
    public List<Novel> getAllNovels(int page, String search) throws IOException;
}
