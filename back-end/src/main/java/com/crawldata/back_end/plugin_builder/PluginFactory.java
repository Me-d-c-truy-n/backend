package com.crawldata.back_end.plugin_builder;

import com.crawldata.back_end.model.Chapter;
import com.crawldata.back_end.model.Novel;
import com.crawldata.back_end.response.DataResponse;

import java.util.List;

/**
 * This interface defines methods for retrieving various details about novels and authors.
 */
public interface PluginFactory {

     /**
     * Retrieves the details of a specific chapter of a novel.
     *
     * @param novelId The ID of the novel.
     * @param chapterId The ID of the chapter.
     * @return The details of the chapter.
     */
    public  DataResponse getNovelChapterDetail(String novelId, String chapterId);
    /**
     * Retrieves a list of chapters for a given novel and page number.
     *
     * @param novelId The ID of the novel.
     * @param page The page number.
     * @return The list of chapters.
     */
    public DataResponse getNovelListChapters(String novelId, int page);

    /**
     * Retrieves the details of a novel.
     *
     * @param novelId The ID of the novel.
     * @return The details of the novel.
     */
    public DataResponse getNovelDetail(String novelId);

    /**
     * Retrieves the novels written by a specific author.
     *
     * @param authorId The ID of the author.
     * @return The list of novels written by the author.
     */
    public DataResponse getDetailAuthor(String authorId);

    /**
     * Retrieves all novels matching the given search criteria and page number.
     *
     * @param page The page number.
     * @param search The search criteria.
     * @return The list of novels.
     */
    public DataResponse getAllNovels(int page, String search);


    /**
     * Retrieves all novels matching the given search criteria and page number.
     *
     * @param page The page number.
     * @param key The search criteria.
     * @param orderBy The sort option
     * @return The list of novels.
     */
    public DataResponse getNovelSearch(int page, String key, String orderBy);
}
