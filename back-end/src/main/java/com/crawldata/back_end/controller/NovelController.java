package com.crawldata.back_end.controller;

import com.crawldata.back_end.service.TruyenFullService;
import com.crawldata.back_end.dto.*;
import com.crawldata.back_end.utils.*;
import com.crawldata.back_end.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/truyenfull")
@RequiredArgsConstructor
public class NovelController {
    private final TruyenFullService truyenFullService;
    //Get detail chapter
    @GetMapping("{idNovel}/{idChapter}")
    public ResponseEntity<?> getContents( @PathVariable("idNovel") String idNovel, @PathVariable("idChapter") String idChapter
    ) throws IOException
    {
        ChapterDetail chapterDetail = truyenFullService.getDetailChapter(idNovel,idChapter);
        DataResponse result = new DataResponse("success",1,1,1,"",chapterDetail);
        return ResponseEntity.ok(result);
    }

    //get all chapters of novel
    @GetMapping("{idNovel}/all")
    public ResponseEntity<?> getAllChapters( @PathVariable("idNovel") String idNovel
    ) throws IOException
    {
        List<Chapter> chapters = truyenFullService.getAllChapters(idNovel);
        DataResponse result = new DataResponse("success",1,1,chapters.size(),"",chapters);
        return ResponseEntity.ok(result);
    }

    //get detail novel
    @GetMapping("{idNovel}")
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

    //get all novel category "Kiem hiep"
    @GetMapping("/ds-truyen")
    public ResponseEntity<?> getAllNovels(@RequestParam(value = "page",defaultValue = "1") int page) throws IOException
    {
        List<Novel> novels = truyenFullService.getAllNovels(page);
        int totalPage = truyenFullService.getEndPage(SourceNovels.kiemHiep);
        DataResponse result = new DataResponse("success",totalPage,page,novels.size(),"",novels);
        return ResponseEntity.ok(result);
    }
}
