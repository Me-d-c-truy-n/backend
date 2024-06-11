//package com.crawldata.demo.plugin_builder.metruyencv;
//
//import com.crawldata.back_end.model.Novel;
//import com.crawldata.back_end.plugin_builder.metruyencv.MeTruyenChuPlugin;
//import com.crawldata.back_end.plugin_builder.truyenfull.TruyenFullPlugin;
//import com.crawldata.back_end.response.DataResponse;
//import com.crawldata.back_end.utils.ConnectJsoup;
//import com.crawldata.back_end.utils.HandleString;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.mockito.MockitoAnnotations;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//import static org.mockito.Mockito.mockStatic;
//
//public class MeTruyenChuPluginTest {
//    @InjectMocks
//    private MeTruyenChuPlugin meTruyenChuPlugin;
//
//    @Mock
//    private ConnectJsoup connectJsoupMock;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//
//    @Test
//    public void getAllNovels_onlyOnePage_success() {
//
//    }
//
//    @Test
//    public void getAllNovels_manyPages_success() {
//
//    }
//
//    @Test
//    public void getNovelSearch_emptyKey_success() {
//
//    }
//
//    @Test
//    public void getNovelSearch_failedGetDataFromAPI_error() {
//
//    }
//
//    @Test
//    public void getAuthorDetail_validAuthorId_success() {
//        String authorId = "valid-author-id";
//        String url = "https://truyenfull.vn/tac-gia/" + authorId;
//        Document mockDocument = mock(Document.class);
//        Author author = new Author("john-ka", "john ka");
//        Elements mockElements = mock(Elements.class);
//        Element mockElement = mock(Element.class);
//        try (MockedStatic<ConnectJsoup> mockedStatic = mockStatic(ConnectJsoup.class)) {
//            mockedStatic.when(() -> ConnectJsoup.connect(url)).thenReturn(mockDocument);
//            when(mockDocument.select("div[itemtype=https://schema.org/Book]")).thenReturn(mockElements);
//            when(mockElements.size()).thenReturn(1);
//            when(mockElements.get(0)).thenReturn(mockElement);
//            when(mockElement.selectFirst("span[class=author]")).thenReturn(mockElement);
//            when(mockElement.text()).thenReturn("john ka");
//            String image = "image";
//            String name = "john ka";
//            String link = "link";
//            String idNovel = "id";
//            String description = "description";
//            when(mockElements.get(0).selectFirst("div[data-image]")).thenReturn(mockElement);
//            when(mockElement.selectFirst("div[data-image]").attr("data-image")).thenReturn(image);
//            when(mockElement.selectFirst("h3")).thenReturn(mockElement);
//            when(mockElement.text()).thenReturn(name);
//            when(mockElement.selectFirst("a")).thenReturn(mockElement);
//            when(mockElement.attr("href")).thenReturn(link);
//            try (MockedStatic<TruyenFullPlugin> mockedStaticTruyenFull = mockStatic(TruyenFullPlugin.class)) {
//                mockedStaticTruyenFull.when(() -> truyenFullPlugin.getEndSlugFromUrl(link)).thenReturn(idNovel);
//                mockedStatic.when(() -> ConnectJsoup.connect(link)).thenReturn(mockDocument);
//                when(mockDocument.select("ul[class=list-chapter] li")).thenReturn(mockElements);
//                when(mockElements.get(0).select("a")).thenReturn(mockElements);
//                when(mockDocument.select("ul[class=list-chapter] li").get(0)).thenReturn(mockElement);
//                when(mockElement.select("a").attr("href")).thenReturn("test/chuong-1");
//                when(mockDocument.selectFirst("div[itemprop=description]")).thenReturn(mockElement);
//                when(mockElement.toString()).thenReturn(description);
//                DataResponse result = truyenFullPlugin.getAuthorDetail(authorId);
//                assertNotNull(result);
//                assertEquals("success", result.getStatus());
//                assertEquals(1, result.getTotalPage());
//                assertEquals(1, result.getCurrentPage());
//                assertEquals(1, result.getPerPage());
//                assertNotNull(result.getData());
//                List<Novel> novels = (List<Novel>) result.getData();
//                assertFalse(novels.isEmpty());
//                Novel novel = novels.get(0);
//                assertEquals(author.getName(), novel.getAuthor().getName());
//                assertEquals(image, novel.getImage());
//                assertEquals(description, novel.getDescription());
//            }
//        }
//    }
//
//    @Test
//    public void getAuthorDetail_inValidAuthorId_error() {
//        String authorId = "in-valid-author-id";
//        String url = "https://truyenfull.vn/tac-gia/" + authorId;
//        Document mockDocument = mock(Document.class);
//        Author author = new Author("john-ka", "john ka");
//        Elements mockElements = mock(Elements.class);
//        Element mockElement = mock(Element.class);
//        try (MockedStatic<ConnectJsoup> mockedStatic = mockStatic(ConnectJsoup.class)) {
//            mockedStatic.when(() -> ConnectJsoup.connect(url)).thenReturn(null);
//            when(mockDocument.select("div[itemtype=https://schema.org/Book]")).thenReturn(mockElements);
//            when(mockElements.size()).thenReturn(1);
//            when(mockElements.get(0)).thenReturn(mockElement);
//            when(mockElement.selectFirst("span[class=author]")).thenReturn(mockElement);
//            when(mockElement.text()).thenReturn("john ka");
//            String image = "image";
//            String name = "john ka";
//            String link = "link";
//            String idNovel = "id";
//            String description = "description";
//            when(mockElements.get(0).selectFirst("div[data-image]")).thenReturn(mockElement);
//            when(mockElement.selectFirst("div[data-image]").attr("data-image")).thenReturn(image);
//            when(mockElement.selectFirst("h3")).thenReturn(mockElement);
//            when(mockElement.text()).thenReturn(name);
//            when(mockElement.selectFirst("a")).thenReturn(mockElement);
//            when(mockElement.attr("href")).thenReturn(link);
//            try (MockedStatic<TruyenFullPlugin> mockedStaticTruyenFull = mockStatic(TruyenFullPlugin.class)) {
//                mockedStaticTruyenFull.when(() -> truyenFullPlugin.getEndSlugFromUrl(link)).thenReturn(idNovel);
//                mockedStatic.when(() -> ConnectJsoup.connect(link)).thenReturn(mockDocument);
//                when(mockDocument.select("ul[class=list-chapter] li")).thenReturn(mockElements);
//                when(mockElements.get(0).select("a")).thenReturn(mockElements);
//                when(mockDocument.select("ul[class=list-chapter] li").get(0)).thenReturn(mockElement);
//                when(mockElement.select("a").attr("href")).thenReturn("test/chuong-1");
//                when(mockDocument.selectFirst("div[itemprop=description]")).thenReturn(mockElement);
//                when(mockElement.toString()).thenReturn(description);
//                DataResponse result = truyenFullPlugin.getAuthorDetail(authorId);
//                assertNotNull(result);
//                assertEquals("success", result.getStatus());
//                assertEquals(1, result.getTotalPage());
//                assertEquals(1, result.getCurrentPage());
//                assertEquals(1, result.getPerPage());
//                assertNotNull(result.getData());
//                List<Novel> novels = (List<Novel>) result.getData();
//                assertFalse(novels.isEmpty());
//                Novel novel = novels.get(0);
//                assertEquals(author.getName(), novel.getAuthor().getName());
//                assertEquals(image, novel.getImage());
//                assertEquals(description, novel.getDescription());
//            }
//        }
//    }
//
//    @Test
//    public void getNovelDetail_validNovelId_success() {
//        String novelId = "valid-novel-id";
//        String url = "https://truyenfull.vn/" + novelId;
//        String nameAuthor = "john wick";
//        String nameAuthorSlug = "john-wick";
//        String image = "expect image";
//        String description = "expect description";
//        String nameNovel = "expect name novel";
//        Document mockDocument = mock(Document.class);
//        Elements mockElements = mock(Elements.class);
//        Element mockElement = mock(Element.class);
//        Elements mockTitleElements = mock(Elements.class);
//        Element mockTitleElement = mock(Element.class);
//        try (MockedStatic<ConnectJsoup> mockedStatic = mockStatic(ConnectJsoup.class)) {
//            mockedStatic.when(() -> ConnectJsoup.connect(url)).thenReturn(mockDocument);
//            when(mockDocument.select("h3[class=title]")).thenReturn(mockTitleElements);
//            when(mockTitleElements.first()).thenReturn(mockTitleElement);
//            when(mockTitleElement.text()).thenReturn(nameNovel);
//            when(mockDocument.select("a[itemprop=author]")).thenReturn(mockElements);
//            when(mockElements.first()).thenReturn(mockElement);
//            when(mockElement.text()).thenReturn(nameAuthor);
//            try (MockedStatic<HandleString> mockedStaticHandle = mockStatic(HandleString.class)) {
//                mockedStaticHandle.when(() -> HandleString.makeSlug(nameAuthor)).thenReturn(nameAuthorSlug);
//                when(mockDocument.select("ul[class=list-chapter] li")).thenReturn(mockElements);
//                when(mockElements.get(0)).thenReturn(mockElement);
//                when(mockElement.select("a")).thenReturn(mockElements);
//                when(mockDocument.select("ul[class=list-chapter] li").get(0)).thenReturn(mockElement);
//                when(mockElement.select("a").attr("href")).thenReturn("test/chuong-1");
//                when(mockDocument.selectFirst("img")).thenReturn(mockElement);
//                when(mockElement.attr("src")).thenReturn(image);
//                when(mockDocument.selectFirst("div[itemprop=description]")).thenReturn(mockElement);
//                when(mockElement.toString()).thenReturn(description);
//                DataResponse result = truyenFullPlugin.getNovelDetail(novelId);
//                assertNotNull(result);
//                assertEquals("success", result.getStatus());
//                assertNotNull(result.getData());
//                Novel novel = (Novel) result.getData();
//                assertEquals(novel.getNovelId(), novelId);
//                assertEquals(novel.getFirstChapter(), "chuong-1");
//                assertEquals(novel.getName(), nameNovel);
//                assertEquals(nameAuthor, novel.getAuthor().getName());
//                assertEquals(image, novel.getImage());
//                assertEquals(description, novel.getDescription());
//            }
//        }
//    }
//
//    @Test
//    public void getNovelDetail_inValidNovelId_error() {
//        String novelId = "in_valid-novel-id";
//        String url = "https://truyenfull.vn/" + novelId;
//        String nameAuthor = "john wick";
//        String nameAuthorSlug = "john-wick";
//        String image = "expect image";
//        String description = "expect description";
//        String nameNovel = "expect name novel";
//        Document mockDocument = mock(Document.class);
//        Elements mockElements = mock(Elements.class);
//        Element mockElement = mock(Element.class);
//        Elements mockTitleElements = mock(Elements.class);
//        Element mockTitleElement = mock(Element.class);
//        try (MockedStatic<ConnectJsoup> mockedStatic = mockStatic(ConnectJsoup.class)) {
//            mockedStatic.when(() -> ConnectJsoup.connect(url)).thenReturn(null);
//            when(mockDocument.select("h3[class=title]")).thenReturn(mockTitleElements);
//            when(mockTitleElements.first()).thenReturn(mockTitleElement);
//            when(mockTitleElement.text()).thenReturn(nameNovel);
//            when(mockDocument.select("a[itemprop=author]")).thenReturn(mockElements);
//            when(mockElements.first()).thenReturn(mockElement);
//            when(mockElement.text()).thenReturn(nameAuthor);
//            try (MockedStatic<HandleString> mockedStaticHandle = mockStatic(HandleString.class)) {
//                mockedStaticHandle.when(() -> HandleString.makeSlug(nameAuthor)).thenReturn(nameAuthorSlug);
//                when(mockDocument.select("ul[class=list-chapter] li")).thenReturn(mockElements);
//                when(mockElements.get(0)).thenReturn(mockElement);
//                when(mockElement.select("a")).thenReturn(mockElements);
//                when(mockDocument.select("ul[class=list-chapter] li").get(0)).thenReturn(mockElement);
//                when(mockElement.select("a").attr("href")).thenReturn("test/chuong-1");
//                when(mockDocument.selectFirst("img")).thenReturn(mockElement);
//                when(mockElement.attr("src")).thenReturn(image);
//                when(mockDocument.selectFirst("div[itemprop=description]")).thenReturn(mockElement);
//                when(mockElement.toString()).thenReturn(description);
//                DataResponse result = truyenFullPlugin.getNovelDetail(novelId);
//                assertNotNull(result);
//                assertEquals("success", result.getStatus());
//                assertNotNull(result.getData());
//                Novel novel = (Novel) result.getData();
//                assertEquals(novel.getNovelId(), novelId);
//                assertEquals(novel.getFirstChapter(), "chuong-1");
//                assertEquals(novel.getName(), nameNovel);
//                assertEquals(nameAuthor, novel.getAuthor().getName());
//                assertEquals(image, novel.getImage());
//                assertEquals(description, novel.getDescription());
//            }
//        }
//    }
//
//    @Test
//    public void getNovelListChapters_validNovelIdAndPage_success() {
//        String novelId = "valid-novel-id";
//        int page = 1;
//        String urlNovel = "https://truyenfull.vn/" + novelId;
//        String url = String.format("https://truyenfull.vn/%s/trang-%d", novelId, page);
//        String nameAuthor = "john wick";
//        String nameAuthorSlug = "john-wick";
//        String name = "expect name chapter";
//        String nameChapter = "expect name chapter";
//        String linkChapter = "test/chuong-1";
//        String idChapter = "chuong-1";
//        String idNextChapter = "next";
//        String idPreChapter = "pre";
//        int totalPage = 2;
//        int chapterEnd = 2;
//        Document mockDocument = mock(Document.class);
//        Elements mockElements = mock(Elements.class);
//        Element mockElement = mock(Element.class);
//        Elements mockTitleElements = mock(Elements.class);
//        Element mockTitleElement = mock(Element.class);
//        try (MockedStatic<ConnectJsoup> mockedStatic = mockStatic(ConnectJsoup.class)) {
//            mockedStatic.when(() -> ConnectJsoup.connect(urlNovel)).thenReturn(mockDocument);
//            when(mockDocument.select("h3[class=title]")).thenReturn(mockTitleElements);
//            when(mockTitleElements.first()).thenReturn(mockTitleElement);
//            when(mockTitleElement.text()).thenReturn(name);
//            when(mockDocument.select("a[itemprop=author]")).thenReturn(mockElements);
//            when(mockElements.first()).thenReturn(mockElement);
//            when(mockElement.text()).thenReturn(nameAuthor);
//            mockedStatic.when(() -> ConnectJsoup.connect(url)).thenReturn(mockDocument);
//            try (MockedStatic<HandleString> mockedStaticHandle = mockStatic(HandleString.class)) {
//                mockedStaticHandle.when(() -> HandleString.makeSlug(nameAuthor)).thenReturn(nameAuthorSlug);
//                when(mockDocument.select("ul[class=list-chapter] li")).thenReturn(mockElements);
//                when(mockElements.size()).thenReturn(1);
//                try (MockedStatic<TruyenFullPlugin> mockedStaticTruyen = mockStatic(TruyenFullPlugin.class)) {
//                    mockedStaticTruyen.when(() -> truyenFullPlugin.getNovelTotalPages(urlNovel)).thenReturn(totalPage);
//                    mockedStaticTruyen.when(() -> truyenFullPlugin.getChapterEnd(urlNovel)).thenReturn(chapterEnd);
//                    when(mockElements.get(0)).thenReturn(mockElement);
//                    when(mockElement.selectFirst("a")).thenReturn(mockElement);
//                    when(mockElement.text()).thenReturn(nameChapter);
//                    when(mockElement.attr("href")).thenReturn(linkChapter);
//                    mockedStaticTruyen.when(() -> truyenFullPlugin.getValidPreChapter(idChapter)).thenReturn(idPreChapter);
//                    mockedStaticTruyen.when(() -> truyenFullPlugin.getValidNextChapter(idChapter, totalPage)).thenReturn(idNextChapter);
//                    DataResponse result = truyenFullPlugin.getNovelListChapters(novelId, page);
//                    assertNotNull(result);
//                    assertEquals("success", result.getStatus());
//                    assertEquals(2, result.getTotalPage());
//                    assertEquals(1, result.getCurrentPage());
//                    assertEquals(1, result.getPerPage());
//                    assertNotNull(result.getData());
//                    List<Chapter> chapters = (List<Chapter>) result.getData();
//                    assertFalse(chapters.isEmpty());
//                    Chapter chapter = chapters.get(0);
//                    assertEquals(idChapter, chapter.getChapterId());
//                    assertEquals(nameChapter, chapter.getName());
//                    assertEquals(idNextChapter, chapter.getNextChapterId());
//                    assertEquals(idPreChapter, chapter.getPreChapterId());
//                }
//            }
//        }
//    }
//
//    @Test
//    public void getNovelListChapters_inValidNovelId_error() {
//        String novelId = "in_valid-novel-id";
//        int page = 1;
//        String urlNovel = "https://truyenfull.vn/" + novelId;
//        String url = String.format("https://truyenfull.vn/%s/trang-%d", novelId, page);
//        String nameAuthor = "john wick";
//        String nameAuthorSlug = "john-wick";
//        String name = "expect name chapter";
//        String nameChapter = "expect name chapter";
//        String linkChapter = "test/chuong-1";
//        String idChapter = "chuong-1";
//        String idNextChapter = "next";
//        String idPreChapter = "pre";
//        int totalPage = 2;
//        int chapterEnd = 2;
//        Document mockDocument = mock(Document.class);
//        Elements mockElements = mock(Elements.class);
//        Element mockElement = mock(Element.class);
//        Elements mockTitleElements = mock(Elements.class);
//        Element mockTitleElement = mock(Element.class);
//        try (MockedStatic<ConnectJsoup> mockedStatic = mockStatic(ConnectJsoup.class)) {
//            mockedStatic.when(() -> ConnectJsoup.connect(urlNovel)).thenReturn(null);
//            when(mockDocument.select("h3[class=title]")).thenReturn(mockTitleElements);
//            when(mockTitleElements.first()).thenReturn(mockTitleElement);
//            when(mockTitleElement.text()).thenReturn(name);
//            when(mockDocument.select("a[itemprop=author]")).thenReturn(mockElements);
//            when(mockElements.first()).thenReturn(mockElement);
//            when(mockElement.text()).thenReturn(nameAuthor);
//            mockedStatic.when(() -> ConnectJsoup.connect(url)).thenReturn(mockDocument);
//            try (MockedStatic<HandleString> mockedStaticHandle = mockStatic(HandleString.class)) {
//                mockedStaticHandle.when(() -> HandleString.makeSlug(nameAuthor)).thenReturn(nameAuthorSlug);
//                when(mockDocument.select("ul[class=list-chapter] li")).thenReturn(mockElements);
//                when(mockElements.size()).thenReturn(1);
//                try (MockedStatic<TruyenFullPlugin> mockedStaticTruyen = mockStatic(TruyenFullPlugin.class)) {
//                    mockedStaticTruyen.when(() -> truyenFullPlugin.getNovelTotalPages(urlNovel)).thenReturn(totalPage);
//                    mockedStaticTruyen.when(() -> truyenFullPlugin.getChapterEnd(urlNovel)).thenReturn(chapterEnd);
//                    when(mockElements.get(0)).thenReturn(mockElement);
//                    when(mockElement.selectFirst("a")).thenReturn(mockElement);
//                    when(mockElement.text()).thenReturn(nameChapter);
//                    when(mockElement.attr("href")).thenReturn(linkChapter);
//                    mockedStaticTruyen.when(() -> truyenFullPlugin.getValidPreChapter(idChapter)).thenReturn(idPreChapter);
//                    mockedStaticTruyen.when(() -> truyenFullPlugin.getValidNextChapter(idChapter, totalPage)).thenReturn(idNextChapter);
//                    DataResponse result = truyenFullPlugin.getNovelListChapters(novelId, page);
//                    assertNotNull(result);
//                    assertEquals("success", result.getStatus());
//                    assertEquals(2, result.getTotalPage());
//                    assertEquals(1, result.getCurrentPage());
//                    assertEquals(1, result.getPerPage());
//                    assertNotNull(result.getData());
//                    List<Chapter> chapters = (List<Chapter>) result.getData();
//                    assertFalse(chapters.isEmpty());
//                    Chapter chapter = chapters.get(0);
//                    assertEquals(idChapter, chapter.getChapterId());
//                    assertEquals(nameChapter, chapter.getName());
//                    assertEquals(idNextChapter, chapter.getNextChapterId());
//                    assertEquals(idPreChapter, chapter.getPreChapterId());
//                }
//            }
//        }
//    }
//
//    @Test
//    public void getNovelListChapters_inValidPage_error() {
//        String novelId = "valid-novel-id";
//        int page = -2;
//        String urlNovel = "https://truyenfull.vn/" + novelId;
//        String url = String.format("https://truyenfull.vn/%s/trang-%d", novelId, page);
//        String nameAuthor = "john wick";
//        String nameAuthorSlug = "john-wick";
//        String name = "expect name chapter";
//        String nameChapter = "expect name chapter";
//        String linkChapter = "test/chuong-1";
//        String idChapter = "chuong-1";
//        String idNextChapter = "next";
//        String idPreChapter = "pre";
//        int totalPage = 2;
//        int chapterEnd = 2;
//        Document mockDocument = mock(Document.class);
//        Elements mockElements = mock(Elements.class);
//        Element mockElement = mock(Element.class);
//        Elements mockTitleElements = mock(Elements.class);
//        Element mockTitleElement = mock(Element.class);
//        try (MockedStatic<ConnectJsoup> mockedStatic = mockStatic(ConnectJsoup.class)) {
//            mockedStatic.when(() -> ConnectJsoup.connect(urlNovel)).thenReturn(null);
//            when(mockDocument.select("h3[class=title]")).thenReturn(mockTitleElements);
//            when(mockTitleElements.first()).thenReturn(mockTitleElement);
//            when(mockTitleElement.text()).thenReturn(name);
//            when(mockDocument.select("a[itemprop=author]")).thenReturn(mockElements);
//            when(mockElements.first()).thenReturn(mockElement);
//            when(mockElement.text()).thenReturn(nameAuthor);
//            mockedStatic.when(() -> ConnectJsoup.connect(url)).thenReturn(null);
//            try (MockedStatic<HandleString> mockedStaticHandle = mockStatic(HandleString.class)) {
//                mockedStaticHandle.when(() -> HandleString.makeSlug(nameAuthor)).thenReturn(nameAuthorSlug);
//                when(mockDocument.select("ul[class=list-chapter] li")).thenReturn(mockElements);
//                when(mockElements.size()).thenReturn(1);
//                try (MockedStatic<TruyenFullPlugin> mockedStaticTruyen = mockStatic(TruyenFullPlugin.class)) {
//                    mockedStaticTruyen.when(() -> truyenFullPlugin.getNovelTotalPages(urlNovel)).thenReturn(totalPage);
//                    mockedStaticTruyen.when(() -> truyenFullPlugin.getChapterEnd(urlNovel)).thenReturn(chapterEnd);
//                    when(mockElements.get(0)).thenReturn(mockElement);
//                    when(mockElement.selectFirst("a")).thenReturn(mockElement);
//                    when(mockElement.text()).thenReturn(nameChapter);
//                    when(mockElement.attr("href")).thenReturn(linkChapter);
//                    mockedStaticTruyen.when(() -> truyenFullPlugin.getValidPreChapter(idChapter)).thenReturn(idPreChapter);
//                    mockedStaticTruyen.when(() -> truyenFullPlugin.getValidNextChapter(idChapter, totalPage)).thenReturn(idNextChapter);
//                    DataResponse result = truyenFullPlugin.getNovelListChapters(novelId, page);
//                    assertNotNull(result);
//                    assertEquals("success", result.getStatus());
//                    assertEquals(2, result.getTotalPage());
//                    assertEquals(1, result.getCurrentPage());
//                    assertEquals(1, result.getPerPage());
//                    assertNotNull(result.getData());
//                    List<Chapter> chapters = (List<Chapter>) result.getData();
//                    assertFalse(chapters.isEmpty());
//                    Chapter chapter = chapters.get(0);
//                    assertEquals(idChapter, chapter.getChapterId());
//                    assertEquals(nameChapter, chapter.getName());
//                    assertEquals(idNextChapter, chapter.getNextChapterId());
//                    assertEquals(idPreChapter, chapter.getPreChapterId());
//                }
//            }
//        }
//    }
//
//    @Test
//    public void getNovelChapterDetail_validNovelIdAndChapterId_success() {
//        String novelId = "valid-novel-id";
//        String chapterId = "valid-chapter-id";
//        String urlChapter = "https://truyenfull.vn/" + novelId + "/" + chapterId;
//        String urlAuthor = "https://truyenfull.vn/" + novelId;
//        String nameAuthor = "john wick";
//        String name = "expect name novel";
//        String nameChapter = "expect name chapter";
//        String content = "this is content";
//        String nextChapterUrl = "/chuong-3";
//        String preChapterUrl = "/chuong-1";
//        Document mockDocument = mock(Document.class);
//        Elements mockElements = mock(Elements.class);
//        Element mockElement = mock(Element.class);
//        Elements mockTitleElements = mock(Elements.class);
//        Elements mockNextElements = mock(Elements.class);
//        Element mockTitleElement = mock(Element.class);
//        try (MockedStatic<ConnectJsoup> mockedStatic = mockStatic(ConnectJsoup.class)) {
//            mockedStatic.when(() -> ConnectJsoup.connect(urlAuthor)).thenReturn(mockDocument);
//            when(mockDocument.select("a[itemprop=author]")).thenReturn(mockElements);
//            when(mockElements.text()).thenReturn(nameAuthor);
//            mockedStatic.when(() -> ConnectJsoup.connect(urlChapter)).thenReturn(mockDocument);
//            when(mockDocument.select("a[class=truyen-title]")).thenReturn(mockTitleElements);
//            when(mockTitleElements.first()).thenReturn(mockTitleElement);
//            when(mockTitleElement.text()).thenReturn(name);
//            when(mockDocument.select("a[class=chapter-title]")).thenReturn(mockElements);
//            when(mockElements.first()).thenReturn(mockElement);
//            when(mockElement.text()).thenReturn(nameChapter);
//            when(mockDocument.select("div#chapter-c")).thenReturn(mockElements);
//            when(mockElements.toString()).thenReturn(content);
//            when(mockDocument.select("a[id=next_chap]")).thenReturn(mockNextElements);
//            when(mockNextElements.attr("href")).thenReturn(nextChapterUrl);
//            when(mockDocument.select("a[id=prev_chap]")).thenReturn(mockElements);
//            when(mockElements.attr("href")).thenReturn(preChapterUrl);
//            DataResponse result = truyenFullPlugin.getNovelChapterDetail(novelId, chapterId);
//            assertNotNull(result);
//            assertNotNull(result.getData());
//            Chapter chapter = (Chapter) result.getData();
//            assertEquals(name,chapter.getNovelName());
//            assertEquals(nameChapter,chapter.getName());
//            assertEquals(content,chapter.getContent());
//            assertEquals(chapter.getContent(),content);
//            assertEquals("chuong-1",chapter.getPreChapterId());
//            assertEquals("chuong-3",chapter.getNextChapterId());
//
//        }
//    }
//
//    @Test
//    public void getNovelChapterDetail_inValidNovelId_error() {
//        String novelId = "in_valid-novel-id";
//        String chapterId = "valid-chapter-id";
//        String urlChapter = "https://truyenfull.vn/" + novelId + "/" + chapterId;
//        String urlAuthor = "https://truyenfull.vn/" + novelId;
//        String nameAuthor = "john wick";
//        String name = "expect name novel";
//        String nameChapter = "expect name chapter";
//        String content = "this is content";
//        String nextChapterUrl = "/chuong-3";
//        String preChapterUrl = "/chuong-1";
//        Document mockDocument = mock(Document.class);
//        Elements mockElements = mock(Elements.class);
//        Element mockElement = mock(Element.class);
//        Elements mockTitleElements = mock(Elements.class);
//        Elements mockNextElements = mock(Elements.class);
//        Element mockTitleElement = mock(Element.class);
//        try (MockedStatic<ConnectJsoup> mockedStatic = mockStatic(ConnectJsoup.class)) {
//            mockedStatic.when(() -> ConnectJsoup.connect(urlAuthor)).thenReturn(null);
//            when(mockDocument.select("a[itemprop=author]")).thenReturn(mockElements);
//            when(mockElements.text()).thenReturn(nameAuthor);
//            mockedStatic.when(() -> ConnectJsoup.connect(urlChapter)).thenReturn(null);
//            when(mockDocument.select("a[class=truyen-title]")).thenReturn(mockTitleElements);
//            when(mockTitleElements.first()).thenReturn(mockTitleElement);
//            when(mockTitleElement.text()).thenReturn(name);
//            when(mockDocument.select("a[class=chapter-title]")).thenReturn(mockElements);
//            when(mockElements.first()).thenReturn(mockElement);
//            when(mockElement.text()).thenReturn(nameChapter);
//            when(mockDocument.select("div#chapter-c")).thenReturn(mockElements);
//            when(mockElements.toString()).thenReturn(content);
//            when(mockDocument.select("a[id=next_chap]")).thenReturn(mockNextElements);
//            when(mockNextElements.attr("href")).thenReturn(nextChapterUrl);
//            when(mockDocument.select("a[id=prev_chap]")).thenReturn(mockElements);
//            when(mockElements.attr("href")).thenReturn(preChapterUrl);
//            DataResponse result = truyenFullPlugin.getNovelChapterDetail(novelId, chapterId);
//            assertNotNull(result);
//            assertEquals("success", result.getStatus());
//            assertNotNull(result.getData());
//            Chapter chapter = (Chapter) result.getData();
//            assertEquals(name,chapter.getNovelName());
//            assertEquals(nameChapter,chapter.getName());
//            assertEquals(content,chapter.getContent());
//            assertEquals(chapter.getContent(),content);
//            assertEquals("chuong-1",chapter.getPreChapterId());
//            assertEquals("chuong-3",chapter.getNextChapterId());
//
//        }
//    }
//
//    @Test
//    public void getNovelChapterDetail_inChapterId_error() {
//        String novelId = "valid-novel-id";
//        String chapterId = "in_valid-chapter-id";
//        String urlChapter = "https://truyenfull.vn/" + novelId + "/" + chapterId;
//        String urlAuthor = "https://truyenfull.vn/" + novelId;
//        String nameAuthor = "john wick";
//        String name = "expect name novel";
//        String nameChapter = "expect name chapter";
//        String content = "this is content";
//        String nextChapterUrl = "/chuong-3";
//        String preChapterUrl = "/chuong-1";
//        Document mockDocument = mock(Document.class);
//        Elements mockElements = mock(Elements.class);
//        Element mockElement = mock(Element.class);
//        Elements mockTitleElements = mock(Elements.class);
//        Elements mockNextElements = mock(Elements.class);
//        Element mockTitleElement = mock(Element.class);
//        try (MockedStatic<ConnectJsoup> mockedStatic = mockStatic(ConnectJsoup.class)) {
//            mockedStatic.when(() -> ConnectJsoup.connect(urlAuthor)).thenReturn(mockDocument);
//            when(mockDocument.select("a[itemprop=author]")).thenReturn(mockElements);
//            when(mockElements.text()).thenReturn(nameAuthor);
//            mockedStatic.when(() -> ConnectJsoup.connect(urlChapter)).thenReturn(null);
//            when(mockDocument.select("a[class=truyen-title]")).thenReturn(mockTitleElements);
//            when(mockTitleElements.first()).thenReturn(mockTitleElement);
//            when(mockTitleElement.text()).thenReturn(name);
//            when(mockDocument.select("a[class=chapter-title]")).thenReturn(mockElements);
//            when(mockElements.first()).thenReturn(mockElement);
//            when(mockElement.text()).thenReturn(nameChapter);
//            when(mockDocument.select("div#chapter-c")).thenReturn(mockElements);
//            when(mockElements.toString()).thenReturn(content);
//            when(mockDocument.select("a[id=next_chap]")).thenReturn(mockNextElements);
//            when(mockNextElements.attr("href")).thenReturn(nextChapterUrl);
//            when(mockDocument.select("a[id=prev_chap]")).thenReturn(mockElements);
//            when(mockElements.attr("href")).thenReturn(preChapterUrl);
//            DataResponse result = truyenFullPlugin.getNovelChapterDetail(novelId, chapterId);
//            assertNotNull(result);
//            assertEquals("success", result.getStatus());
//            assertNotNull(result.getData());
//            Chapter chapter = (Chapter) result.getData();
//            assertEquals(name,chapter.getNovelName());
//            assertEquals(nameChapter,chapter.getName());
//            assertEquals(content,chapter.getContent());
//            assertEquals(chapter.getContent(),content);
//            assertEquals("chuong-1",chapter.getPreChapterId());
//            assertEquals("chuong-3",chapter.getNextChapterId());
//
//        }
//    }
//}
