package com.crawldata.back_end.export_plugin_builder.audio;

import com.crawldata.back_end.export_plugin_builder.ExportPluginFactory;
import com.crawldata.back_end.model.Chapter;
import com.crawldata.back_end.plugin_builder.PluginFactory;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


@Service
public class AudioPlugin implements ExportPluginFactory {
    private final  String API_TTS = "https://viettelgroup.ai/voice/api/tts/v1/rest/syn";
    private final String TOKEN = "Nvh7EYHB9TXCXu0WB9tTxGI3e8mKlgiKBkLxuXqsO3qJp6wS79gXfbjg5BBV1Rj3";
    private final double SPEED = 1.0;
    private final String VOICE = "hcm-diemmy";
    private final int RETURN_OPTION = 2;
    public void export(Chapter chapter, HttpServletResponse response) throws IOException {
        String content =  Jsoup.parse(chapter.getContent()).text().replace("\"", "");
        String dataJson=String.format( "{\"text\":\"%s\"," + "\"voice\":\"%s\"," +
                "\"id\":\"3\"," +
                "\"without_filter\":false," +
                "\"speed\":%.1f," +
                "\"tts_return_option\":%d}",content,VOICE,SPEED,RETURN_OPTION);
        String fileName = String.format("%s_%s",chapter.getNovelId(),chapter.getChapterId()).replace("-","_");
        InputStream result = null;
        OutputStream os = null;
        try {
            result = getTTS(API_TTS, dataJson);
            response.setContentType("audio/wav");
            response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s.wav\"",fileName));
            // Write audio data to response output stream
            os = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = result.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        } finally {
            if (result != null) {
                result.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }
    public InputStream getTTS(String apiUrl, String datajson) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(apiUrl);
        StringEntity body = new StringEntity(datajson, "UTF-8");
        request.addHeader("content-type", "application/json;charset=UTF-8");
        request.addHeader("token", TOKEN);
        request.getRequestLine();
        request.setEntity(body);
        HttpResponse response = httpClient.execute(request);
        System.err.println(response);
        return response.getEntity().getContent();
    }

    public void export(PluginFactory plugin, String novelId, HttpServletResponse response) throws IOException {

    }
}
