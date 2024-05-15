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


    @Override
    public PluginFactory getPluginFactory(String pluginId) {
        pluginManager.updatePlugins();
        return pluginManager.getPluginById(pluginId).getPluginObject();
    }

    @Override
    public int getNovelTotalPages(String pluginId, String url) {
        PluginFactory pluginFactory = getPluginFactory(pluginId);
        if(pluginFactory == null) {
            return 0;
        } else {
            return pluginFactory.getNovelTotalPages(url);
        }
    }

    @Override
    public Integer getNovelTotalChapters(String pluginId, String url) {
        PluginFactory pluginFactory = getPluginFactory(pluginId);
        if(pluginFactory == null) {
            return null;
        } else {
            return pluginFactory.getNovelTotalChapters(url);
        }
    }

    @Override
    public Chapter getNovelChapterDetail(String pluginId, String novelId, String chapterId) {
        PluginFactory pluginFactory = getPluginFactory(pluginId);
        if(pluginFactory == null) {
            return new Chapter();
        } else {
            return pluginFactory.getNovelChapterDetail(novelId, chapterId);
        }
    }

    @Override
    public DataResponse getNovelListChapters(String pluginId, String novelId, int page) {
        PluginFactory pluginFactory = getPluginFactory(pluginId);
        if(pluginFactory == null) {
            return new DataResponse();
        } else {
            return pluginFactory.getNovelListChapters(novelId, page);
        }
    }

    @Override
    public Novel getNovelDetail(String pluginId, String novelId) {
        PluginFactory pluginFactory = getPluginFactory(pluginId);
        if(pluginFactory == null) {
            return new Novel();
        } else {
            return pluginFactory.getNovelDetail(novelId);
        }
    }

    @Override
    public List<Novel> getAuthorNovels(String pluginId, String authorId) {
        PluginFactory pluginFactory = getPluginFactory(pluginId);
        if(pluginFactory == null) {
            return new ArrayList<>();
        } else {
            return pluginFactory.getAuthorNovels(authorId);
        }
    }

    @Override
    public List<Novel> getAllNovels(String pluginId, int page, String search) {
        PluginFactory pluginFactory = getPluginFactory(pluginId);
        if(pluginFactory == null) {
            return new ArrayList<>();
        } else {
            return pluginFactory.getAllNovels(page, search);
        }
    }
}
