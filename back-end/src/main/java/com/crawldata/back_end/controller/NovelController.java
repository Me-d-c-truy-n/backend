package com.crawldata.back_end.controller;
import com.crawldata.back_end.plugin.PluginManager;
import com.crawldata.back_end.plugin_builder.PluginTemplate;
import com.crawldata.back_end.service.TruyenFullService;
import com.crawldata.back_end.model.*;
import com.crawldata.back_end.utils.*;
import com.crawldata.back_end.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xeustechnologies.jcl.JclUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@RestController
@RequiredArgsConstructor
public class NovelController {
    private final TruyenFullService truyenFullService;
    PluginManager pluginManager = new PluginManager();
    //Get detail chapter
    @GetMapping("{pluginId}/truyen/{idNovel}/{idChapter}")
    public ResponseEntity<?> getContents(@PathVariable String pluginId, @PathVariable("idNovel") String idNovel, @PathVariable("idChapter") String idChapter
    ) throws IOException
    {
        Plugin plugin = pluginManager.get(pluginId);
        if(plugin != null ){
            PluginTemplate obj = JclUtils.cast(plugin.getLoadedObject(), PluginTemplate.class);
            ChapterDetail chapterDetail=obj.getDetailChapter(idNovel,idChapter);
            DataResponse result = new DataResponse("success",1,1,1,"",chapterDetail);
            return ResponseEntity.ok(result);
        } else {
            DataResponse result = new DataResponse();
            result.setStatus("error");
            return ResponseEntity.badRequest().body(result);
        }
    }

    //get all chapters of novel
    @GetMapping("/truyen/{idNovel}/all")
    public ResponseEntity<?> getAllChapters( @PathVariable("idNovel") String idNovel,@RequestParam(value = "page",defaultValue = "1") int page
    ) throws IOException
    {
        DataResponse data = truyenFullService.getAllChapters(idNovel,page);
        data.setCurrentPage(page);
        data.setStatus("success");
        data.setSearchValue("");
        return ResponseEntity.ok(data);
    }

    //get detail novel
    @GetMapping("/truyen/{idNovel}")
    public ResponseEntity<?> getDetailNovel( @PathVariable("idNovel") String idNovel
    ) throws IOException
    {
        NovelDetail novelDetail = truyenFullService.getDetailNovel(idNovel);
        DataResponse result = new DataResponse("success",1,1,1,"",novelDetail);
        return ResponseEntity.ok(result);
    }
    //get list novel of an author
    @GetMapping("/tac-gia/{idAuthor}")
    public ResponseEntity<?> getNovelsAuthor( @PathVariable("idAuthor") String idAuthor
    ) throws IOException
    {
        List<Novel> novels = truyenFullService.getNovelsAuthor(idAuthor);
        DataResponse result = new DataResponse("success",1,1,novels.size(),"",novels);
        return ResponseEntity.ok(result);
    }

    //get all novels
    @GetMapping("/ds-truyen")
    public ResponseEntity<?> getAllNovels(@RequestParam(value = "page",defaultValue = "1") int page,@RequestParam(value = "search",defaultValue = "%22") String search) throws IOException
    {
        List<Novel> novels = truyenFullService.getAllNovels(page,search);
        int totalPage = truyenFullService.getEndPage(SourceNovels.FULL_NOVELS+search);
        DataResponse result = new DataResponse("success",totalPage,page,novels.size(),"",novels);
        return ResponseEntity.ok(result);
    }
    //find author by name novel or author
    @GetMapping("/tim-kiem")
    public ResponseEntity<?> findNovels(@RequestParam(value = "page",defaultValue = "1") int page,@RequestParam(value = "search",defaultValue = "%22") String search) throws IOException
    {
        List<Novel> novels = truyenFullService.getAllNovels(page,search);
        int totalPage = truyenFullService.getEndPage(SourceNovels.FULL_NOVELS);
        DataResponse result = new DataResponse("success",totalPage,page,novels.size(),"",novels);
        result.setCurrentPage(page);
        return ResponseEntity.ok(result);
    }
}
