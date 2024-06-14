package com.crawldata.demo.plugin_builder.tangthuvien;
import com.crawldata.back_end.model.Author;
import com.crawldata.back_end.model.Chapter;
import com.crawldata.back_end.model.Novel;
import com.crawldata.back_end.novel_plugin_builder.tangthuvien.TangThuVienPlugin;
import com.crawldata.back_end.response.DataResponse;
import com.crawldata.back_end.utils.ConnectJsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TangThuVienPluginTest {
    private final String ROOT_URL = "https://truyen.tangthuvien.vn/";
    private final String  NOVEL_DETAIL_URL = ROOT_URL + "/doc-truyen/%s";
    @InjectMocks
    private TangThuVienPlugin tangThuVienPlugin;

    @BeforeEach
    public void setUp()
    {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void testGetAuthorIdFromUrl() {
        String url = "https://truyen.tangthuvien.vn/tac-gia?author=123";
        String authorId = tangThuVienPlugin.getAuthorIdFromUrl(url);
        assertEquals("123", authorId);
    }

    @Test
    public void testGetNovelIdFromUrl() {
        String url = "https://truyen.tangthuvien.vn/doc-truyen/456";
        String novelId = tangThuVienPlugin.getNovelIdFromUrl(url);
        assertEquals("456", novelId);
    }

    @Test
    public void testGetTotalChapterFromText() {
        String text = "Danh sách chương (100 chương)";
        Integer totalChapters = tangThuVienPlugin.getTotalChapterFromText(text);
        assertEquals(100, totalChapters);
    }

    @Test
    public void testGetChapterIdFromUrl() {
        String url = "https://truyen.tangthuvien.vn/doc-truyen/456/chapter-1";
        String chapterId = tangThuVienPlugin.getChapterIdFromUrl(url);
        assertEquals("chapter-1", chapterId);
    }

    @Test
    public void testMapNovelInfo() throws IOException {
        // Mock necessary dependencies
        String novelId = "tri-menh-vu-kho";
        String novelDetailUrl = "https://truyen.tangthuvien.vn/doc-truyen/tri-menh-vu-kho";

        Document  mockDocument = mock(Document.class);
        Element mockElement = mock(Element.class);
        Elements mockElements = mock(Elements.class);

        // Mocking behavior for ConnectJsoup and Document
        try (MockedStatic<ConnectJsoup> mockedStatic = mockStatic(ConnectJsoup.class)) {
            mockedStatic.when(() -> ConnectJsoup.connect(novelDetailUrl)).thenReturn(mockDocument);
            when(mockDocument.select(".book-information.cf")).thenReturn(mockElements);
            when(mockElements.get(0)).thenReturn(mockElement);
            when(mockElement.child(1)).thenReturn(mockElement);
            when(mockElement.child(0)).thenReturn(mockElement);
            when(mockElement.text()).thenReturn("Test Novel");

            // Mock other necessary behaviors for successful testing
            // Execute the method under test
            Map<String, Object> novelInfo = tangThuVienPlugin.mapNovelInfo(novelId);

            // Assertions
            assertEquals("Test Novel", novelInfo.get("novelName"));
            // Add more assertions based on expected behavior
        }
    }

    @Test
    public void getNovelChapterDetail_invalidNovelId_error() throws IOException {
        String novelId = "invalid-novel-id";
        String chapterId = "valid-chapter-id";
        Novel novel = new Novel(novelId,"name","image","description",new Author("john-wick","john wick"),"chuong-1");
        Chapter chapter = new Chapter(chapterId,"name","chuong-1","chuong-2",null,"test chapter",novel.getAuthor(),"this is content");
        String chapterDetailUrl = String.format(NOVEL_DETAIL_URL, novelId) + "/" + chapterId;
        Map<String,Object> map = new HashMap<>();
        map.put("novelName",novel.getName());
        map.put("author",novel.getAuthor());
        map.put("total",1);
        map.put("storyId",novel.getNovelId());
        map.put("preChapter",chapter.getPreChapterId());
        map.put("nextChapter",chapter.getNextChapterId());
        map.put("chapterName",chapter.getName());
        map.put("content",chapter.getContent());

        doReturn(null).when(tangThuVienPlugin).mapNovelInfo(novelId);
        doReturn(map).when(tangThuVienPlugin).getAdjacentChapters(novelId,chapterId);
        doReturn(map).when(tangThuVienPlugin).getContentChapter(chapterDetailUrl);
        DataResponse result = tangThuVienPlugin.getNovelChapterDetail(novelId,chapterId);
        assertNotNull(result);

        assertEquals("success", result.getStatus());
        assertEquals(null, result.getTotalPage());
        assertEquals(null, result.getCurrentPage());
        assertNotNull(result.getData());
        Chapter value = (Chapter) result.getData();
        assertEquals("this is content", value.getContent());
        assertEquals(chapter.getName(), value.getName());
        assertEquals(chapter.getNovelName(),value.getNovelName());
    }
    @Test
    void testGetNovelListChaptersSuccess() throws IOException {
        String novelId = "novelId1";
        String chapterName = "name1";
        String novelName = "novel1";
        String storyId = "storyId1";
        String expectedStatus = "success";
        String fromChapter = "chuong-1";

        List<Chapter> chapters = new ArrayList<>();

        chapters.add(new Chapter().novelId(novelId).novelName(novelName).chapterId(fromChapter).name(chapterName).author(new Author("john-wick","john wick")));
        int numChapter = 1;

        Map<String,Object> map = new HashMap<>();
        map.put("novelName",novelName);
        map.put("author", chapters.get(0).getAuthor());
        map.put("storyId", storyId);

        doReturn(map).when(tangThuVienPlugin).mapNovelInfo(novelId);
        doReturn(chapters).when(tangThuVienPlugin).getAllChaptersImpl(storyId);

        // Calling the method under test
        DataResponse response = tangThuVienPlugin.getNovelListChapters(novelId, fromChapter, numChapter);
        // Assertions
        assertNotNull(response);
        assertEquals(expectedStatus, response.getStatus());
        assertNotNull(response.getData());
        List<Chapter> chaptersRS = (List<Chapter>) response.getData();
        assertEquals(1, chaptersRS.size());
    }
    @Test
    void testGetNovelListChaptersError() throws IOException {
        String novelId = "Invalid novel id";
        String chapterName = "name1";
        String novelName = "novel1";
        String storyId = "storyId1";
        String expectedStatus = "error";
        String fromChapter = "chuong-1";

        List<Chapter> chapters = new ArrayList<>();

        chapters.add(new Chapter().novelId(novelId).novelName(novelName).chapterId(fromChapter).name(chapterName).author(new Author("john-wick","john wick")));
        int numChapter = 1;


        doReturn(null).when(tangThuVienPlugin).mapNovelInfo(novelId);
        doReturn(chapters).when(tangThuVienPlugin).getAllChaptersImpl(storyId);

        // Calling the method under test
        DataResponse response = tangThuVienPlugin.getNovelListChapters(novelId, fromChapter, numChapter);
        // Assertions
        assertNotNull(response);
        assertEquals(expectedStatus, response.getStatus());
    }
    @Test
    void testGetAllNovelListChaptersSuccess() {
        String novelId = "dai-dao-ky";

        String expectedStatus = "error";
        String fromChapter = "chuong-1";
        int numChapter = 10;

        // Calling the method under test
        DataResponse response = tangThuVienPlugin.getNovelListChapters(novelId, fromChapter, numChapter);
        // Assertions
        assertNotNull(response);
        assertEquals(expectedStatus, response.getStatus());
    }
    @Test
    void testGetAllNovelListChaptersError() {
        String novelId = "dai-dao-ky";

        String expectedStatus = "error";
        String fromChapter = "chuong-1";
        int numChapter = 10;

        // Calling the method under test
        DataResponse response = tangThuVienPlugin.getNovelListChapters(novelId, fromChapter, numChapter);
        // Assertions
        assertNotNull(response);
        assertEquals(expectedStatus, response.getStatus());
    }
    @Test
    void getNovelsPerPageSuccess()
    {
        String expectedStatus = "success";
        int currentPage = 1;
        int totalPage = 599;
        int perPage = 20;
        int size = 20;
        DataResponse dataResponse = tangThuVienPlugin.getAllNovels(1, "");
        List<Novel> novels = (List<Novel>) dataResponse.getData();
        // Assertions
        assertNotNull(dataResponse);
        assertEquals(expectedStatus,dataResponse.getStatus());
        assertEquals(currentPage, dataResponse.getCurrentPage());
        assertEquals(totalPage, dataResponse.getTotalPage());
        assertEquals(perPage, dataResponse.getPerPage());
        assertEquals(size, novels.size());
    }

    @Test
    void getNovelsPerPageError()
    {
        String expectedStatus = "error";
        DataResponse dataResponse = tangThuVienPlugin.getAllNovels(600, "");
        // Assertions
        assertNotNull(dataResponse);
        assertEquals(expectedStatus,dataResponse.getStatus());
    }

    @Test
    void getNovelSearchSuccess()
    {
        String keyword = "kiếm";
        int page = 1;
        String orderBy = "a-z";
        DataResponse dataResponse = tangThuVienPlugin.getNovelSearch(page, keyword, orderBy);
        List<Novel> novels = (List<Novel>) dataResponse.getData();
        String expectedStatus = "success";
        int currentPage = 1;
        int totalPage = 27;
        String searchValue = "kiếm";
        int size = 20;
        // Assertions
        assertNotNull(dataResponse);
        assertEquals(expectedStatus,dataResponse.getStatus());
        assertEquals(totalPage, dataResponse.getTotalPage());
        assertEquals(currentPage, dataResponse.getCurrentPage());
        assertEquals(searchValue, dataResponse.getSearchValue());
        assertEquals(size, novels.size());
    }

    @Test
    void getNovelSearchError1()
    {
        String keyword = "kiếm";
        int page = 1000;
        String orderBy = "a-z";
        DataResponse dataResponse = tangThuVienPlugin.getNovelSearch(page, keyword, orderBy);
        String expectedStatus = "error";
        // Assertions
        assertNotNull(dataResponse);
        assertEquals(expectedStatus,dataResponse.getStatus());
    }

    @Test
    void getNovelSearchError2()
    {
        String keyword = "zzzzzzzzzzzzzzzzzz";
        int page = 1000;
        String orderBy = "a-z";
        DataResponse dataResponse = tangThuVienPlugin.getNovelSearch(page, keyword, orderBy);
        String expectedStatus = "error";
        // Assertions
        assertNotNull(dataResponse);
        assertEquals(expectedStatus,dataResponse.getStatus());
    }


}