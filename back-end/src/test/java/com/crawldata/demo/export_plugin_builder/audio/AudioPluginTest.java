package com.crawldata.demo.export_plugin_builder.audio;
import com.crawldata.back_end.export_plugin_builder.audio.AudioPlugin;
import com.crawldata.back_end.model.Chapter;
import com.crawldata.back_end.novel_plugin_builder.PluginFactory;
import com.crawldata.back_end.response.DataResponse;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AudioPluginTest {

    @Mock
    private PluginFactory pluginFactory;

    @Mock
    private HttpServletResponse httpServletResponse;

    @InjectMocks
    private AudioPlugin audioPlugin;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }g

    @Test
    void export_oneChapter_success() throws Exception {
        // Setup
        Chapter mockChapter = new Chapter();
        mockChapter.setContent("This is a test content.");
        DataResponse mockDataResponse = new DataResponse().status("success").data(mockChapter);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        when(httpServletResponse.getOutputStream()).thenReturn(new MockServletOutputStream(outputStream));
        when(pluginFactory.getNovelChapterDetail(anyString(), anyString())).thenReturn(mockDataResponse);

        audioPlugin.export(pluginFactory, "novel-1", "chuong-1", 1, httpServletResponse);

        verify(pluginFactory).getNovelChapterDetail("novel-1", "chuong-1");
        assertTrue(outputStream.size() > 0);
    }

    @Test
    void export_largeContentSplitting_success() throws Exception {
        // Setup
        Chapter mockChapter = new Chapter();
        mockChapter.setContent("word ".repeat(1000));
        DataResponse mockDataResponse = new DataResponse().status("success").data(mockChapter);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        when(httpServletResponse.getOutputStream()).thenReturn(new MockServletOutputStream(outputStream));
        when(pluginFactory.getNovelChapterDetail(anyString(), anyString())).thenReturn(mockDataResponse);

        audioPlugin.export(pluginFactory, "novel-1", "chuong-1", 1, httpServletResponse);

        verify(pluginFactory).getNovelChapterDetail("novel-1", "chuong-1");
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
