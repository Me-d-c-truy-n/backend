package com.crawldata.demo.export_plugin_builder.epub;

import com.crawldata.back_end.export_plugin_builder.epub.EpubPlugin;
import com.crawldata.back_end.model.Author;
import com.crawldata.back_end.model.Chapter;
import com.crawldata.back_end.model.Novel;
import com.crawldata.back_end.novel_plugin_builder.PluginFactory;
import com.crawldata.back_end.response.DataResponse;
import com.crawldata.back_end.utils.AppUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


public class EpubPluginTest {

    @Mock
    private PluginFactory pluginFactory;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private EpubPlugin epubPlugin;

    @TempDir
    Path tempDir;

   @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void export_oneChapter_success() throws IOException {
        Novel mockNovel = new Novel().name("test novel").noveId("novel-1").author(new Author("author-id", "author name")).image("https://static.cdnno.com/poster/sau-khi-ly-hon-ta-ke-thua-trong-tro-choi-tai-san/300.jpg?1715769900");

        Chapter mockChapter = new Chapter().chapterId("chapter-1").name("test tittle").content("this is a content of chapter 1");

        List<Chapter> mockChapterList = new ArrayList<>();
        mockChapterList.add(mockChapter);

        DataResponse novelResponse = new DataResponse().status("success").data(mockNovel);
        DataResponse chapterResponse = new DataResponse().status("success").data(mockChapterList);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(response.getOutputStream()).thenReturn(new MockServletOutputStream(outputStream));
        when(pluginFactory.getNovelDetail(anyString())).thenReturn(novelResponse);
        when(pluginFactory.getNovelListChapters(anyString(), anyString(), anyInt())).thenReturn(chapterResponse);
        when(pluginFactory.getContentChapter(anyString(), anyString())).thenReturn(mockChapter);
        epubPlugin.export(pluginFactory, "novel-1", "chuong-1", 1, response);
        // Verify the response interactions
        assertTrue(outputStream.size() > 0);
    }

    private static class MockServletOutputStream extends ServletOutputStream {
        private final ByteArrayOutputStream outputStream;

        public MockServletOutputStream(ByteArrayOutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void write(int b) {
            outputStream.write(b);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }
    }
}
