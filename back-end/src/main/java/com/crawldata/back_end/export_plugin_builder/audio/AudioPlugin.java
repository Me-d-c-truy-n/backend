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
    public void export(Chapter chapter, HttpServletResponse response) throws IOException {
        String content =  Jsoup.parse(chapter.getContent()).text().replace("\"", "");
        String dataJson=String.format( "{\"text\":\"%s\"," + "\"voice\":\"hcm-diemmy\"," +
                "\"id\":\"3\"," +
                "\"without_filter\":false," +
                "\"speed\":1.0," +
                "\"tts_return_option\":2}",content);
        InputStream result = null;
        OutputStream os = null;
        try {
            result = getTTS("https://viettelgroup.ai/voice/api/tts/v1/rest/syn", dataJson);
            response.setContentType("audio/wav");
            response.setHeader("Content-Disposition", "attachment; filename=\"chapter_audio.wav\"");
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
    public static InputStream getTTS(String apiUrl, String datajson) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(apiUrl);
        StringEntity body = new StringEntity(datajson, "UTF-8");
        request.addHeader("content-type", "application/json;charset=UTF-8");
        request.addHeader("token", "I5pzVBRUAHQvwmetEW64G30vHG0cX08RSQyFqbywhNRylaCsV6Z529IX5zCZbpJm");
        request.getRequestLine();
        request.setEntity(body);
        HttpResponse response = httpClient.execute(request);
        System.err.println(response);
        return response.getEntity().getContent();
    }

    public void export(PluginFactory plugin, String novelId, HttpServletResponse response) throws IOException {

    }
}
