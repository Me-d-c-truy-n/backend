package com.crawldata.back_end.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ConnectJsoup {
    private static final String USER_AGENT_STRING = "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; WOW64; Trident/6.0)";
    private static final int DEFAULT_TIMEOUT = 8* 1000; // 10 seconds
    public static Document connect(String url) throws IOException {
        return Jsoup.connect(url).userAgent(USER_AGENT_STRING).timeout(DEFAULT_TIMEOUT).get();
    }
}
