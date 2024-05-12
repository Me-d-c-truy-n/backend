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
public class comicController {
    private final TruyenFullService truyenFullService;
    @GetMapping("")
    public ResponseEntity<Category> getAllCategories(
    ) throws IOException {
        Category categories  = truyenFullService.getComicsCategory("https://truyenfull.vn/the-loai/kiem-hiep/");
        return ResponseEntity.ok(categories);
    }
    //get chapter
    @GetMapping("test")
    public ResponseEntity<DetailComic> getDetailComic(
    ) throws IOException {
        DetailComic comic  = truyenFullService.getAComic("https://truyenfull.vn/thien-tai-tien-dao/");
        return ResponseEntity.ok(comic);
    }

    //Get detail chapter
    @GetMapping("{idNovel}/{idChapter}")
    public ResponseEntity<?> getContents( @PathVariable("idNovel") String idNovel, @PathVariable("idChapter") String idChapter
    ) throws IOException
    {
        ChapterDetail chapterDetail = truyenFullService.getDetailChapter(idNovel,idChapter);
        dataResponse result = new dataResponse("success",1,1,"",chapterDetail);
        return ResponseEntity.ok(result);
    }

    //get all chapters of novel
    @GetMapping("{idNovel}/all")
    public ResponseEntity<?> getAllChapters( @PathVariable("idNovel") String idNovel
    ) throws IOException
    {
        List<Chapter> chapters = truyenFullService.getAllChapters(idNovel);
        dataResponse result = new dataResponse("success",1,1,"",chapters);
        return ResponseEntity.ok(result);
    }
}
