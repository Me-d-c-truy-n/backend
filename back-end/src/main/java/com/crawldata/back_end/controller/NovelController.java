package com.crawldata.back_end.controller;
import com.crawldata.back_end.plugin_builder.truyenfull.TruyenFullPlugin;
import com.crawldata.back_end.service.NovelServiceImpl;
import com.crawldata.back_end.model.*;
import com.crawldata.back_end.utils.*;
import com.crawldata.back_end.response.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class NovelController {
    private final NovelServiceImpl novelServiceImpl;
    private final TruyenFullPlugin truyenFullPlugin;
    //Get detail chapter
    @GetMapping("{pluginId}/truyen/{novelId}/{chapterId}")
    public ResponseEntity<?> getContents(@PathVariable String pluginId, @PathVariable("novelId") String novelId, @PathVariable("chapterId") String chapterId) {
       // DataResponse dataResponse = novelServiceImpl.getNovelChapterDetail(pluginId, novelId, chapterId);
        DataResponse dataResponse = truyenFullPlugin.getNovelChapterDetail(novelId, chapterId);
        return ResponseEntity.ok(dataResponse);
    }

    @GetMapping("{pluginId}/truyen/{novelId}/chapters")
    public DataResponse getListChapterPerPage(@PathVariable String pluginId, @PathVariable("novelId") String id, @RequestParam(name =  "page", defaultValue = "1") String page) throws NumberFormatException {
        //return novelServiceImpl.getNovelListChapters(pluginId, id, Integer.parseInt(page));
        return truyenFullPlugin.getNovelListChapters(id, Integer.parseInt(page));
    }
    //get detail novel
    @GetMapping("{pluginId}/truyen/{novelId}")
    public ResponseEntity<?> getDetailNovel(@PathVariable("pluginId") String pluginId, @PathVariable("novelId") String novelId) {
        //DataResponse dataResponse = novelServiceImpl.getNovelDetail(pluginId, novelId);
        DataResponse dataResponse = truyenFullPlugin.getNovelDetail(novelId);
        return ResponseEntity.ok(dataResponse);
    }

    //get list novel of an author
    @GetMapping("{pluginId}/tac-gia/{authorId}")
    public ResponseEntity<?> getNovelsAuthor(@PathVariable("pluginId") String pluginId, @PathVariable("authorId") String authorId)
    {
       // DataResponse dataResponse = novelServiceImpl.getDetailAuthor(pluginId, authorId);
        DataResponse dataResponse = truyenFullPlugin.getDetailAuthor(authorId);
        return ResponseEntity.ok(dataResponse);
    }

    //get all novels
    @GetMapping("{pluginId}/ds-truyen")
    public ResponseEntity<?> getAllNovels(@PathVariable("pluginId") String pluginId, @RequestParam(value = "page",defaultValue = "1") int page, @RequestParam(value = "search",defaultValue = "%22") String search) {
     //  DataResponse dataResponse = novelServiceImpl.getAllNovels(pluginId, page, search);
        DataResponse dataResponse = truyenFullPlugin.getAllNovels(page,search);
        return ResponseEntity.ok(dataResponse);
    }

    //find author by name novel or author
    @GetMapping("{pluginId}/tim-kiem")
    public ResponseEntity<?> findNovels(@PathVariable("pluginId") String pluginId, @RequestParam(value = "page",defaultValue = "1") int page,@RequestParam(value = "key",defaultValue = "%22") String key, @RequestParam(name = "orderBy" ,defaultValue = "a-z") String orderBy) {
        //DataResponse dataResponse = novelServiceImpl.getSearchedNovels(pluginId,page,key, orderBy);
        DataResponse dataResponse = truyenFullPlugin.getNovelSearch(page,key,orderBy);
        return ResponseEntity.ok(dataResponse);
    }
}
