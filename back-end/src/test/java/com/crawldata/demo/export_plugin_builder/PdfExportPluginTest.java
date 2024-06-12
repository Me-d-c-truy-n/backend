package com.crawldata.demo.export_plugin_builder;

import com.crawldata.back_end.export_plugin_builder.pdf.PdfPlugin;
import com.crawldata.back_end.model.Author;
import com.crawldata.back_end.model.Chapter;
import com.crawldata.back_end.model.Novel;
import com.crawldata.back_end.plugin_builder.PluginFactory;
import com.crawldata.back_end.response.DataResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PdfExportPluginTest {

    @Mock
    private PluginFactory pluginFactory;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private PdfPlugin pdfPlugin;

    private Novel novel;
    private List<Chapter> chapters;
    private Chapter chapter;

    @BeforeEach
    void setUp() {
        novel = new Novel();
        novel.setNovelId("tri-menh-vu-kho");
        novel.setName("Trí Mệnh Vũ Khố - 致命武库");
        novel.setAuthor(new Author().name("Trần Phong Bạo Liệt Tửu").authorId("25518"));

        novel.setImage("https://www.nae.vn/ttv/ttv/public/images/story/735dd776417121460da98dccf9ea75d824c2f9354eed9dd9bff23f29c563c1d8.jpg");

        chapter = new Chapter().chapterId("chuong-1").novelId("tri-menh-vu-kho").content("content").name("CHuong 1");
        chapters = new ArrayList<>();
        chapters.add(chapter);
    }

    @Test
    void testExport() throws Exception {
        String novelId = "tri-menh-vu-kho";
        String fromChapterId = "chuong-1";
        int numChapters= 1;

        // Mocking DataResponse for novel detail
        DataResponse novelDetailResponse = new DataResponse();
        novelDetailResponse.setStatus("success");
        novelDetailResponse.setData(novel);

        // Mocking DataResponse for novel chapters
        DataResponse chaptersResponse = new DataResponse();
        chaptersResponse.setStatus("success");
        chaptersResponse.setData(chapters);

        // Mocking pluginFactory responses
        when(pluginFactory.getNovelDetail(novelId)).thenReturn(novelDetailResponse);
        when(pluginFactory.getNovelListChapters(novelId, fromChapterId, numChapters)).thenReturn(chaptersResponse);
        when(pluginFactory.getContentChapter(novelId, fromChapterId)).thenReturn(chapter);

        // Mocking HttpServletResponse output stream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        when(response.getOutputStream()).thenReturn(new ServletOutputStreamMock(baos));

        // Running the export method
        pdfPlugin.export(pluginFactory, novelId, fromChapterId, numChapters, response);

        // Verify interactions
        verify(pluginFactory).getNovelDetail(novelId);
        verify(pluginFactory).getNovelListChapters(novelId, fromChapterId, 1);
        verify(pluginFactory).getContentChapter(novelId, fromChapterId);

        // Verify PDF content (this is a simple check, real checks should be more thorough)
        assertTrue(baos.size() > 0, "PDF should be generated and written to the output stream");
    }

    // Mock ServletOutputStream
    private static class ServletOutputStreamMock extends jakarta.servlet.ServletOutputStream {
        private final ByteArrayOutputStream baos;

        public ServletOutputStreamMock(ByteArrayOutputStream baos) {
            this.baos = baos;
        }

        @Override
        public void write(int b) {
            baos.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) {
            baos.write(b, off, len);
        }

        @Override
        public void setWriteListener(jakarta.servlet.WriteListener writeListener) {
            // No implementation needed for this mock
        }

        @Override
        public boolean isReady() {
            return true;
        }
    }
}
