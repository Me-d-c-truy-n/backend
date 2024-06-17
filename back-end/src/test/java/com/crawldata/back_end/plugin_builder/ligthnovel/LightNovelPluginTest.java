package com.crawldata.back_end.plugin_builder.ligthnovel;

import com.crawldata.back_end.BackEndApplication;
import com.crawldata.back_end.model.Author;
import com.crawldata.back_end.model.Chapter;
import com.crawldata.back_end.model.Novel;
import com.crawldata.back_end.novel_plugin_builder.lightnovel.LightNovelPlugin;
import com.crawldata.back_end.response.DataResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = BackEndApplication.class)
public class LightNovelPluginTest {
    private String API_KEY = "35OqUcP8sjF1T";
    private final String NOVEL_LIST_CHAPTERS_API = "https://lightnovel.vn/_next/data/%s/truyen/%s/danh-sach-chuong.json?page=%s";
    private final String AUTHOR_DETAIL_API = "https://lightnovel.vn/_next/data/%s/tac-gia/%s.json";
    private final String ALL_NOVELS_API = "https://lightnovel.vn/_next/data/%s/the-loai.json?sort=doc-nhieu&page=%s";
    private final String NOVEL_SEARCH_API = "https://lightnovel.vn/_next/data/%s/the-loai.json?sort=doc-nhieu&page=%s&keyword=%s";
    @Spy
    private LightNovelPlugin lightNovelPlugin;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getAllNovels_whenApiCallSucceeds_success() throws IOException {
        int page = 1;
        String search = "testSearch";
        String apiUrl = String.format(ALL_NOVELS_API, API_KEY, page);
        JsonObject mockJsonObject = new JsonObject();
        JsonArray dataArray = new JsonArray();
        JsonObject pageObject = new JsonObject();
        pageObject.addProperty("total", 36);
        JsonObject novelObject = new JsonObject();
        novelObject.addProperty("slug", "novel-slug");
        dataArray.add(novelObject);
        pageObject.add("data", dataArray);
        mockJsonObject.add("pageProps", pageObject);
        Novel novel = new Novel("1", "name", "image", "description", new Author("john-wick", "john wick"), "chuong-1");

        doReturn(novelObject).when(lightNovelPlugin).getNovelDetailBySlug("novel-slug");
        doReturn(novel).when(lightNovelPlugin).createNovelByJsonData(novelObject);
        doReturn(mockJsonObject).when(lightNovelPlugin).connectAPI(apiUrl);
        DataResponse result = lightNovelPlugin.getAllNovels(page, search);

        assertNotNull(result);
        assertEquals("success", result.getStatus());
        assertEquals(1, result.getCurrentPage());
        assertNotNull(result.getData());
        List<Novel> novels = (List<Novel>) result.getData();
        assertFalse(novels.isEmpty());
        Novel value = novels.get(0);
        assertEquals("name", value.getName());
        assertEquals("description", value.getDescription());
        assertEquals("image", value.getImage());
        assertEquals("john wick", value.getAuthor().getName());

        verify(lightNovelPlugin).connectAPI(apiUrl);
        verify(lightNovelPlugin).getNovelDetailBySlug("novel-slug");
        verify(lightNovelPlugin).createNovelByJsonData(novelObject);
    }

    @Test
    public void getAllNovels_whenApiCallFailed_error() throws IOException {
        int page = 1;
        String search = "testSearch";
        String apiUrl = String.format(ALL_NOVELS_API, API_KEY, page);
        JsonObject mockJsonObject = new JsonObject();
        JsonArray dataArray = new JsonArray();
        JsonObject pageObject = new JsonObject();
        pageObject.addProperty("total", 36);
        JsonObject novelObject = new JsonObject();
        novelObject.addProperty("slug", "novel-slug");
        dataArray.add(novelObject);
        pageObject.add("data", dataArray);
        mockJsonObject.add("pageProps", pageObject);
        Novel novel = new Novel("1", "name", "image", "description", new Author("john-wick", "john wick"), "chuong-1");

        doReturn(null).when(lightNovelPlugin).connectAPI(apiUrl);
        DataResponse result = lightNovelPlugin.getAllNovels(page, search);

        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals(null, result.getCurrentPage());
        assertNull(result.getData());

        verify(lightNovelPlugin).connectAPI(apiUrl);
    }

