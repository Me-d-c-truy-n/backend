package com.crawldata.demo.plugin_builder.truyenfull;
import com.crawldata.back_end.model.Author;
import com.crawldata.back_end.model.Novel;
import com.crawldata.back_end.plugin_builder.truyenfull.TruyenFullPlugin;
import com.crawldata.back_end.response.DataResponse;
import com.crawldata.back_end.utils.ConnectJsoup;
import com.crawldata.back_end.utils.HandleString;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
    public void getNovelTotalPages_withoutPagination_success() {
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
    public void getNovelTotalPages_withPagination_success() {
        String url = "https://truyenfull.vn/example/";
        Document mockDocument = mock(Document.class);
        Elements mockElements = mock(Elements.class);
        Element mockElement = mock(Element.class);
        try (MockedStatic<ConnectJsoup> mockedStatic = mockStatic(ConnectJsoup.class)) {
            mockedStatic.when(() -> ConnectJsoup.connect(url)).thenReturn(mockDocument);
            when(mockDocument.select("ul[class=pagination pagination-sm] li")).thenReturn(mockElements);
            when(mockElements.size()).thenReturn(3);
            when(mockElements.get(mockElements.size()-2)).thenReturn(mockElement);
            when(mockElement.text()).thenReturn("Cuối »");
            String notValidLink = "https://example";
            when(mockElement.select("a")).thenReturn(mockElements);
            when(mockElements.attr("href")).thenReturn(notValidLink);
            try(MockedStatic<HandleString> mockedStaticHandle= mockStatic(HandleString.class))
            {
                String validLink="https://valid";
                mockedStaticHandle.when(()->HandleString.getValidURL(notValidLink)).thenReturn(validLink);
                mockedStatic.when(()->ConnectJsoup.connect(validLink)).thenReturn(mockDocument);
                when(mockDocument.select("ul[class=pagination pagination-sm] li")).thenReturn(mockElements);
                when(mockElements.get(mockElements.size() - 2).text().split(" ")[0]).thenReturn("20");
                int totalPages = truyenFullPlugin.getNovelTotalPages(url);
                assertEquals(20, totalPages);
            }
        }
    }

    @Test
    public void getAllNovels_onlyOnePage_success() {
        int page = 1;
        String search = "";
        String jsonResponse = "{\"data\":[{\"id\":1,\"author\":\"Author Name\",\"title\":\"Novel Title\"}],\"meta\":{\"pagination\":{\"total_pages\":1}}}";
        String detailResponse = "{\"data\":{\"description\":\"Novel Description\",\"image\":\"Novel Image\"}}";
        try (MockedStatic<TruyenFullPlugin> mockedStatic = mockStatic(TruyenFullPlugin.class)) {
            mockedStatic.when(() -> truyenFullPlugin.getJsonResponse(String.format("https://api.truyenfull.vn/v1/tim-kiem?title=%s&page=%d", search, page))).thenReturn(jsonResponse);
            mockedStatic.when(() -> truyenFullPlugin.getJsonResponse("https://api.truyenfull.vn/v1/story/detail/1")).thenReturn(detailResponse);
            DataResponse result = truyenFullPlugin.getAllNovels(page, search);
            assertNotNull(result);
            assertEquals("success", result.getStatus());
            assertEquals(1, result.getTotalPage());
            assertEquals(1, result.getCurrentPage());
            assertEquals(1,result.getPerPage());
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

    @Test
    public void getAllNovels_manyPages_success() {
        int page = 1;
        String search = "";
        String jsonResponse = "{\"data\":[{\"id\":1,\"author\":\"Author Name\",\"title\":\"Novel Title\"},{\"id\":2,\"author\":\"Author Name\",\"title\":\"Novel Title\"}],\"meta\":{\"pagination\":{\"total_pages\":10}}}";
        String detailResponse = "{\"data\":{\"description\":\"Novel Description\",\"image\":\"Novel Image\"}}";
        try (MockedStatic<TruyenFullPlugin> mockedStatic = mockStatic(TruyenFullPlugin.class)) {
            mockedStatic.when(() -> truyenFullPlugin.getJsonResponse(String.format("https://api.truyenfull.vn/v1/tim-kiem?title=%s&page=%d", search, page))).thenReturn(jsonResponse);
            mockedStatic.when(() -> truyenFullPlugin.getJsonResponse("https://api.truyenfull.vn/v1/story/detail/1")).thenReturn(detailResponse);
            mockedStatic.when(() -> truyenFullPlugin.getJsonResponse("https://api.truyenfull.vn/v1/story/detail/2")).thenReturn(detailResponse);
            DataResponse result = truyenFullPlugin.getAllNovels(page, search);
            assertNotNull(result);
            assertEquals("success", result.getStatus());
            assertEquals(10, result.getTotalPage());
            assertEquals(1, result.getCurrentPage());
            assertEquals(2,result.getPerPage());
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

    @Test
    public void getNovelSearch_emptyKey_success() {
        int page = 1;
        String search = "";
        String jsonResponse = "{\"data\":[{\"id\":1,\"author\":\"Author Name\",\"title\":\"Novel Title\"}],\"meta\":{\"pagination\":{\"total_pages\":1}}}";
        String detailResponse = "{\"data\":{\"description\":\"Novel Description\",\"image\":\"Novel Image\"}}";
        try (MockedStatic<TruyenFullPlugin> mockedStatic = mockStatic(TruyenFullPlugin.class)) {
            mockedStatic.when(() -> truyenFullPlugin.getJsonResponse(String.format("https://api.truyenfull.vn/v1/tim-kiem?title=%s&page=%d", search, page))).thenReturn(jsonResponse);
            mockedStatic.when(() -> truyenFullPlugin.getJsonResponse("https://api.truyenfull.vn/v1/story/detail/1")).thenReturn(detailResponse);
            DataResponse result = truyenFullPlugin.getAllNovels(page, search);
            assertNotNull(result);
            assertEquals("success", result.getStatus());
            assertEquals(1, result.getTotalPage());
            assertEquals(1, result.getCurrentPage());
            assertEquals(1,result.getPerPage());
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

    @Test
    public void getNovelSearch_failedGetDataFromAPI_error() {
        int page = 1;
        String search = "";
        String jsonResponse = "error";
        String detailResponse = "{\"data\":{\"description\":\"Novel Description\",\"image\":\"Novel Image\"}}";
        try (MockedStatic<TruyenFullPlugin> mockedStatic = mockStatic(TruyenFullPlugin.class)) {
            mockedStatic.when(() -> truyenFullPlugin.getJsonResponse(String.format("https://api.truyenfull.vn/v1/tim-kiem?title=%s&page=%d", search, page))).thenReturn(jsonResponse);
            mockedStatic.when(() -> truyenFullPlugin.getJsonResponse("https://api.truyenfull.vn/v1/story/detail/1")).thenReturn(detailResponse);
            DataResponse result = truyenFullPlugin.getAllNovels(page, search);
            assertNotNull(result);
            assertEquals("success", result.getStatus());
            assertEquals(10, result.getTotalPage());
            assertEquals(1, result.getCurrentPage());
            assertEquals(2,result.getPerPage());
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

    @Test
    public void getAuthorDetail_validAuthorId_success() {
        String authorId = "valid-author-id";
        String url = "https://truyenfull.vn/tac-gia/" + authorId;
        Document mockDocument = mock(Document.class);
        Author author = new Author("john-ka","john ka");
        Elements mockElements = mock(Elements.class);
        Element mockElement = mock(Element.class);
        try (MockedStatic<ConnectJsoup> mockedStatic = mockStatic(ConnectJsoup.class)) {
            mockedStatic.when(() -> ConnectJsoup.connect(url)).thenReturn(mockDocument);
            when(mockDocument.select("div[itemtype=https://schema.org/Book]")).thenReturn(mockElements);
            when(mockElements.size()).thenReturn(1);
            when(mockElements.get(0)).thenReturn(mockElement);
            when(mockElement.selectFirst("span[class=author]")).thenReturn(mockElement);
            when(mockElement.text()).thenReturn("john ka");
            String image = "image";
            String name = "john ka";
            String link = "link";
            String idNovel ="id";
            String description = "description";
            when(mockElements.get(0).selectFirst("div[data-image]")).thenReturn(mockElement);
            when(mockElement.selectFirst("div[data-image]").attr("data-image")).thenReturn(image);
            when(mockElement.selectFirst("h3")).thenReturn(mockElement);
            when(mockElement.text()).thenReturn(name);
            when(mockElement.selectFirst("a")).thenReturn(mockElement);
            when(mockElement.attr("href")).thenReturn(link);
            try (MockedStatic<TruyenFullPlugin> mockedStaticTruyenFull = mockStatic(TruyenFullPlugin.class)) {
                mockedStaticTruyenFull.when(() -> truyenFullPlugin. getEndSlugFromUrl(link)).thenReturn(idNovel);
                mockedStatic.when(() -> ConnectJsoup.connect(link)).thenReturn(mockDocument);
                when(mockDocument.select("ul[class=list-chapter] li")).thenReturn(mockElements);
                when(mockElements.get(0).select("a")).thenReturn(mockElements);
                when(mockDocument.select("ul[class=list-chapter] li").get(0)).thenReturn(mockElement);
                when(mockElement.select("a").attr("href")).thenReturn("test/chuong-1");
                when(mockDocument.selectFirst("div[itemprop=description]")).thenReturn(mockElement);
                when(mockElement.toString()).thenReturn(description);
                DataResponse result = truyenFullPlugin.getAuthorDetail(authorId);
                assertNotNull(result);
                assertEquals("success", result.getStatus());
                assertEquals(1, result.getTotalPage());
                assertEquals(1, result.getCurrentPage());
                assertEquals(1,result.getPerPage());
                assertNotNull(result.getData());
                List<Novel> novels = (List<Novel>) result.getData();
                assertFalse(novels.isEmpty());
                Novel novel = novels.get(0);
                assertEquals(author.getName(), novel.getAuthor().getName());
            }
        }
    }
}
