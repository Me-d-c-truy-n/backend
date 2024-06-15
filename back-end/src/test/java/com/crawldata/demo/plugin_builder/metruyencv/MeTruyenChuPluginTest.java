package com.crawldata.demo.plugin_builder.metruyencv;

import com.crawldata.back_end.model.Author;
import com.crawldata.back_end.model.Chapter;
import com.crawldata.back_end.model.Novel;
import com.crawldata.back_end.novel_plugin_builder.metruyencv.MeTruyenChuPlugin;
import com.crawldata.back_end.response.DataResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

public class MeTruyenChuPluginTest {
    @Spy
    private MeTruyenChuPlugin meTruyenChuPlugin;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void getAllNovels_whenApiCallSucceeds_success() {
        // Arrange
        int page = 1;
        String search = "test";
        String apiUrl = String.format("https://backend.metruyencv.com/api/books?include=author&sort=-view_count&limit=20&page=%s&filter[state]=published", page);
        Novel novel = new Novel("1", "name", "image", "description", new Author("john-wick", "john wick"), "chuong-1");
        JsonObject mockJsonObject = new JsonObject();
        JsonObject mockPagination = new JsonObject();
        mockJsonObject.add("pagination", mockPagination);
        JsonArray mockDataArray = new JsonArray();
        mockJsonObject.add("data", mockDataArray);
        mockPagination.addProperty("last", 5); // Assuming there are 5 pages in total
        JsonObject mockNovelObject = new JsonObject();
        mockNovelObject.addProperty("name", "Test Novel");
        mockNovelObject.addProperty("slug", "test-novel");
        mockDataArray.add(mockNovelObject);

        doReturn(mockJsonObject).when(meTruyenChuPlugin).connectAPI(apiUrl);
        doReturn(novel).when(meTruyenChuPlugin).createNovelByJsonData(mockNovelObject);

        DataResponse result = meTruyenChuPlugin.getAllNovels(page, search);

        assertNotNull(result);
        assertEquals("success", result.getStatus());
        assertEquals(5, result.getTotalPage());
        assertEquals(1, result.getCurrentPage());
        assertNotNull(result.getData());
        List<Novel> novels = (List<Novel>) result.getData();
        assertFalse(novels.isEmpty());
        Novel value = novels.get(0);
        assertEquals("name", value.getName());
        assertEquals("description", value.getDescription());
        assertEquals("image", value.getImage());
        assertEquals("john wick", value.getAuthor().getName());

        verify(meTruyenChuPlugin).connectAPI(apiUrl);
        verify(meTruyenChuPlugin).createNovelByJsonData(mockNovelObject);
    }

    @Test
    public void getAllNovels_whenApiCallFailed_error() {
        int page = 1;
        String search = "test";
        String apiUrl = String.format("https://backend.metruyencv.com/api/books?include=author&sort=-view_count&limit=20&page=%s&filter[state]=published", page);
        JsonObject mockJsonObject = new JsonObject();
        JsonObject mockPagination = new JsonObject();
        mockJsonObject.add("pagination", mockPagination);
        JsonArray mockDataArray = new JsonArray();
        mockJsonObject.add("data", mockDataArray);
        mockPagination.addProperty("last", 5); // Assuming there are 5 pages in total
        JsonObject mockNovelObject = new JsonObject();
        mockNovelObject.addProperty("name", "Test Novel");
        mockNovelObject.addProperty("slug", "test-novel");
        mockDataArray.add(mockNovelObject);

        doReturn(null).when(meTruyenChuPlugin).connectAPI(apiUrl);

        DataResponse result = meTruyenChuPlugin.getAllNovels(page, search);

        assertEquals("error", result.getStatus());
        assertNull(result.getData());

        verify(meTruyenChuPlugin).connectAPI(apiUrl);
    }

