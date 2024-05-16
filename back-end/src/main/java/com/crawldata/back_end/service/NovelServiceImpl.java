package com.crawldata.back_end.service;

import com.crawldata.back_end.model.Chapter;
import com.crawldata.back_end.model.Novel;
import com.crawldata.back_end.plugin.PluginManager;
import com.crawldata.back_end.plugin_builder.PluginFactory;
import com.crawldata.back_end.response.DataResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class NovelServiceImpl implements NovelService{
    private PluginManager pluginManager;
    private final  String createPluginErrorMessage = "Error when creating plugin";
    @Override
    public PluginFactory getPluginFactory(String pluginId) {
        pluginManager.updatePlugins();
        return pluginManager.getPluginById(pluginId).getPluginObject();
    }
    @Override
    public DataResponse getNovelChapterDetail(String pluginId, String novelId, String chapterId) {
        PluginFactory pluginFactory = getPluginFactory(pluginId);
        if(pluginFactory == null) {
            return new DataResponse("error", null, null, null, null, null, createPluginErrorMessage);
        } else {
            return pluginFactory.getNovelChapterDetail(novelId, chapterId);
        }
    }
    @Override
    public DataResponse getNovelListChapters(String pluginId, String novelId, int page) {
        PluginFactory pluginFactory = getPluginFactory(pluginId);
        if(pluginFactory == null) {
            return new DataResponse("error", null, null, null, null, null, createPluginErrorMessage);
        } else {
            return pluginFactory.getNovelListChapters(novelId, page);
        }
    }
    @Override
    public DataResponse getNovelDetail(String pluginId, String novelId) {
        PluginFactory pluginFactory = getPluginFactory(pluginId);
        if(pluginFactory == null) {
            return new DataResponse("error", null, null, null, null, null, createPluginErrorMessage);
        } else {
            return pluginFactory.getNovelDetail(novelId);
        }
    }
    @Override
    public DataResponse getDetailAuthor(String pluginId, String authorId) {
        PluginFactory pluginFactory = getPluginFactory(pluginId);
        if(pluginFactory == null) {
            return new DataResponse("error", null, null, null, null, null, createPluginErrorMessage);
        } else {
            return pluginFactory.getDetailAuthor(authorId);
        }
    }
    @Override
    public DataResponse getAllNovels(String pluginId, int page, String search) {
        PluginFactory pluginFactory = getPluginFactory(pluginId);
        if (pluginFactory == null) {
            return new DataResponse("error", null, null, null, null, null, createPluginErrorMessage);
        } else {
            return pluginFactory.getAllNovels(page, search);
        }
    }

    @Override
    public DataResponse getSearchedNovels(String pluginId, int page, String key, String orderBy) {
        PluginFactory pluginFactory = getPluginFactory(pluginId);
        if (pluginFactory == null) {
            return new DataResponse("error", null, null, null, null, null, createPluginErrorMessage);
        } else {
            return pluginFactory.getNovelSearch(page, key,orderBy);
        }
    }
}