    @Test
    public void getNovelSearch_whenApiCallSucceeds_success() throws IOException {
        int page = 1;
        String search = "testSearch";
        String apiUrl = String.format(NOVEL_SEARCH_API, API_KEY, page, search);
        JsonObject mockJsonObject = new JsonObject();
        JsonArray dataArray = new JsonArray();
        JsonObject pageObject = new JsonObject();
        pageObject.addProperty("total", 36);
        JsonObject novelObject = new JsonObject();
        novelObject.addProperty("slug", "novel-slug");
        dataArray.add(novelObject);
        pageObject.add("data", dataArray);
        mockJsonObject.add("pageProps", pageObject);
        Novel novel = new Novel("1", "name", "image", "description", new Author("john-wick", "john wick"), "chuong-1");

        doReturn(search).when(lightNovelPlugin).reverseSlugging(search);
        doReturn(novelObject).when(lightNovelPlugin).getNovelDetailBySlug("novel-slug");
        doReturn(novel).when(lightNovelPlugin).createNovelByJsonData(novelObject);
        doReturn(mockJsonObject).when(lightNovelPlugin).connectAPI(apiUrl);
        DataResponse result = lightNovelPlugin.getNovelSearch(page, search, "A-Z");

        assertNotNull(result);
        assertEquals("success", result.getStatus());
        assertEquals(1, result.getCurrentPage());
        assertNotNull(result.getData());
        List<Novel> novels = (List<Novel>) result.getData();
        assertFalse(novels.isEmpty());
        Novel value = novels.get(0);
        assertEquals("name", value.getName());
        assertEquals("description", value.getDescription());
        assertEquals("image", value.getImage());
        assertEquals("john wick", value.getAuthor().getName());

        verify(lightNovelPlugin).connectAPI(apiUrl);
        verify(lightNovelPlugin).getNovelDetailBySlug("novel-slug");
        verify(lightNovelPlugin).createNovelByJsonData(novelObject);
    }

    @Test
    public void getNovelSearch_whenApiCallFailed_error() throws IOException {
        int page = 1;
        String search = "testSearch";
        String apiUrl = String.format(NOVEL_SEARCH_API, API_KEY, page, search);
        JsonObject mockJsonObject = new JsonObject();
        JsonArray dataArray = new JsonArray();
        JsonObject pageObject = new JsonObject();
        pageObject.addProperty("total", 36);
        JsonObject novelObject = new JsonObject();
        novelObject.addProperty("slug", "novel-slug");
        dataArray.add(novelObject);
        pageObject.add("data", dataArray);
        mockJsonObject.add("pageProps", pageObject);
        Novel novel = new Novel("1", "name", "image", "description", new Author("john-wick", "john wick"), "chuong-1");


        doReturn(null).when(lightNovelPlugin).connectAPI(apiUrl);
        DataResponse result = lightNovelPlugin.getNovelSearch(page, search, "A-Z");

        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals(null, result.getCurrentPage());
        assertNull(result.getData());


        verify(lightNovelPlugin).connectAPI(apiUrl);

    }

    @Test
    public void getAuthorDetail_validAuthorId_success() throws IOException {
        String authorId = "john-wick";
        String apiUrl = String.format(AUTHOR_DETAIL_API, API_KEY, authorId);
        JsonObject mockJsonObject = new JsonObject();
        JsonArray dataArray = new JsonArray();
        JsonObject novelObject = new JsonObject();
        novelObject.addProperty("slug", "novel-slug");
        dataArray.add(novelObject);
        mockJsonObject.add("data", dataArray);
        mockJsonObject.addProperty("author", "has author");
        Novel novel = new Novel("1", "name", "image", "description", new Author("john-wick", "john wick"), "chuong-1");

        doReturn(novel).when(lightNovelPlugin).createNovelByJsonData(novelObject);
        doReturn(mockJsonObject).when(lightNovelPlugin).connectAPI(apiUrl);
        DataResponse result = lightNovelPlugin.getAuthorDetail(authorId);

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

        verify(lightNovelPlugin).connectAPI(apiUrl);
        verify(lightNovelPlugin).createNovelByJsonData(novelObject);

    }

    @Test
    public void getAuthorDetail_invalidAuthorId_error() throws IOException {
        String authorId = "invalid-author-id";
        String apiUrl = String.format(AUTHOR_DETAIL_API, API_KEY, authorId);
        JsonObject mockJsonObject = new JsonObject();
        JsonArray dataArray = new JsonArray();
        JsonObject novelObject = new JsonObject();
        novelObject.addProperty("slug", "novel-slug");
        dataArray.add(novelObject);
        mockJsonObject.add("data", dataArray);
        mockJsonObject.addProperty("author", "has author");
        Novel novel = new Novel("1", "name", "image", "description", new Author("john-wick", "john wick"), "chuong-1");

        doReturn(null).when(lightNovelPlugin).connectAPI(apiUrl);
        DataResponse result = lightNovelPlugin.getAuthorDetail(authorId);

        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertNull(result.getData());

        verify(lightNovelPlugin).connectAPI(apiUrl);

    }

