package com.crawldata.back_end.plugin;


import com.crawldata.back_end.model.Plugin;
import com.crawldata.back_end.utils.ZipUtils;
import lombok.Data;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

@Data
public class PluginGetter {
    private File plugin;
    private Plugin pluginObject;

    public PluginGetter(File plugin) throws FileNotFoundException {
        this.plugin = plugin;
        this.load();
    }

    public void load() throws FileNotFoundException {
        pluginObject = new Plugin();
        JSONObject js = new JSONObject(ZipUtils.readInZipAsString(this.plugin, "plugin.json"));
        JSONObject metadata = js.getJSONObject("metadata");
        pluginObject.setId(metadata.getString("id"));
        pluginObject.setName(metadata.getString("name"));
        pluginObject.setUrl(metadata.getString("url"));
        pluginObject.setClassName(metadata.getString("className"));
        pluginObject.setLoadedObject(PluginLoader.loadPluginClass(plugin.getAbsolutePath(),pluginObject.getClassName()));
        System.out.println(pluginObject.getId());
        JSONObject manifest = js.getJSONObject("method");
        pluginObject.setGetAllChaptersMethod(manifest.getString("getAllChaptersMethod"));
        pluginObject.setGetAllNovelsMethod(manifest.getString("getAllNovelsMethod"));
        pluginObject.setGetEndPageMethod(manifest.getString("getEndPageMethod"));
        pluginObject.setGetDetailChapterMethod(manifest.getString("getDetailChapterMethod"));
        pluginObject.setGetDetailNovelMethod(manifest.getString("getDetailNovelMethod"));
        pluginObject.setGetNovelsAuthorMethod(manifest.getString("getNovelsAuthorMethod"));
        pluginObject.setGetTotalChaptersMethod(manifest.getString("getTotalChaptersMethod"));
    }

    public boolean isMatch(String id) {
        return pluginObject.getId().equals(id);
    }

    private String parseClassName(String path) {
        path = path.replaceAll("(.*?/|^)(.*?).class", "$2");
        return path;
    }
}
