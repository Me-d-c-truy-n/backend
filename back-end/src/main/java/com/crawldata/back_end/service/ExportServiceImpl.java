package com.crawldata.back_end.service;

import com.crawldata.back_end.export_plugin_builder.ExportPluginFactory;
import com.crawldata.back_end.export_plugin_builder.epub.EpubPlugin;
import com.crawldata.back_end.model.Chapter;
import com.crawldata.back_end.model.ExportPluginInformation;
import com.crawldata.back_end.model.PluginInformation;
import com.crawldata.back_end.plugin.ExportPluginManager;
import com.crawldata.back_end.plugin.PluginManager;
import com.crawldata.back_end.plugin_builder.PluginFactory;
import com.crawldata.back_end.response.DataResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ExportServiceImpl implements  ExportService{

    private ExportPluginManager exportPluginManager;
    private final String createPluginErrorMessage = "Error when creating plugin";
    private final NovelService novelService;
    @Override
    public ExportPluginFactory getExportPluginFactory(String pluginId) {
        if(pluginId.equals("epub")) {
            return new EpubPlugin();
        }
        exportPluginManager.updateExportPlugins();
        return exportPluginManager.getExportPluginById(pluginId).getExportPluginObject();
    }


    @Override
    public void export(String pluginId, Chapter chapter, HttpServletResponse response) throws IOException {
        ExportPluginFactory exportPluginFactory = getExportPluginFactory(pluginId);
        if(exportPluginFactory == null) {
            //return new DataResponse("error", null, null, null, null, null, createPluginErrorMessage);
        } else {
            exportPluginFactory.export(chapter, response);
        }
    }

    @Override
    public void export(String fileType, String pluginId, String novelId, HttpServletResponse response) throws IOException {
        ExportPluginFactory exportPluginFactory = getExportPluginFactory(fileType);
        if(exportPluginFactory == null) {
            //return new DataResponse("error", null, null, null, null, null, createPluginErrorMessage);
        } else {
            exportPluginFactory.export(novelService.getPluginFactory(pluginId), novelId, response);
        }
    }

    @Override
    public List<String> getAllExportPlugins() {
        ArrayList<String> keyExportPlugins = new ArrayList<>();
        List<ExportPluginInformation> listPlugins = exportPluginManager.getAllExportPlugins();
        listPlugins.forEach(plugin -> {
            keyExportPlugins.add(plugin.getPluginId());
        });
        return keyExportPlugins;
    }
}
