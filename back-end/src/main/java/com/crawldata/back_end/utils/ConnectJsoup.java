package com.crawldata.back_end.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ConnectJsoup {
    private static final String USER_AGENT_STRING = "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; WOW64; Trident/6.0)";
    private static final int DEFAULT_TIMEOUT = 8* 1000; // 10 seconds
    private static final int MAX_RETRIES = 3;
    /**
     * Connects to the given URL using Jsoup and returns the Document.
     * Retries the connection in case of a timeout or HTTP error.
     *
     * @param url The URL to connect to.
     * @return The Document object.
     * @throws IOException If an I/O error occurs.
     */
    public static Document connect(String url) throws IOException {
        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            try {
                return Jsoup.connect(url)
                        .userAgent(USER_AGENT_STRING)
                        .timeout(DEFAULT_TIMEOUT)
                        .get();
            } catch (IOException e) {
                System.out.println("Failed to connect to " + url);
                attempt++;
                if (attempt >= MAX_RETRIES) {
                    throw e; // Rethrow the exception if max retries are reached
                }
                try {
                    Thread.sleep(100); // Wait before retrying
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return null;
    }
}
