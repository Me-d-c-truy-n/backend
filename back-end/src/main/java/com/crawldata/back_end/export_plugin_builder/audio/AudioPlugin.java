

import com.crawldata.back_end.export_plugin_builder.ExportPluginFactory;
import com.crawldata.back_end.model.Chapter;
import com.crawldata.back_end.model.Novel;
import com.crawldata.back_end.plugin_builder.PluginFactory;
import com.crawldata.back_end.response.DataResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class AudioPlugin implements ExportPluginFactory {
    private PluginFactory pluginFactory;
    private Novel novel;
    private List<Chapter> chapters;
    private final  String API_TTS = "https://viettelgroup.ai/voice/api/tts/v1/rest/syn";
    private final String TOKEN = "R47WTMLE9e1KfDFZL8-vFa-kVokVY6rgKjZVmK6VPKz--Z4fL5nyYfYG1hXiFGB3";
    private final double SPEED = 1.0;
    private final String VOICE = "hcm-diemmy";
    private final int RETURN_OPTION = 2;
    public void export(Chapter chapter, HttpServletResponse response) throws IOException {
        String content =chapter.getName()+"."+  Jsoup.parse(chapter.getContent()).text().replace("\"", "");
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
        //get list chapter
        getNovelInfo(plugin, novelId);
        List<File> chapterFiles = new ArrayList<>();
        //get data
        String novelName = chapters.get(0).getNovelId().replace("-","_");
        try {
            for (Chapter chapter : chapters) {
                String content =chapter.getName()+".";
                DataResponse dataResponse = pluginFactory.getNovelChapterDetail(novelId,chapter.getChapterId());
                if(dataResponse != null && dataResponse.getStatus().equals("success")) {
                    if(dataResponse.getData() instanceof Chapter)
                    {
                        content+= Jsoup.parse(((Chapter) dataResponse.getData()).getContent()).text().replace("\"", "");
                    }
                }
                // Generate TTS for each chapter
                String dataJson = String.format("{\"text\":\"%s\"," + "\"voice\":\"%s\"," +
                        "\"id\":\"3\"," +
                        "\"without_filter\":false," +
                        "\"speed\":%.1f," +
                        "\"tts_return_option\":%d}", content, VOICE, SPEED, RETURN_OPTION);
                InputStream result = getTTS(API_TTS, dataJson);
                // Save each chapter audio to a temporary file
                File tempFile = File.createTempFile("chapter_" + chapter.getChapterId(), ".wav");
                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = result.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }
                chapterFiles.add(tempFile);
            }
            // Combine all chapter files into one
            File combinedFile = File.createTempFile(novelName, ".wav");
            combineAudioFiles(chapterFiles, combinedFile);
            // Send combined audio file in response
            response.setContentType("audio/wav");
            response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s.wav\"", novelName));
            try (InputStream is = new FileInputStream(combinedFile);
                 OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            // Clean up temporary files
            for (File file : chapterFiles) {
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }
    private void getNovelInfo(PluginFactory plugin, String novelId) {
        pluginFactory = plugin;
        DataResponse dataResponse = pluginFactory.getNovelDetail(novelId);
        if(dataResponse != null && dataResponse.getStatus().equals("success")) {
            novel = (Novel) dataResponse.getData();
        }
        dataResponse = pluginFactory.getNovelListChapters(novel.getNovelId());
        if (dataResponse != null && "success".equals(dataResponse.getStatus())) {
            Object data = dataResponse.getData();
            if (data instanceof List<?> dataList) {
                if (!dataList.isEmpty() && dataList.get(0) instanceof Chapter) {
                    chapters= (List<Chapter>) dataList;
                }
            }
        }
    }

    private void combineAudioFiles(List<File> inputFiles, File outputFile) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        try (SequenceInputStream sis = new SequenceInputStream(Collections.enumeration(inputFiles.stream().map(file -> {
            try {
                return new AudioInputStream(new FileInputStream(file), AudioSystem.getAudioFileFormat(file).getFormat(), file.length());
            } catch (UnsupportedAudioFileException | IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList())))) {
            AudioInputStream combinedAudioInputStream = new AudioInputStream(sis,
                    AudioSystem.getAudioFileFormat(inputFiles.get(0)).getFormat(),
                    inputFiles.stream().mapToLong(file -> {
                        try {
                            return AudioSystem.getAudioFileFormat(file).getFrameLength();
                        } catch (UnsupportedAudioFileException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).sum());

            AudioSystem.write(combinedAudioInputStream, AudioFileFormat.Type.WAVE, outputFile);
        }
    }

    @Override
    public void export(PluginFactory plugin, String novelId, String fromChapterId, int numChapters, HttpServletResponse response) throws IOException {

    }
}
