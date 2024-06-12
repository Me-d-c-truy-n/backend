package com.crawldata.demo.plugin_builder.metruyencv;

import com.crawldata.back_end.model.Author;
import com.crawldata.back_end.model.Chapter;
import com.crawldata.back_end.model.Novel;
import com.crawldata.back_end.plugin_builder.metruyencv.MeTruyenChuPlugin;
import com.crawldata.back_end.response.DataResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MeTruyenChuPluginTest {
    @Mock
    private CloseableHttpClient httpClient;
    @Mock
    private CloseableHttpResponse httpResponse;
    @Mock
    private HttpEntity httpEntity;

    @Spy
    private MeTruyenChuPlugin meTruyenChuPlugin;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void getAllNovels_whenApiCallSucceeds_success()  {
        // Arrange
        int page = 1;
        String search = "test";
        String apiUrl = String.format("https://backend.metruyencv.com/api/books?include=author&sort=-view_count&limit=20&page=%s&filter[state]=published",page);
        Novel novel = new Novel("1","name","image","description",new Author("john-wick","john wick"),"chuong-1");
        JsonObject mockJsonObject = new JsonObject();
        doReturn(mockJsonObject).when(meTruyenChuPlugin).connectAPI(apiUrl);
        JsonObject mockPagination = new JsonObject();
        mockJsonObject.add("pagination", mockPagination);
        JsonArray mockDataArray = new JsonArray();
        mockJsonObject.add("data", mockDataArray);
        mockPagination.addProperty("last", 5); // Assuming there are 5 pages in total
        JsonObject mockNovelObject = new JsonObject();
        mockNovelObject.addProperty("name", "Test Novel");
        mockNovelObject.addProperty("slug", "test-novel");
        mockDataArray.add(mockNovelObject);
        doReturn(novel).when(meTruyenChuPlugin).createNovelByJsonData(mockNovelObject);
        // Act
        DataResponse result = meTruyenChuPlugin.getAllNovels(page, search);
        // Assert
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
    }
    @Test
    public void getAllNovels_whenApiCallFailed_error()  {
        // Arrange
        int page = 1;
        String search = "test";
        String apiUrl = String.format("https://backend.metruyencv.com/api/books?include=author&sort=-view_count&limit=20&page=%s&filter[state]=published",page);
        Novel novel = new Novel("1","name","image","description",new Author("john-wick","john wick"),"chuong-1");
        JsonObject mockJsonObject = new JsonObject();
        doReturn(null).when(meTruyenChuPlugin).connectAPI(apiUrl);
        JsonObject mockPagination = new JsonObject();
        mockJsonObject.add("pagination", mockPagination);
        JsonArray mockDataArray = new JsonArray();
        mockJsonObject.add("data", mockDataArray);
        mockPagination.addProperty("last", 5); // Assuming there are 5 pages in total
        JsonObject mockNovelObject = new JsonObject();
        mockNovelObject.addProperty("name", "Test Novel");
        mockNovelObject.addProperty("slug", "test-novel");
        mockDataArray.add(mockNovelObject);
        doReturn(novel).when(meTruyenChuPlugin).createNovelByJsonData(mockNovelObject);
        // Act
        DataResponse result = meTruyenChuPlugin.getAllNovels(page, search);
        // Assert
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
    }
    @Test
    public void getNovelSearch_whenApiCallSucceeds_success()  {
        // Arrange
        int page = 1;
        String search = "test";
        String apiUrl = "https://backend.metruyencv.com/api/books/search?keyword=test&limit=20&page=1&sort=-view_count&filter[state]=published";
        Novel novel = new Novel("1","name","image","description",new Author("john-wick","john wick"),"chuong-1");
        JsonObject mockJsonObject = new JsonObject();
        doReturn(mockJsonObject).when(meTruyenChuPlugin).connectAPI(apiUrl);
        JsonArray mockDataArray = new JsonArray();
        mockJsonObject.add("data", mockDataArray);
        JsonObject mockNovelObject = new JsonObject();
        JsonObject mockPagination = new JsonObject();
        mockPagination.addProperty("last", 5);
        mockJsonObject.add("pagination", mockPagination);
        mockNovelObject.addProperty("name", "Test Novel");
        mockNovelObject.addProperty("slug", "test-novel");
        mockDataArray.add(mockNovelObject);
        doReturn(novel).when(meTruyenChuPlugin).createNovelByJsonData(mockNovelObject);
        DataResponse result = meTruyenChuPlugin.getNovelSearch(1,search,"A-Z");
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
    }
    @Test
    public void getNovelSearch_whenApiCallFailed_error()  {
        // Arrange
        int page = 1;
        String search = "test";
        String apiUrl = "https://backend.metruyencv.com/api/books/search?keyword=test&limit=20&page=1&sort=-view_count&filter[state]=published";
        Novel novel = new Novel("1","name","image","description",new Author("john-wick","john wick"),"chuong-1");
        JsonObject mockJsonObject = new JsonObject();
        doReturn(null).when(meTruyenChuPlugin).connectAPI(apiUrl);
        JsonArray mockDataArray = new JsonArray();
        mockJsonObject.add("data", mockDataArray);
        JsonObject mockNovelObject = new JsonObject();
        JsonObject mockPagination = new JsonObject();
        mockPagination.addProperty("last", 5);
        mockJsonObject.add("pagination", mockPagination);
        mockNovelObject.addProperty("name", "Test Novel");
        mockNovelObject.addProperty("slug", "test-novel");
        mockDataArray.add(mockNovelObject);
        doReturn(novel).when(meTruyenChuPlugin).createNovelByJsonData(mockNovelObject);
        DataResponse result = meTruyenChuPlugin.getNovelSearch(1,search,"A-Z");
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
    }
    @Test
    public void getAuthorDetail_validAuthorId_success()  {
        String authorId = "valid-author-id";
        String apiUrl = String.format("https://backend.metruyencv.com/api/books?filter[author]=%s&include=author&limit=100&page=1&filter[state]=published", authorId.split("-")[0]);

        Novel novel = new Novel("1","name","image","description",new Author("john-wick","john wick"),"chuong-1");
        JsonObject mockJsonObject = new JsonObject();
        doReturn(mockJsonObject).when(meTruyenChuPlugin).connectAPI(apiUrl);
        JsonArray mockDataArray = new JsonArray();
        mockJsonObject.add("data", mockDataArray);
        JsonObject mockNovelObject = new JsonObject();
        mockDataArray.add(mockNovelObject);
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
    }
    @Test
    public void getAuthorDetail_inValidAuthorId_error()  {
        String authorId = "valid-author-id";
        String apiUrl = String.format("https://backend.metruyencv.com/api/books?filter[author]=%s&include=author&limit=100&page=1&filter[state]=published", authorId.split("-")[0]);

        Novel novel = new Novel("1","name","image","description",new Author("john-wick","john wick"),"chuong-1");
        JsonObject mockJsonObject = new JsonObject();
        doReturn(null).when(meTruyenChuPlugin).connectAPI(apiUrl);
        JsonArray mockDataArray = new JsonArray();
        mockJsonObject.add("data", mockDataArray);
        JsonObject mockNovelObject = new JsonObject();
        mockDataArray.add(mockNovelObject);
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
    }
    @Test
    public void getNovelDetail_validNovelId_success()  {
        String novelId = "valid-novel-id";
        Novel novel = new Novel("1","name","image","description",new Author("john-wick","john wick"),"chuong-1");
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
    }
    @Test
    public void getNovelDetail_inValidNovelId_error()  {
        String novelId = "invalid-novel-id";
        Novel novel = new Novel("1","name","image","description",new Author("john-wick","john wick"),"chuong-1");
        JsonObject mockNovelObject = new JsonObject();
        doReturn(null).when(meTruyenChuPlugin).getNovelDetailBySlug(novelId);
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
    }
    @Test
    public void getNovelListChapters_validNovelId_success() {
        String novelId = "valid-novel-id";
        int page = 1 ;
        Novel novel = new Novel("1","name","image","description",new Author("john-wick","john wick"),"chuong-1");
        Chapter chapter = new Chapter("1","name","chuong-1","chuong-2",null,"test chapter",novel.getAuthor(),"this is content");
        String apiUrl = String.format("https://backend.metruyencv.com/api/chapters?filter[book_id]=%s", novelId);
        JsonObject mockNovelObject = new JsonObject();
        JsonObject mockJsonObject = new JsonObject();
        mockNovelObject.addProperty("id",novelId);
        JsonArray mockDataArray = new JsonArray();
        mockJsonObject.add("data", mockDataArray);
        JsonObject chapterObject = new JsonObject();
        mockDataArray.add(chapterObject);

        doReturn(mockJsonObject).when(meTruyenChuPlugin).connectAPI(apiUrl);
        doReturn(mockNovelObject).when(meTruyenChuPlugin).getNovelDetailBySlug(novelId);
        doReturn(novel).when(meTruyenChuPlugin).createNovelByJsonData(mockNovelObject);
        doReturn(chapter).when(meTruyenChuPlugin).createChapterByJsonData(chapterObject,novel);

        DataResponse result = meTruyenChuPlugin.getNovelListChapters(novelId,page);

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
        assertEquals(chapter.getNovelName(),value.getNovelName());
    }
    @Test
    public void getNovelListChapters_invalidNovelId_error()  {
        String novelId = "invalid-novel-id";
        int page = 1 ;
        Novel novel = new Novel("1","name","image","description",new Author("john-wick","john wick"),"chuong-1");
        Chapter chapter = new Chapter("1","name","chuong-1","chuong-2",null,"test chapter",novel.getAuthor(),"this is content");
        String apiUrl = String.format("https://backend.metruyencv.com/api/chapters?filter[book_id]=%s", novelId);
        JsonObject mockNovelObject = new JsonObject();
        JsonObject mockJsonObject = new JsonObject();
        mockNovelObject.addProperty("id",novelId);
        JsonArray mockDataArray = new JsonArray();
        mockJsonObject.add("data", mockDataArray);
        JsonObject chapterObject = new JsonObject();
        mockDataArray.add(chapterObject);

        doReturn(null).when(meTruyenChuPlugin).connectAPI(apiUrl);
        doReturn(mockNovelObject).when(meTruyenChuPlugin).getNovelDetailBySlug(novelId);
        doReturn(novel).when(meTruyenChuPlugin).createNovelByJsonData(mockNovelObject);
        doReturn(chapter).when(meTruyenChuPlugin).createChapterByJsonData(chapterObject,novel);

        DataResponse result = meTruyenChuPlugin.getNovelListChapters(novelId,page);

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
        assertEquals(chapter.getNovelName(),value.getNovelName());
    }
    @Test
    public void getNovelChapterDetail_validNovelIdAndChapterId_success()  {
        String novelId = "valid-novel-id";
        String chapterId = "valid-chapter-id";
        Novel novel = new Novel("1","name","image","description",new Author("john-wick","john wick"),"chuong-1");
        Chapter chapter = new Chapter("1","name","chuong-1","chuong-2",null,"test chapter",novel.getAuthor(),"this is content");
        JsonObject mockNovelObject = new JsonObject();
        doReturn(mockNovelObject).when(meTruyenChuPlugin).getNovelDetailBySlug(novelId);
        doReturn(novel).when(meTruyenChuPlugin).createNovelByJsonData(mockNovelObject);
        doReturn(chapter).when(meTruyenChuPlugin).getContentChapter(novelId,chapterId);
        DataResponse result = meTruyenChuPlugin.getNovelChapterDetail(novelId,chapterId);
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
    public void getNovelChapterDetail_inValidNovelId_error()  {
        String novelId = "invalid-novel-id";
        String chapterId = "valid-chapter-id";
        Novel novel = new Novel("1","name","image","description",new Author("john-wick","john wick"),"chuong-1");
        Chapter chapter = new Chapter("1","name","chuong-1","chuong-2",null,"test chapter",novel.getAuthor(),"this is content");
        JsonObject mockNovelObject = new JsonObject();
        doReturn(null).when(meTruyenChuPlugin).getNovelDetailBySlug(novelId);
        doReturn(novel).when(meTruyenChuPlugin).createNovelByJsonData(mockNovelObject);
        doReturn(chapter).when(meTruyenChuPlugin).getContentChapter(novelId,chapterId);
        DataResponse result = meTruyenChuPlugin.getNovelChapterDetail(novelId,chapterId);
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
    public void getNovelChapterDetail_inValidChapterId_error()  {
        String novelId = "valid-novel-id";
        String chapterId = "invalid-chapter-id";
        Novel novel = new Novel("1","name","image","description",new Author("john-wick","john wick"),"chuong-1");
        Chapter chapter = new Chapter("1","name","chuong-1","chuong-2",null,"test chapter",novel.getAuthor(),"this is content");
        JsonObject mockNovelObject = new JsonObject();
        doReturn(mockNovelObject).when(meTruyenChuPlugin).getNovelDetailBySlug(novelId);
        doReturn(novel).when(meTruyenChuPlugin).createNovelByJsonData(mockNovelObject);
        doReturn(null).when(meTruyenChuPlugin).getContentChapter(novelId,chapterId);
        DataResponse result = meTruyenChuPlugin.getNovelChapterDetail(novelId,chapterId);
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
}
