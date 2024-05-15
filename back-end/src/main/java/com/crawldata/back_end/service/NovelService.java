package com.crawldata.back_end.service;

import com.crawldata.back_end.model.Chapter;
import com.crawldata.back_end.model.Novel;
import com.crawldata.back_end.plugin_builder.PluginFactory;
import com.crawldata.back_end.response.DataResponse;
import java.util.List;

/**
 * Service interface for managing novels.
 */
public interface NovelService {

    /**
     * Retrieves the plugin factory for the specified plugin ID.
     *
     * @param pluginId The ID of the plugin.
     * @return The plugin factory.
     */
    PluginFactory getPluginFactory(String pluginId);

    /**
     * Retrieves the total number of pages for a novel from the specified plugin and URL.
     *
     * @param pluginId The ID of the plugin.
     * @param url      The URL of the novel.
     * @return The total number of pages.
     */
    int getNovelTotalPages(String pluginId, String url);

    /**
     * Retrieves the total number of chapters for a novel from the specified plugin and URL.
     *
     * @param pluginId The ID of the plugin.
     * @param url      The URL of the novel.
     * @return The total number of chapters.
     */
    Integer getNovelTotalChapters(String pluginId, String url);

    /**
     * Retrieves the detail of a specific chapter in a novel.
     *
     * @param pluginId  The ID of the plugin.
     * @param novelId   The ID of the novel.
     * @param chapterId The ID of the chapter.
     * @return The chapter detail.
     */
    Chapter getNovelChapterDetail(String pluginId, String novelId, String chapterId);

    /**
     * Retrieves a paginated list of chapters for a novel.
     *
     * @param pluginId The ID of the plugin.
     * @param novelId  The ID of the novel.
     * @param page     The page number.
     * @return A paginated list of chapters.
     */
    DataResponse getNovelListChapters(String pluginId, String novelId, int page);

    /**
     * Retrieves the detail of a novel.
     *
     * @param pluginId The ID of the plugin.
     * @param novelId  The ID of the novel.
     * @return The novel detail.
     */
    Novel getNovelDetail(String pluginId, String novelId);

    /**
     * Retrieves novels authored by a specific author.
     *
     * @param pluginId The ID of the plugin.
     * @param authorId The ID of the author.
     * @return A list of novels authored by the author.
     */
    List<Novel> getAuthorNovels(String pluginId, String authorId);

    /**
     * Retrieves all novels based on search criteria and pagination.
     *
     * @param pluginId The ID of the plugin.
     * @param page     The page number.
     * @param search   The search criteria.
     * @return A list of novels matching the search criteria.
     */
    List<Novel> getAllNovels(String pluginId, int page, String search);
}
