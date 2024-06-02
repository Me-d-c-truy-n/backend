package com.crawldata.back_end.controller;

import com.crawldata.back_end.export_plugin_builder.pdf.PdfPlugin;
import com.crawldata.back_end.plugin_builder.PluginFactory;
import com.crawldata.back_end.service.ExportServiceImpl;
import com.crawldata.back_end.service.NovelService;
import com.crawldata.back_end.service.NovelServiceImpl;
import com.crawldata.back_end.model.*;
import com.crawldata.back_end.utils.*;
import com.crawldata.back_end.response.*;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class NovelController {

    private final NovelServiceImpl novelServiceImpl;
    private final ExportServiceImpl exportServiceImpl;

    //Get detail chapter
    @GetMapping("{pluginId}/truyen/{novelId}/{chapterId}")
    public ResponseEntity<?> getContents(@PathVariable String pluginId, @PathVariable("novelId") String novelId, @PathVariable("chapterId") String chapterId) {
        DataResponse dataResponse = novelServiceImpl.getNovelChapterDetail(pluginId, novelId, chapterId);
        return ResponseEntity.ok(dataResponse);
    }

    @GetMapping("{pluginId}/truyen/{novelId}/chapters")
    public DataResponse getListChapterPerPage(@PathVariable String pluginId, @PathVariable("novelId") String id, @RequestParam(name =  "page", defaultValue = "1") String page) throws NumberFormatException {
        return novelServiceImpl.getNovelListChapters(pluginId, id, Integer.parseInt(page));
    }
    //get detail novel
    @GetMapping("{pluginId}/truyen/{novelId}")
    public ResponseEntity<?> getDetailNovel(@PathVariable("pluginId") String pluginId, @PathVariable("novelId") String novelId) {
        DataResponse dataResponse = novelServiceImpl.getNovelDetail(pluginId, novelId);
        return ResponseEntity.ok(dataResponse);
    }

    //get list novel of an author
    @GetMapping("{pluginId}/tac-gia/{authorId}")
    public ResponseEntity<?> getNovelsAuthor(@PathVariable("pluginId") String pluginId, @PathVariable("authorId") String authorId)
    {
        DataResponse dataResponse = novelServiceImpl.getDetailAuthor(pluginId, authorId);
        return ResponseEntity.ok(dataResponse);
    }

    //get all novels
    @GetMapping("{pluginId}/ds-truyen")
    public ResponseEntity<?> getAllNovels(@PathVariable("pluginId") String pluginId, @RequestParam(value = "page",defaultValue = "1") int page, @RequestParam(value = "search",defaultValue = "%22") String search) {
        DataResponse dataResponse = novelServiceImpl.getAllNovels(pluginId, page, search);
        return ResponseEntity.ok(dataResponse);
    }

    //find author by name novel or author
    @GetMapping("{pluginId}/tim-kiem")
    public ResponseEntity<?> findNovels(@PathVariable("pluginId") String pluginId, @RequestParam(value = "page",defaultValue = "1") int page,@RequestParam(value = "key",defaultValue = "%22") String key, @RequestParam(name = "orderBy" ,defaultValue = "a-z") String orderBy) {
        DataResponse dataResponse = novelServiceImpl.getSearchedNovels(pluginId,page,key, orderBy);
        return ResponseEntity.ok(dataResponse);
    }

    @GetMapping("{pluginId}/tai-truyen/{fileType}/{novelId}/{chapterId}")
    public void export(@PathVariable("pluginId") String pluginId , @PathVariable(name = "fileType") String fileType,@PathVariable(name = "novelId") String novelId,
                       @PathVariable(name = "chapterId") String chapterId, HttpServletResponse response) throws IOException {
        DataResponse dataResponse = novelServiceImpl.getNovelChapterDetail(pluginId, novelId, chapterId);
        exportServiceImpl.export(fileType, (Chapter) dataResponse.getData(),response);
    }

    @GetMapping("{pluginId}/tai-truyen/{novelId}/{fileType}")
    public void export(@PathVariable("pluginId") String pluginId , @PathVariable(name = "fileType") String fileType,@PathVariable(name = "novelId") String novelId,
                       HttpServletResponse response) throws IOException {
        exportServiceImpl.export(fileType, pluginId, novelId, response);
    }

    // Test
    @GetMapping("exportPdf/{pluginId}/{novelId}/{fileType}")
    public void exportPdf(@PathVariable("pluginId") String pluginId , @PathVariable(name = "fileType") String fileType,@PathVariable(name = "novelId") String novelId,
                          HttpServletResponse response) throws IOException{
        PluginFactory plugin = novelServiceImpl.getPluginFactory(pluginId);
        PdfPlugin pdf = new PdfPlugin();
        pdf.export(plugin,novelId, response);
    }


}
