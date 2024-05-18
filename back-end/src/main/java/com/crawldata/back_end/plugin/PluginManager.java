package com.crawldata.back_end.plugin;

import com.crawldata.back_end.model.PluginInformation;
import com.crawldata.back_end.utils.AppUtils;
import com.crawldata.back_end.utils.FileUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the lifecycle of plugins in the application.
 */
@Service
@AllArgsConstructor
public class PluginManager {

    private static final String PLUGIN_DIRECTORY = "/plugins";

    private List<PluginInformation> plugins = new ArrayList<>();

    private PluginLoader pluginLoader;

    /**
     * Updates the list of available plugins by loading plugins from the plugin directory.
     */
    public void updatePlugins() {
        // Initialize plugin directory
        File pluginsDir = new File(FileUtils.validate(AppUtils.curDir + PLUGIN_DIRECTORY));
        if (!pluginsDir.exists() || !pluginsDir.isDirectory()) {
            return;
        }

        // Clear existing plugins
        unloadPlugins();
        plugins.clear();

        // Load plugins from JAR files in the directory
        File[] files = pluginsDir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.getName().endsWith(".jar")) {
                PluginInformation pluginInformation = pluginLoader.loadPluginInformation(file);
                if (pluginInformation != null) {
                    plugins.add(pluginInformation);
                }
            }
        }
    }

    /**
     * Unloads classes of all plugins.
     */
    private void unloadPlugins() {
        pluginLoader.unloadPluginClasses(plugins);
    }

    /**
     * Retrieves plugin information by ID.
     *
     * @param id The ID of the plugin.
     * @return The plugin information, or null if not found.
     */
    public PluginInformation getPluginById(String id) {
        return plugins.stream()
                .filter(plugin -> plugin.getPluginId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves all available plugins.
     *
     * @return The list of available plugins.
     */
    public List<PluginInformation> getAllPlugins() {
        return new ArrayList<>(plugins);
    }
}