    @Test
    public void getNovelDetail_validNovelId_success() {
        String novelId = "valid-novel-id";
        Novel novel = new Novel("1", "name", "image", "description", new Author("john-wick", "john wick"), "chuong-1");
        JsonObject novelObject = new JsonObject();
        doReturn(novelObject).when(lightNovelPlugin).getNovelDetailBySlug(novelId);
        doReturn(novel).when(lightNovelPlugin).createNovelByJsonData(novelObject);
        DataResponse result = lightNovelPlugin.getNovelDetail(novelId);
        assertNotNull(result);
        assertEquals("success", result.getStatus());
        assertEquals(null, result.getTotalPage());
        assertEquals(null, result.getCurrentPage());
        assertNotNull(result.getData());
        Novel value = (Novel) result.getData();
        assertEquals("description", value.getDescription());
        assertEquals("image", value.getImage());
        assertEquals("john wick", value.getAuthor().getName());

        verify(lightNovelPlugin).getNovelDetailBySlug(novelId);
        verify(lightNovelPlugin).createNovelByJsonData(novelObject);
    }

    @Test
    public void getNovelDetail_invalidNovelId_error() {
        String novelId = "invalid-novel-id";
        Novel novel = new Novel("1", "name", "image", "description", new Author("john-wick", "john wick"), "chuong-1");
        JsonObject novelObject = new JsonObject();
        doReturn(null).when(lightNovelPlugin).getNovelDetailBySlug(novelId);
        DataResponse result = lightNovelPlugin.getNovelDetail(novelId);
        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals(null, result.getTotalPage());
        assertEquals(null, result.getCurrentPage());
        assertNull(result.getData());

        verify(lightNovelPlugin).getNovelDetailBySlug(novelId);
    }

    @Test
    public void getNovelListChapters_validNovelId_success() throws IOException {
        String novelId = "valid-novel-id";
        int page = 1;
        Novel novel = new Novel("1", "name", "image", "description", new Author("john-wick", "john wick"), "chuong-1");
        Chapter chapter = new Chapter("1", "name", "chuong-1", "chuong-2", null, "test chapter", novel.getAuthor(), "this is content");
        JsonObject novelObject = new JsonObject();
        novelObject.addProperty("slug", "hihi");
        String apiUrl = String.format(NOVEL_LIST_CHAPTERS_API, API_KEY, novelObject.get("slug").getAsString(), page == 1 ? 0 : page);
        JsonObject mockJsonObject = new JsonObject();
        JsonArray dataArray = new JsonArray();
        JsonObject pageObject = new JsonObject();
        pageObject.addProperty("total", 36);
        JsonObject chapterObject = new JsonObject();
        dataArray.add(chapterObject);
        pageObject.add("chapterList", dataArray);
        mockJsonObject.add("pageProps", pageObject);

        doReturn(novelObject).when(lightNovelPlugin).getNovelDetailBySlug(novelId);
        doReturn(novel).when(lightNovelPlugin).createNovelByJsonData(novelObject);
        doReturn(mockJsonObject).when(lightNovelPlugin).connectAPI(apiUrl);
        doReturn(chapter).when(lightNovelPlugin).createChapterByJsonData(chapterObject, novel);
        DataResponse result = lightNovelPlugin.getNovelListChapters(novelId, page);

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

        verify(lightNovelPlugin).getNovelDetailBySlug(novelId);
        verify(lightNovelPlugin).createNovelByJsonData(novelObject);
        verify(lightNovelPlugin).connectAPI(apiUrl);
        verify(lightNovelPlugin).createChapterByJsonData(chapterObject, novel);

    }

