package com.crawldata.back_end.plugin;


import com.crawldata.back_end.model.Plugin;
import com.crawldata.back_end.utils.AppUtils;
import com.crawldata.back_end.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class PluginManager {
    private static PluginManager manager;
    private static ArrayList<PluginGetter> pluginList;

    public PluginManager() {
        pluginList = new ArrayList<>();
    }
    public static PluginManager getManager() {
        if (manager == null) {
            manager = new PluginManager();
        }

        return manager;
    }

    public void updatePlugins() {
        pluginList.clear();
        File pluginsDir = new File(FileUtils.validate(AppUtils.curDir + "/plugins"));
        if (!pluginsDir.exists() || !pluginsDir.isDirectory()) {
            return;
        }
        File[] files = pluginsDir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.getName().endsWith(".jar")) {
                try {
                    PluginGetter pluginGetter = loadPluginFromJar(file);
                    if (pluginGetter != null) {
                        pluginList.add(pluginGetter);
                    }
                } catch (IOException e) {
                    // Handle IOException, if required
                    e.printStackTrace();
                }
            }
        }
    }

    private PluginGetter loadPluginFromJar(File jarFile) throws IOException {
        return new PluginGetter(jarFile);
    }

    public Plugin get(String id) {
        Iterator<PluginGetter> iterator = pluginList.iterator();
        PluginGetter pluginGetter;
        do {
            if (!iterator.hasNext()) {
                return null;
            }

            pluginGetter = (PluginGetter)iterator.next();
        } while(!pluginGetter.isMatch(id));

        return pluginGetter.getPluginObject();
    }
}
