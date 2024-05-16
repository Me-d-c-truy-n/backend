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
     * Retrieves the total number of pages of a novel from the given URL.
     *
     * @param url The URL of the novel.
     * @return The total number of pages of the novel.
     */
    public int getNovelTotalPages(String url);

    /**
     * Retrieves the total number of chapters of a novel from the given URL.
     *
     * @param url The URL of the novel.
     * @return The total number of chapters of the novel.
     */
    public Integer getNovelTotalChapters(String url);

    /**
     * Retrieves the details of a specific chapter of a novel.
     *
     * @param novelId The ID of the novel.
     * @param chapterId The ID of the chapter.
     * @return The details of the chapter.
     */
    public Chapter getNovelChapterDetail(String novelId, String chapterId);
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
    public Novel getNovelDetail(String novelId);

    /**
     * Retrieves the novels written by a specific author.
     *
     * @param authorId The ID of the author.
     * @return The list of novels written by the author.
     */
    public List<Novel> getAuthorNovels(String authorId);

    /**
     * Retrieves all novels matching the given search criteria and page number.
     *
     * @param page The page number.
     * @param search The search criteria.
     * @return The list of novels.
     */
    public List<Novel> getAllNovels(int page, String search);
}
