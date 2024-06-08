package com.crawldata.back_end.plugin;

import com.crawldata.back_end.export_plugin_builder.ExportPluginFactory;
import com.crawldata.back_end.model.ExportPluginInformation;
import com.crawldata.back_end.model.PluginInformation;
import com.crawldata.back_end.plugin_builder.PluginFactory;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.JclObjectFactory;
import org.xeustechnologies.jcl.JclUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Component
public class ExportPluginLoader {
    private static JarClassLoader jcl = new JarClassLoader();

    /**
     * Loads a export plugin class from the specified JAR file.
     *
     * @param path      The path to the JAR file containing the plugin class.
     * @param className The fully qualified class name of the plugin.
     * @return An instance of the loaded plugin class.
     * */
    public ExportPluginFactory loadExportPluginClass(String path, String className) {
        jcl.add(path);
        JclObjectFactory factory = JclObjectFactory.getInstance();
        Object obj = factory.create(jcl, className);
        return JclUtils.cast(obj, ExportPluginFactory.class);
    }

    /**
     * Loads export plugin information from the specified JAR file.
     *
     * @param pluginFile The JAR file containing the plugin information.
     * @return The plugin information.
     */
    public ExportPluginInformation loadExportPluginInformation(File pluginFile) {
        ExportPluginInformation exportPluginInfo = new ExportPluginInformation();
        readPluginJson(exportPluginInfo, pluginFile);
        exportPluginInfo.setExportPluginObject(loadExportPluginClass(pluginFile.getAbsolutePath(), exportPluginInfo.getClassName()));
        return  exportPluginInfo;
    }

    /**
     * Reads plugin information from the "plugin.json" file inside the JAR file.
     *
     * @param pluginInfo The PluginInformation object to populate.
     * @param pluginFile The JAR file containing the plugin information.
     */
    public void readPluginJson(ExportPluginInformation pluginInfo, File pluginFile) {
        try (JarFile jarFile = new JarFile(pluginFile)) {
            JarEntry entry = jarFile.getJarEntry("plugin.json");
            if (entry == null) {
                // Handle error: "plugin.json" not found in the JAR file
                System.err.println("Error: 'plugin.json' not found in the JAR file.");
                return;
            }
            try (InputStream inputStream = jarFile.getInputStream(entry)) {
                // Read the JSON content from the input stream
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
                String jsonContent = new String(buffer);
                JSONObject js = new JSONObject(jsonContent);
                JSONObject metadata = js.getJSONObject("metadata");
                pluginInfo.setPluginId(metadata.getString("id"));
                pluginInfo.setName(metadata.getString("name"));
                pluginInfo.setClassName(metadata.getString("className"));
            }
        } catch (IOException e) {
            // Handle error: Unable to read "plugin.json" from the JAR file
            e.printStackTrace();
        }
    }

    /**
     * Unloads the all plugin classes.
     *
     */
    public void unloadPluginClasses(List<ExportPluginInformation> plugins) {
        for(ExportPluginInformation plugin : plugins) {
            if(jcl.getLoadedClasses().containsKey(plugin.getClassName())){
                jcl.unloadClass(plugin.getClassName());
            }
        }
        jcl = new JarClassLoader();
    }
}