    @Test
    public void getNovelSearch_whenApiCallSucceeds_success() {
        // Arrange
        int page = 1;
        String search = "test";
        String apiUrl = "https://backend.metruyencv.com/api/books/search?keyword=test&limit=20&page=1&sort=-view_count&filter[state]=published";
        Novel novel = new Novel("1", "name", "image", "description", new Author("john-wick", "john wick"), "chuong-1");
        JsonObject mockJsonObject = new JsonObject();
        JsonArray mockDataArray = new JsonArray();
        mockJsonObject.add("data", mockDataArray);
        JsonObject mockNovelObject = new JsonObject();
        JsonObject mockPagination = new JsonObject();
        mockPagination.addProperty("last", 5);
        mockJsonObject.add("pagination", mockPagination);
        mockNovelObject.addProperty("name", "Test Novel");
        mockNovelObject.addProperty("slug", "test-novel");
        mockDataArray.add(mockNovelObject);

        doReturn(mockJsonObject).when(meTruyenChuPlugin).connectAPI(apiUrl);
        doReturn(novel).when(meTruyenChuPlugin).createNovelByJsonData(mockNovelObject);
        DataResponse result = meTruyenChuPlugin.getNovelSearch(1, search, "A-Z");

        assertNotNull(result);
        assertEquals("success", result.getStatus());
        assertEquals(5, result.getTotalPage());
        assertEquals(1, result.getCurrentPage());
        assertNotNull(result.getData());
        List<Novel> novels = (List<Novel>) result.getData();
        assertFalse(novels.isEmpty());
        Novel value = novels.get(0);
        assertEquals("description", value.getDescription());
        assertEquals("image", value.getImage());
        assertEquals("john wick", value.getAuthor().getName());

        verify(meTruyenChuPlugin).connectAPI(apiUrl);
        verify(meTruyenChuPlugin).createNovelByJsonData(mockNovelObject);
    }

    @Test
    public void getNovelSearch_whenApiCallFailed_error() {
        String search = "test";
        String apiUrl = "https://backend.metruyencv.com/api/books/search?keyword=test&limit=20&page=1&sort=-view_count&filter[state]=published";
        Novel novel = new Novel("1", "name", "image", "description", new Author("john-wick", "john wick"), "chuong-1");
        JsonObject mockJsonObject = new JsonObject();
        JsonArray mockDataArray = new JsonArray();
        mockJsonObject.add("data", mockDataArray);
        JsonObject mockNovelObject = new JsonObject();
        JsonObject mockPagination = new JsonObject();
        mockPagination.addProperty("last", 5);
        mockJsonObject.add("pagination", mockPagination);
        mockNovelObject.addProperty("name", "Test Novel");
        mockNovelObject.addProperty("slug", "test-novel");
        mockDataArray.add(mockNovelObject);

        doReturn(null).when(meTruyenChuPlugin).connectAPI(apiUrl);
        DataResponse result = meTruyenChuPlugin.getNovelSearch(1, search, "A-Z");

        assertEquals("error", result.getStatus());
        assertNull(result.getData());

        verify(meTruyenChuPlugin).connectAPI(apiUrl);
    }

    @Test
    public void getAuthorDetail_validAuthorId_success() {
        String authorId = "valid-author-id";
        String apiUrl = String.format("https://backend.metruyencv.com/api/books?filter[author]=%s&include=author&limit=100&page=1&filter[state]=published", authorId.split("-")[0]);

        Novel novel = new Novel("1", "name", "image", "description", new Author("john-wick", "john wick"), "chuong-1");
        JsonObject mockJsonObject = new JsonObject();
        JsonArray mockDataArray = new JsonArray();
        mockJsonObject.add("data", mockDataArray);
        JsonObject mockNovelObject = new JsonObject();
        mockDataArray.add(mockNovelObject);

        doReturn(mockJsonObject).when(meTruyenChuPlugin).connectAPI(apiUrl);
        doReturn(novel).when(meTruyenChuPlugin).createNovelByJsonData(mockNovelObject);
        DataResponse result = meTruyenChuPlugin.getAuthorDetail(authorId);

        assertNotNull(result);
        assertEquals("success", result.getStatus());
        assertEquals(1, result.getTotalPage());
        assertEquals(1, result.getCurrentPage());
        assertNotNull(result.getData());
        List<Novel> novels = (List<Novel>) result.getData();
        assertFalse(novels.isEmpty());
        Novel value = novels.get(0);
        assertEquals("description", value.getDescription());
        assertEquals("image", value.getImage());
        assertEquals("john wick", value.getAuthor().getName());

        verify(meTruyenChuPlugin).connectAPI(apiUrl);
        verify(meTruyenChuPlugin).createNovelByJsonData(mockNovelObject);
    }

    @Test
    public void getAuthorDetail_inValidAuthorId_error() {
        String authorId = "valid-author-id";
        String apiUrl = String.format("https://backend.metruyencv.com/api/books?filter[author]=%s&include=author&limit=100&page=1&filter[state]=published", authorId.split("-")[0]);
        JsonObject mockJsonObject = new JsonObject();
        JsonArray mockDataArray = new JsonArray();
        mockJsonObject.add("data", mockDataArray);
        JsonObject mockNovelObject = new JsonObject();
        mockDataArray.add(mockNovelObject);

        doReturn(null).when(meTruyenChuPlugin).connectAPI(apiUrl);
        DataResponse result = meTruyenChuPlugin.getAuthorDetail(authorId);
        List<Novel> novels = (List<Novel>) result.getData();
        assertTrue(novels.isEmpty());
        verify(meTruyenChuPlugin).connectAPI(apiUrl);
    }