    @Test
    public void getNovelListChapters_invalidNovelId_error() throws IOException {
        String novelId = "valid-novel-id";
        int page = 1;
        Novel novel = new Novel("1", "name", "image", "description", new Author("john-wick", "john wick"), "chuong-1");
        Chapter chapter = new Chapter("1", "name", "chuong-1", "chuong-2", null, "test chapter", novel.getAuthor(), "this is content");
        JsonObject novelObject = new JsonObject();
        novelObject.addProperty("slug", "hihi");
        String apiUrl = String.format(NOVEL_LIST_CHAPTERS_API, API_KEY, novelObject.get("slug").getAsString(), page == 1 ? 0 : page);
        JsonObject mockJsonObject = new JsonObject();
        JsonArray dataArray = new JsonArray();
        JsonObject pageObject = new JsonObject();
        pageObject.addProperty("total", 36);
        JsonObject chapterObject = new JsonObject();
        dataArray.add(chapterObject);
        pageObject.add("chapterList", dataArray);
        mockJsonObject.add("pageProps", pageObject);

        doReturn(null).when(lightNovelPlugin).connectAPI(apiUrl);
        DataResponse result = lightNovelPlugin.getNovelListChapters(novelId, page);

        assertNotNull(result);
        assertEquals("error", result.getStatus());

    }

    @Test
    public void getNovelChapterDetail_validNovelIdAndChapterId_success() {
        String novelId = "valid-novel-id";
        String chapterId = "valid-chapter-id";
        Novel novel = new Novel("1", "name", "image", "description", new Author("john-wick", "john wick"), "chuong-1");
        Chapter chapter = new Chapter("1", "name", "chuong-1", "chuong-2", null, "test chapter", novel.getAuthor(), "this is content");
        JsonObject novelObject = new JsonObject();

        doReturn(novelObject).when(lightNovelPlugin).getNovelDetailBySlug(novelId);
        doReturn(novel).when(lightNovelPlugin).createNovelByJsonData(novelObject);
        doReturn(chapter).when(lightNovelPlugin).getContentChapter(novelId, chapterId);

        DataResponse result = lightNovelPlugin.getNovelChapterDetail(novelId, chapterId);
        assertNotNull(result);
        assertEquals("success", result.getStatus());
        assertEquals(null, result.getTotalPage());
        assertEquals(null, result.getCurrentPage());
        assertNotNull(result.getData());
        Chapter value = (Chapter) result.getData();
        assertEquals("this is content", value.getContent());
        assertEquals(chapter.getName(), value.getName());
        assertEquals(chapter.getNovelName(), value.getNovelName());

        verify(lightNovelPlugin).getNovelDetailBySlug(novelId);
        verify(lightNovelPlugin).createNovelByJsonData(novelObject);
        verify(lightNovelPlugin).getContentChapter(novelId, chapterId);
    }

    @Test
    public void getNovelChapterDetail_invalidNovelId_error() {
        String novelId = "invalid-novel-id";
        String chapterId = "valid-chapter-id";
        Novel novel = new Novel("1", "name", "image", "description", new Author("john-wick", "john wick"), "chuong-1");
        Chapter chapter = new Chapter("1", "name", "chuong-1", "chuong-2", null, "test chapter", novel.getAuthor(), "this is content");
        JsonObject novelObject = new JsonObject();

        doReturn(null).when(lightNovelPlugin).getNovelDetailBySlug(novelId);


        DataResponse result = lightNovelPlugin.getNovelChapterDetail(novelId, chapterId);
        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals(null, result.getTotalPage());
        assertEquals(null, result.getCurrentPage());
        assertNull(result.getData());

        verify(lightNovelPlugin).getNovelDetailBySlug(novelId);
    }

    @Test
    public void getNovelChapterDetail_invalidChapterId_error() {
        String novelId = "valid-novel-id";
        String chapterId = "invalid-chapter-id";
        Novel novel = new Novel("1", "name", "image", "description", new Author("john-wick", "john wick"), "chuong-1");
        Chapter chapter = new Chapter("1", "name", "chuong-1", "chuong-2", null, "test chapter", novel.getAuthor(), "this is content");
        JsonObject novelObject = new JsonObject();

        doReturn(novelObject).when(lightNovelPlugin).getNovelDetailBySlug(novelId);
        doReturn(novel).when(lightNovelPlugin).createNovelByJsonData(novelObject);
        doReturn(null).when(lightNovelPlugin).getContentChapter(novelId, chapterId);

        DataResponse result = lightNovelPlugin.getNovelChapterDetail(novelId, chapterId);
        assertNotNull(result);
        assertEquals("error", result.getStatus());
        assertEquals(null, result.getTotalPage());
        assertEquals(null, result.getCurrentPage());
        assertNull(result.getData());


        verify(lightNovelPlugin).getNovelDetailBySlug(novelId);
        verify(lightNovelPlugin).createNovelByJsonData(novelObject);
        verify(lightNovelPlugin).getContentChapter(novelId, chapterId);
    }
}
