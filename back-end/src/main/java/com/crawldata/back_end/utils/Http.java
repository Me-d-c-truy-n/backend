package com.crawldata.back_end.utils;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class Http {
    private Connection connection;
    private Connection.Response response;

    private Http(String url) throws IOException {
        connection = connect(url);
    }

    public static Connection connect(String url) throws IOException {
        try {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36")
                    .ignoreContentType(true)
                    .followRedirects(true)
                    .maxBodySize(0)
                    .timeout(30000);

        } catch (Exception e) {
            return null;
        }
    }


    public static Document get(String url) {
        try {
            return Objects.requireNonNull(connect(url)).get();
        } catch (IOException e) {
            return null;
        }
    }

    public Http cookie(String cookie) {
        connection.header("Cookie", cookie);
        return this;
    }

    public String cookies() {
        return response.header("Set-Cookie");
    }

    public static Http request(String url) throws IOException {
        return new Http(url);
    }

    public Http data(String name, String value) {
        connection.data(name, value);
        return this;
    }


    public Http data(Map<String, String> data) {
        connection.data(data);
        return this;
    }

    public Http data(String... args) {
        connection.data(args);
        return this;
    }


    public Http header(String name, String value) {
        connection.header(name, value);
        return this;
    }

    private void execute() {
        try {
            response = connection.execute();
        } catch (IOException e) {
        }
    }

    public String string() {
        execute();
        try {
            if (response != null)
                return response.body();
        } catch (Exception e) {
        }
        return null;
    }

    public Document document() {
        execute();
        try {
            if (response != null)
                return response.parse();
        } catch (Exception e) {
        }
        return null;
    }

    public JSONObject json() {
        execute();
        try {
            if (response != null)
                return new JSONObject(response.body());
        } catch (Exception e) {
        }
        return null;
    }

    public byte[] bytes() {
        execute();
        try {
            if (response != null)
                return response.bodyAsBytes();
        } catch (Exception e) {
        }
        return null;
    }

}
