package com.crawldata.back_end.service;

import com.crawldata.back_end.export_plugin_builder.ExportPluginFactory;
import com.crawldata.back_end.model.Chapter;
import com.crawldata.back_end.plugin.ExportPluginManager;
import com.crawldata.back_end.plugin.PluginManager;
import com.crawldata.back_end.plugin_builder.PluginFactory;
import com.crawldata.back_end.response.DataResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@AllArgsConstructor
public class ExportServiceImpl implements  ExportService{

    private ExportPluginManager exportPluginManager;
    private final String createPluginErrorMessage = "Error when creating plugin";

    @Override
    public ExportPluginFactory getExportPluginFactory(String pluginId) {
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
}
