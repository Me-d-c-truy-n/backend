package com.crawldata.back_end.export_plugin_builder;

import com.crawldata.back_end.model.Chapter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * This interface defines methods for export detail novel chapter
 */
public interface ExportPluginFactory {
    /**
     * Export detail novel chapter
     * @param novelId The ID of the novel.
     * @param chapterId The ID of the chapter.
     * @param response response for client
     */
    public void export(Chapter chapter, HttpServletResponse response) throws IOException;
}
