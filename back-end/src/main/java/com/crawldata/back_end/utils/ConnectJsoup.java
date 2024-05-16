package com.crawldata.back_end.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ConnectJsoup {
    private static final int DEFAULT_TIMEOUT = 3 * 1000; // 10 seconds
    public static Document connect(String url) throws IOException {
        return Jsoup.connect(url).timeout(DEFAULT_TIMEOUT).get();
    }
}
