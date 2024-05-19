package com.crawldata.back_end.plugin;

import com.crawldata.back_end.model.ExportPluginInformation;
import com.crawldata.back_end.model.PluginInformation;
import com.crawldata.back_end.utils.AppUtils;
import com.crawldata.back_end.utils.FileUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ExportPluginManager {
    private static final String EXPORT_PLUGIN_DIRECTORY = "/export_plugins";

    private List<ExportPluginInformation> exportPlugins = new ArrayList<>();

    private ExportPluginLoader exportPluginLoader;

    /**
     * Updates the list of available export plugins by loading export plugins from the plugin directory.
     */
    public void updateExportPlugins() {
        // Initialize plugin directory
        File pluginsDir = new File(FileUtils.validate(AppUtils.curDir + EXPORT_PLUGIN_DIRECTORY));
        if (!pluginsDir.exists() || !pluginsDir.isDirectory()) {
            return;
        }

        // Clear existing plugins
        unloadExportPlugins();
        exportPlugins.clear();

        // Load plugins from JAR files in the directory
        File[] files = pluginsDir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.getName().endsWith(".jar")) {
                ExportPluginInformation exportPluginInformation = exportPluginLoader.loadExportPluginInformation(file);
                if (exportPluginInformation != null) {
                    exportPlugins.add(exportPluginInformation);
                }
            }
        }
    }

    /**
     * Unloads classes of all export plugins.
     */
    private void unloadExportPlugins() {
        exportPluginLoader.unloadPluginClasses();
    }

    /**
     * Retrieves export plugin information by ID.
     * @param id The ID of the plugin.
     * @return The export plugin information, or null if not found.
     */
    public ExportPluginInformation getExportPluginById(String id) {
        return exportPlugins.stream()
                .filter(plugin -> plugin.getPluginId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves all available export plugins.
     *
     * @return The list of available export plugins.
     */
    public List<ExportPluginInformation> getAllExportPlugins() {
        updateExportPlugins();
        return new ArrayList<>(exportPlugins);
    }
}
