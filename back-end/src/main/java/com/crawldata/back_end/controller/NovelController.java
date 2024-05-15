package com.crawldata.back_end.controller;

import com.crawldata.back_end.service.NovelServiceImpl;
import com.crawldata.back_end.model.*;
import com.crawldata.back_end.utils.*;
import com.crawldata.back_end.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NovelController {
    private final NovelServiceImpl novelServiceImpl;
    //Get detail chapter
    @GetMapping("{pluginId}/truyen/{novelId}/{chapterId}")
    public ResponseEntity<?> getContents(@PathVariable String pluginId, @PathVariable("novelId") String novelId, @PathVariable("chapterId") String chapterId) {
        Chapter chapter = novelServiceImpl.getNovelChapterDetail(pluginId, novelId, chapterId);
        if(chapter != null) {
            DataResponse result = new DataResponse("success",1,1,1,"", chapter);
            return ResponseEntity.ok(result);
        } else {
            DataResponse result = new DataResponse();
            result.setStatus("error");
            return ResponseEntity.badRequest().body(result);
        }
    }

    //get all chapters of novel
    @GetMapping("{pluginId}/truyen/{novelId}/all")
    public ResponseEntity<?> getAllChapters(@PathVariable("pluginId") String pluginId, @PathVariable("novelId") String novelId, @RequestParam(value = "page", defaultValue = "1") int page) {
        DataResponse data = novelServiceImpl.getNovelListChapters(pluginId, novelId, page);
        data.setCurrentPage(page);
        data.setStatus("success");
        data.setSearchValue("");
        return ResponseEntity.ok(data);
    }

    //get detail novel
    @GetMapping("{pluginId}/truyen/{novelId}")
    public ResponseEntity<?> getDetailNovel(@PathVariable("pluginId") String pluginId, @PathVariable("novelId") String novelId) {
        Novel novel = novelServiceImpl.getNovelDetail(pluginId, novelId);
        DataResponse result = new DataResponse("success",1,1,1,"", novel);
        return ResponseEntity.ok(result);
    }

    //get list novel of an author
    @GetMapping("{pluginId}/tac-gia/{authorId}")
    public ResponseEntity<?> getNovelsAuthor(@PathVariable("pluginId") String pluginId, @PathVariable("authorId") String authorId)
    {
        List<Novel> novels = novelServiceImpl.getAuthorNovels(pluginId, authorId);
        DataResponse result = new DataResponse("success",1,1,novels.size(),"",novels);
        return ResponseEntity.ok(result);
    }

    //get all novels
    @GetMapping("{pluginId}/ds-truyen")
    public ResponseEntity<?> getAllNovels(@PathVariable("pluginId") String pluginId, @RequestParam(value = "page",defaultValue = "1") int page, @RequestParam(value = "search",defaultValue = "%22") String search) {
        List<Novel> novels = novelServiceImpl.getAllNovels(pluginId, page, search);
        int totalPage = novelServiceImpl.getNovelTotalPages(pluginId, SourceNovels.FULL_NOVELS+search);
        DataResponse result = new DataResponse("success",totalPage,page,novels.size(),"",novels);
        return ResponseEntity.ok(result);
    }
    //find author by name novel or author
    @GetMapping("{pluginId}/tim-kiem")
    public ResponseEntity<?> findNovels(@PathVariable("pluginId") String pluginId, @RequestParam(value = "page",defaultValue = "1") int page,@RequestParam(value = "search",defaultValue = "%22") String search) {
        List<Novel> novels = novelServiceImpl.getAllNovels(pluginId, page, search);
        int totalPage = novelServiceImpl.getNovelTotalPages(pluginId, SourceNovels.FULL_NOVELS);
        DataResponse result = new DataResponse("success",totalPage,page,novels.size(),"",novels);
        result.setCurrentPage(page);
        return ResponseEntity.ok(result);
    }
}