    @Test
    public void getNovelDetail_validNovelId_success() {
        String novelId = "valid-novel-id";
        Novel novel = new Novel("1", "name", "image", "description", new Author("john-wick", "john wick"), "chuong-1");
        JsonObject mockNovelObject = new JsonObject();

        doReturn(mockNovelObject).when(meTruyenChuPlugin).getNovelDetailBySlug(novelId);
        doReturn(novel).when(meTruyenChuPlugin).createNovelByJsonData(mockNovelObject);
        DataResponse result = meTruyenChuPlugin.getNovelDetail(novelId);

        assertNotNull(result);
        assertEquals("success", result.getStatus());
        assertEquals(null, result.getTotalPage());
        assertEquals(null, result.getCurrentPage());
        assertNotNull(result.getData());
        Novel value = (Novel) result.getData();
        assertEquals("description", value.getDescription());
        assertEquals("image", value.getImage());
        assertEquals("john wick", value.getAuthor().getName());

        verify(meTruyenChuPlugin).getNovelDetailBySlug(novelId);
        verify(meTruyenChuPlugin).createNovelByJsonData(mockNovelObject);
    }

    @Test
    public void getNovelDetail_inValidNovelId_error() {
        String novelId = "invalid-novel-id";
        JsonObject mockNovelObject = new JsonObject();

        doReturn(null).when(meTruyenChuPlugin).getNovelDetailBySlug(novelId);
        DataResponse result = meTruyenChuPlugin.getNovelDetail(novelId);
        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals(null, result.getTotalPage());
        assertEquals(null, result.getCurrentPage());
        assertNull(result.getData());

        verify(meTruyenChuPlugin).getNovelDetailBySlug(novelId);
    }

    @Test
    public void getNovelListChapters_validNovelId_success() {
        String novelId = "valid-novel-id";
        int page = 1;
        Novel novel = new Novel("1", "name", "image", "description", new Author("john-wick", "john wick"), "chuong-1");
        Chapter chapter = new Chapter("1", "name", "chuong-1", "chuong-2", null, "test chapter", novel.getAuthor(), "this is content");
        String apiUrl = String.format("https://backend.metruyencv.com/api/chapters?filter[book_id]=%s", novelId);
        JsonObject mockNovelObject = new JsonObject();
        JsonObject mockJsonObject = new JsonObject();
        mockNovelObject.addProperty("id", novelId);
        JsonArray mockDataArray = new JsonArray();
        mockJsonObject.add("data", mockDataArray);
        JsonObject chapterObject = new JsonObject();
        mockDataArray.add(chapterObject);

        doReturn(mockJsonObject).when(meTruyenChuPlugin).connectAPI(apiUrl);
        doReturn(mockNovelObject).when(meTruyenChuPlugin).getNovelDetailBySlug(novelId);
        doReturn(novel).when(meTruyenChuPlugin).createNovelByJsonData(mockNovelObject);
        doReturn(chapter).when(meTruyenChuPlugin).createChapterByJsonData(chapterObject, novel);

        DataResponse result = meTruyenChuPlugin.getNovelListChapters(novelId, page);

        assertNotNull(result);
        assertEquals("success", result.getStatus());
        assertEquals(1, result.getTotalPage());
        assertEquals(1, result.getCurrentPage());
        assertNotNull(result.getData());
        List<Chapter> chapters = (List<Chapter>) result.getData();
        assertFalse(chapters.isEmpty());
        Chapter value = chapters.get(0);
        assertEquals("this is content", value.getContent());
        assertEquals(chapter.getName(), value.getName());
        assertEquals(chapter.getNovelName(), value.getNovelName());

        verify(meTruyenChuPlugin).connectAPI(apiUrl);
        verify(meTruyenChuPlugin).getNovelDetailBySlug(novelId);
        verify(meTruyenChuPlugin).createNovelByJsonData(mockNovelObject);
        verify(meTruyenChuPlugin).createChapterByJsonData(chapterObject, novel);
    }

