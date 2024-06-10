package com.crawldata.demo.plugin_builder.truyenfull;
import com.crawldata.back_end.model.Novel;
import com.crawldata.back_end.plugin_builder.truyenfull.TruyenFullPlugin;
import com.crawldata.back_end.response.DataResponse;
import com.crawldata.back_end.utils.ConnectJsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TruyenFullPluginTest {
    @InjectMocks
    private TruyenFullPlugin truyenFullPlugin;

    @Mock
    private ConnectJsoup connectJsoupMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetNovelTotalPagesWithoutPagination() {
        String url = "https://truyenfull.vn/example/";
        Document mockDocument = mock(Document.class);
        Elements mockElements = mock(Elements.class);
        try (MockedStatic<ConnectJsoup> mockedStatic = mockStatic(ConnectJsoup.class)) {
            mockedStatic.when(() -> ConnectJsoup.connect(url)).thenReturn(mockDocument);
            when(mockDocument.select("ul[class=pagination pagination-sm] li")).thenReturn(mockElements);
            when(mockElements.size()).thenReturn(0);
            int totalPages = truyenFullPlugin.getNovelTotalPages(url);
            assertEquals(1, totalPages);
        }
    }

    @Test
    public void testGetChapterEndOfNovelWithPagination() {
        String url = "https://truyenfull.vn/vo-au-tri/";
        int chapterEnd = truyenFullPlugin.getChapterEnd(url);
        assertEquals(110, chapterEnd);
    }

    @Test
    public void testGetChapterEndOfNovelWithoutPagination() {
        String url = "https://truyenfull.vn/phu-quan-la-do-doan-tu/";
        int chapterEnd = truyenFullPlugin.getChapterEnd(url);
        assertEquals(38, chapterEnd);
    }

    @Test
    public void testGetAllNovels() {
        // Arrange
        int page = 1;
        String search = "";
        String jsonResponse = "{\"data\":[{\"id\":1,\"author\":\"Author Name\",\"title\":\"Novel Title\"}],\"meta\":{\"pagination\":{\"total_pages\":10}}}";
        String detailResponse = "{\"data\":{\"description\":\"Novel Description\",\"image\":\"Novel Image\"}}";
        when(truyenFullPlugin.getJsonResponse(String.format("https://api.truyenfull.vn/v1/tim-kiem?title=%s&page=%d", search, page))).thenReturn(jsonResponse);
        when(truyenFullPlugin.getJsonResponse("https://api.truyenfull.vn/v1/story/detail/1")).thenReturn(detailResponse);
        // Act
        DataResponse result = truyenFullPlugin.getAllNovels(page, search);
        // Assert
        assertNotNull(result);
        assertEquals("success", result.getStatus());
        assertEquals(10, result.getTotalPage());
        assertEquals(1, result.getCurrentPage());
        assertEquals(1, result.getTotalPage());
        assertEquals(search, result.getSearchValue());
        assertNotNull(result.getData());
        List<Novel> novels = (List<Novel>) result.getData();
        assertFalse(novels.isEmpty());
        Novel novel = novels.get(0);
        assertEquals("Novel Title", novel.getName());
        assertEquals("Novel Description", novel.getDescription());
        assertEquals("Novel Image", novel.getImage());
        assertEquals("Author Name", novel.getAuthor().getName());
    }
}
