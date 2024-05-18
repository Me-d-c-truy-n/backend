package com.crawldata.back_end.service;

import com.crawldata.back_end.plugin.PluginManager;
import com.crawldata.back_end.plugin_builder.PluginFactory;
import com.crawldata.back_end.response.DataResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
            return pluginFactory.getAuthorDetail(authorId);
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

    //@Override
    public void exportPDF(String pluginId, String novelId, String chapterId, HttpServletResponse response) {
        PluginFactory pluginFactory = getPluginFactory(pluginId);
        if (pluginFactory == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } else {
         //   return pluginFactory.getNovelSearch(page, key,orderBy);
        }
    }
}
