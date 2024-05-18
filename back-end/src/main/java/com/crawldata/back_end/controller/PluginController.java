package com.crawldata.back_end.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PluginController {

    @GetMapping("/server")
    public ResponseEntity<?> getListNovelsPlugin()
    {
        return ResponseEntity.ok().build();
    }
}