    @Test
    public void getNovelListChapters_invalidNovelId_error() {
        String novelId = "invalid-novel-id";
        int page = 1;
        JsonObject mockNovelObject = new JsonObject();
        JsonObject mockJsonObject = new JsonObject();
        mockNovelObject.addProperty("id", novelId);
        JsonArray mockDataArray = new JsonArray();
        mockJsonObject.add("data", mockDataArray);
        JsonObject chapterObject = new JsonObject();
        mockDataArray.add(chapterObject);

        doReturn(null).when(meTruyenChuPlugin).getNovelDetailBySlug(novelId);

        DataResponse result = meTruyenChuPlugin.getNovelListChapters(novelId, page);

        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals(null, result.getTotalPage());
        assertEquals(null, result.getCurrentPage());
        assertNull(result.getData());

        verify(meTruyenChuPlugin).getNovelDetailBySlug(novelId);
    }

    @Test
    public void getNovelChapterDetail_validNovelIdAndChapterId_success() {
        String novelId = "valid-novel-id";
        String chapterId = "valid-chapter-id";
        Novel novel = new Novel("1", "name", "image", "description", new Author("john-wick", "john wick"), "chuong-1");
        Chapter chapter = new Chapter("1", "name", "chuong-1", "chuong-2", null, "test chapter", novel.getAuthor(), "this is content");
        JsonObject mockNovelObject = new JsonObject();

        doReturn(mockNovelObject).when(meTruyenChuPlugin).getNovelDetailBySlug(novelId);
        doReturn(novel).when(meTruyenChuPlugin).createNovelByJsonData(mockNovelObject);
        doReturn(chapter).when(meTruyenChuPlugin).getContentChapter(novelId, chapterId);
        DataResponse result = meTruyenChuPlugin.getNovelChapterDetail(novelId, chapterId);

        assertNotNull(result);
        assertEquals("success", result.getStatus());
        assertEquals(null, result.getTotalPage());
        assertEquals(null, result.getCurrentPage());
        assertNotNull(result.getData());
        Chapter value = (Chapter) result.getData();
        assertEquals("this is content", value.getContent());
        assertEquals(chapter.getName(), value.getName());
        assertEquals(chapter.getNovelName(), value.getNovelName());

        verify(meTruyenChuPlugin).getNovelDetailBySlug(novelId);
        verify(meTruyenChuPlugin).createNovelByJsonData(mockNovelObject);
        verify(meTruyenChuPlugin).getContentChapter(novelId, chapterId);
    }

    @Test
    public void getNovelChapterDetail_inValidNovelId_error() {
        String novelId = "invalid-novel-id";
        String chapterId = "valid-chapter-id";
        Novel novel = new Novel("1", "name", "image", "description", new Author("john-wick", "john wick"), "chuong-1");
        Chapter chapter = new Chapter("1", "name", "chuong-1", "chuong-2", null, "test chapter", novel.getAuthor(), "this is content");
        JsonObject mockNovelObject = new JsonObject();

        doReturn(null).when(meTruyenChuPlugin).getNovelDetailBySlug(novelId);
        DataResponse result = meTruyenChuPlugin.getNovelChapterDetail(novelId, chapterId);

        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals(null, result.getTotalPage());
        assertEquals(null, result.getCurrentPage());
        assertNull(result.getData());

        verify(meTruyenChuPlugin).getNovelDetailBySlug(novelId);
    }

    @Test
    public void getNovelChapterDetail_inValidChapterId_error() {
        String novelId = "valid-novel-id";
        String chapterId = "invalid-chapter-id";
        Novel novel = new Novel("1", "name", "image", "description", new Author("john-wick", "john wick"), "chuong-1");
        Chapter chapter = new Chapter("1", "name", "chuong-1", "chuong-2", null, "test chapter", novel.getAuthor(), "this is content");
        JsonObject mockNovelObject = new JsonObject();

        doReturn(mockNovelObject).when(meTruyenChuPlugin).getNovelDetailBySlug(novelId);
        doReturn(novel).when(meTruyenChuPlugin).createNovelByJsonData(mockNovelObject);
        doReturn(null).when(meTruyenChuPlugin).getContentChapter(novelId, chapterId);
        DataResponse result = meTruyenChuPlugin.getNovelChapterDetail(novelId, chapterId);
        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals(null, result.getTotalPage());
        assertEquals(null, result.getCurrentPage());
        assertNull(result.getData());


        verify(meTruyenChuPlugin).getNovelDetailBySlug(novelId);
        verify(meTruyenChuPlugin).createNovelByJsonData(mockNovelObject);
        verify(meTruyenChuPlugin).getContentChapter(novelId, chapterId);
    }
}
