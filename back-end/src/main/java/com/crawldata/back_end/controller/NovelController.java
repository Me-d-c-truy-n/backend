package com.crawldata.back_end.controller;

import com.crawldata.back_end.service.TruyenFullService;
import com.crawldata.back_end.dto.*;
import com.crawldata.back_end.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        DataResponse result = new DataResponse("success",1,1,"",chapterDetail);
        return ResponseEntity.ok(result);
    }

    //get all chapters of novel
    @GetMapping("{idNovel}/all")
    public ResponseEntity<?> getAllChapters( @PathVariable("idNovel") String idNovel
    ) throws IOException
    {
        List<Chapter> chapters = truyenFullService.getAllChapters(idNovel);
        DataResponse result = new DataResponse("success",1,1,"",chapters);
        return ResponseEntity.ok(result);
    }

    //get detail novel
    @GetMapping("{idNovel}")
    public ResponseEntity<?> getDetailNovel( @PathVariable("idNovel") String idNovel
    ) throws IOException
    {
       NovelDetail novelDetail = truyenFullService.getDetailNovel(idNovel);
        DataResponse result = new DataResponse("success",1,1,"",novelDetail);
        return ResponseEntity.ok(result);
    }
    //get list novel of an author
    @GetMapping("/tac-gia/{idAuthor}")
    public ResponseEntity<?> getNovelsAuthor( @PathVariable("idAuthor") String idAuthor
    ) throws IOException
    {
        List<Novel> novels = truyenFullService.getNovelsAuthor(idAuthor);
        DataResponse result = new DataResponse("success",1,1,"",novels);
        return ResponseEntity.ok(result);
    }
}
