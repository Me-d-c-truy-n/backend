package com.crawldata.demo.export_plugin_builder.epub;

import com.crawldata.back_end.export_plugin_builder.epub.EpubPlugin;
import com.crawldata.back_end.model.Author;
import com.crawldata.back_end.model.Chapter;
import com.crawldata.back_end.model.Novel;
import com.crawldata.back_end.novel_plugin_builder.PluginFactory;
import com.crawldata.back_end.response.DataResponse;
import com.crawldata.back_end.utils.AppUtils;
import com.crawldata.back_end.utils.ConnectJsoup;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EpubPluginTest {

    @Mock
    private PluginFactory pluginFactory;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private EpubPlugin epubPlugin;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AppUtils.curDir = tempDir.toString();
    }

    @Test
    void export_onnChapter_success() throws IOException {
        Novel mockNovel = new Novel().name("test novel").noveId("novel-1").author(new Author("author-id", "author name")).image("image.png");

        Chapter mockChapter = new Chapter().chapterId("chapter-1").name("test tittle").content("this is a content of chapter 1");

        List<Chapter> mockChapterList = new ArrayList<>();
        mockChapterList.add(mockChapter);

        DataResponse novelResponse = new DataResponse().status("success").data(mockNovel);
        DataResponse chapterResponse = new DataResponse().status("success").data(mockChapterList);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(response.getOutputStream()).thenReturn(new EpubPluginTest.MockServletOutputStream(outputStream));
        when(pluginFactory.getNovelDetail(anyString())).thenReturn(novelResponse);
        when(pluginFactory.getNovelListChapters(anyString(), anyString(), anyInt())).thenReturn(chapterResponse);
        when(pluginFactory.getContentChapter(anyString(), anyString())).thenReturn(mockChapter);

        epubPlugin.export(pluginFactory, "novel-1", "chuong-1", 1, response);;
        // Verify the response interactions
        assertTrue(outputStream.size() > 0);
    }

    @Test
    void testExportUntitledEpubNotFound() throws IOException {
        // Create a JAR file without untitled.epub
        File dummyJar = new File(tempDir.toFile(), "epub.jar");
        assertTrue(dummyJar.createNewFile());

        // Execute the export method
        epubPlugin.export(pluginFactory, "test", "chapter-1", 1, response);

        // Verify no response interactions
        verify(response, never()).setContentType(anyString());
        verify(response, never()).setHeader(anyString(), anyString());
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
