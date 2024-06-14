//package com.crawldata.demo.plugin_builder.tangthuvien;
//
//import com.crawldata.back_end.model.Author;
//import com.crawldata.back_end.model.Chapter;
//import com.crawldata.back_end.model.Novel;
//import com.crawldata.back_end.plugin_builder.tangthuvien.TangThuVienPlugin;
//import com.crawldata.back_end.response.DataResponse;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.MockitoAnnotations;
//import org.mockito.Spy;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.doReturn;
//
//public class TangThuVienPluginTest {
//    private final Integer TOTAL_CHAPTERS_PER_PAGE = 75;
//    private final String ROOT_URL = "https://truyen.tangthuvien.vn/";
//    private final String GET_ALL_LIST_CHAPTER_URL = ROOT_URL + "/story/chapters?story_id=%s";
//    private final String  NOVEL_DETAIL_URL = ROOT_URL + "/doc-truyen/%s";
//    private final String  LIST_CHAPTER_NOVEL_URL1 = ROOT_URL + "/story/chapters?story_id=%s";
//    private final String LIST_CHAPTER_NOVEL_URL2 = ROOT_URL + "doc-truyen/page/%s"+"?page=%d"+"&limit="+ TOTAL_CHAPTERS_PER_PAGE;
//    private final String AUTHOR_URL = ROOT_URL + "/tac-gia?author=%s";
//    private final String SEARCH_URL = ROOT_URL + "/ket-qua-tim-kiem?term=%s" + "&page=%d";
//    private final String ALL_NOVEL_URL = ROOT_URL + "/tong-hop?page=%d";
//    @Spy
//    private TangThuVienPlugin tangThuVienPlugin;
//    @BeforeEach
//    public void setUp()
//    {
//        MockitoAnnotations.openMocks(this);
//    }
//
//
//
//
//    @Test
//    public void getNovelDetail_validNovelId_success() throws IOException {
//        String novelId = "valid-novel-id";
//        Novel novel = new Novel("1","name","image","description",new Author("john-wick","john wick"),"chuong-1");
//        Map<String,Object> map = new HashMap<>();
//        map.put("image",novel.getImage());
//        map.put("novelName",novel.getName());
//        map.put("author",novel.getAuthor());
//        map.put("description",novel.getDescription());
//        map.put("firstChapterId",novel.getFirstChapter());
//
//        doReturn(map).when(tangThuVienPlugin).mapNovelInfo(novelId);
//        DataResponse result = tangThuVienPlugin.getNovelDetail(novelId);
//        assertNotNull(result);
//        assertEquals("success", result.getStatus());
//        assertEquals(null, result.getTotalPage());
//        assertEquals(null, result.getCurrentPage());
//        assertNotNull(result.getData());
//        Novel value = (Novel) result.getData();
//        assertEquals("description", value.getDescription());
//        assertEquals("image", value.getImage());
//        assertEquals("john wick", value.getAuthor().getName());
//    }
//    @Test
//    public void getNovelDetail_invalidNovelId_error() throws IOException {
//        String novelId = "invalid-novel-id";
//        Novel novel = new Novel("1","name","image","description",new Author("john-wick","john wick"),"chuong-1");
//        Map<String,Object> map = new HashMap<>();
//        map.put("image",novel.getImage());
//        map.put("novelName",novel.getName());
//        map.put("author",novel.getAuthor());
//        map.put("description",novel.getDescription());
//        map.put("firstChapterId",novel.getFirstChapter());
//
//        doReturn(null).when(tangThuVienPlugin).mapNovelInfo(novelId);
//        DataResponse result = tangThuVienPlugin.getNovelDetail(novelId);
//        assertNotNull(result);
//        assertEquals("success", result.getStatus());
//        assertEquals(null, result.getTotalPage());
//        assertEquals(null, result.getCurrentPage());
//        assertNotNull(result.getData());
//        Novel value = (Novel) result.getData();
//        assertEquals("description", value.getDescription());
//        assertEquals("image", value.getImage());
//        assertEquals("john wick", value.getAuthor().getName());
//    }
//    @Test
//    public void getNovelListChapters_validNovelId_success() throws IOException {
//        String novelId = "valid-novel-id";
//        int page = 1 ;
//        int total =1;
//        Novel novel = new Novel(novelId,"name","image","description",new Author("john-wick","john wick"),"chuong-1");
//        Chapter chapter = new Chapter("1","name","chuong-1","chuong-2",null,"test chapter",novel.getAuthor(),"this is content");
//        Map<String,Object> map = new HashMap<>();
//        map.put("novelName",novel.getName());
//        map.put("author",novel.getAuthor());
//        map.put("total",1);
//        map.put("storyId",novel.getNovelId());
//        List<Chapter> chaptersFake = new ArrayList<>();
//        chaptersFake.add(chapter);
//        doReturn(map).when(tangThuVienPlugin).mapNovelInfo(novelId);
//        doReturn(chaptersFake).when(tangThuVienPlugin).getChapterPerPageImpl(novelId,page,total);
//        DataResponse result = tangThuVienPlugin.getNovelListChapters(novelId,page);
//        assertNotNull(result);
//        assertEquals("success", result.getStatus());
//        assertEquals(1, result.getTotalPage());
//        assertEquals(1, result.getCurrentPage());
//        assertNotNull(result.getData());
//        List<Chapter> chapters = (List<Chapter>) result.getData();
//        assertFalse(chapters.isEmpty());
//        Chapter value = chapters.get(0);
//        assertEquals("this is content", value.getContent());
//        assertEquals(chapter.getName(), value.getName());
//        assertEquals(chapter.getNovelName(),value.getNovelName());
//    }
//    @Test
//    public void getNovelListChapters_invalidNovelId_error() throws IOException {
//        String novelId = "invalid-novel-id";
//        int page = 1 ;
//        int total =1;
//        Novel novel = new Novel(novelId,"name","image","description",new Author("john-wick","john wick"),"chuong-1");
//        Chapter chapter = new Chapter("1","name","chuong-1","chuong-2",null,"test chapter",novel.getAuthor(),"this is content");
//        Map<String,Object> map = new HashMap<>();
//        map.put("novelName",novel.getName());
//        map.put("author",novel.getAuthor());
//        map.put("total",1);
//        map.put("storyId",novel.getNovelId());
//        List<Chapter> chaptersFake = new ArrayList<>();
//        chaptersFake.add(chapter);
//        doReturn(null).when(tangThuVienPlugin).mapNovelInfo(novelId);
//        doReturn(null).when(tangThuVienPlugin).getChapterPerPageImpl(novelId,page,total);
//        DataResponse result = tangThuVienPlugin.getNovelListChapters(novelId,page);
//        assertNotNull(result);
//        assertEquals("success", result.getStatus());
//        assertEquals(1, result.getTotalPage());
//        assertEquals(1, result.getCurrentPage());
//        assertNotNull(result.getData());
//        List<Chapter> chapters = (List<Chapter>) result.getData();
//        assertFalse(chapters.isEmpty());
//        Chapter value = chapters.get(0);
//        assertEquals("this is content", value.getContent());
//        assertEquals(chapter.getName(), value.getName());
//        assertEquals(chapter.getNovelName(),value.getNovelName());
//    }
//    @Test
//    public void getNovelChapterDetail_validNovelIdAndChapterId_success() throws IOException {
//        String novelId = "valid-novel-id";
//        String chapterId = "valid-chapter-id";
//        Novel novel = new Novel(novelId,"name","image","description",new Author("john-wick","john wick"),"chuong-1");
//        Chapter chapter = new Chapter(chapterId,"name","chuong-1","chuong-2",null,"test chapter",novel.getAuthor(),"this is content");
//        String chapterDetailUrl = String.format(NOVEL_DETAIL_URL, novelId) + "/" + chapterId;
//        Map<String,Object> map = new HashMap<>();
//        map.put("novelName",novel.getName());
//        map.put("author",novel.getAuthor());
//        map.put("total",1);
//        map.put("storyId",novel.getNovelId());
//        map.put("preChapter",chapter.getPreChapterId());
//        map.put("nextChapter",chapter.getNextChapterId());
//        map.put("chapterName",chapter.getName());
//        map.put("content",chapter.getContent());
//        doReturn(map).when(tangThuVienPlugin).mapNovelInfo(novelId);
//        doReturn(map).when(tangThuVienPlugin).getAdjacentChapters(novelId,chapterId);
//        doReturn(map).when(tangThuVienPlugin).getContentChapter(chapterDetailUrl);
//        DataResponse result = tangThuVienPlugin.getNovelChapterDetail(novelId,chapterId);
//        assertNotNull(result);
//        assertEquals("success", result.getStatus());
//        assertEquals(null, result.getTotalPage());
//        assertEquals(null, result.getCurrentPage());
//        assertNotNull(result.getData());
//        Chapter value = (Chapter) result.getData();
//        assertEquals("this is content", value.getContent());
//        assertEquals(chapter.getName(), value.getName());
//        assertEquals(chapter.getNovelName(),value.getNovelName());
//    }
//    @Test
//    public void getNovelChapterDetail_invalidNovelId_error() throws IOException {
//        String novelId = "invalid-novel-id";
//        String chapterId = "valid-chapter-id";
//        Novel novel = new Novel(novelId,"name","image","description",new Author("john-wick","john wick"),"chuong-1");
//        Chapter chapter = new Chapter(chapterId,"name","chuong-1","chuong-2",null,"test chapter",novel.getAuthor(),"this is content");
//        String chapterDetailUrl = String.format(NOVEL_DETAIL_URL, novelId) + "/" + chapterId;
//        Map<String,Object> map = new HashMap<>();
//        map.put("novelName",novel.getName());
//        map.put("author",novel.getAuthor());
//        map.put("total",1);
//        map.put("storyId",novel.getNovelId());
//        map.put("preChapter",chapter.getPreChapterId());
//        map.put("nextChapter",chapter.getNextChapterId());
//        map.put("chapterName",chapter.getName());
//        map.put("content",chapter.getContent());
//        doReturn(null).when(tangThuVienPlugin).mapNovelInfo(novelId);
//        doReturn(map).when(tangThuVienPlugin).getAdjacentChapters(novelId,chapterId);
//        doReturn(map).when(tangThuVienPlugin).getContentChapter(chapterDetailUrl);
//        DataResponse result = tangThuVienPlugin.getNovelChapterDetail(novelId,chapterId);
//        assertNotNull(result);
//        assertEquals("success", result.getStatus());
//        assertEquals(null, result.getTotalPage());
//        assertEquals(null, result.getCurrentPage());
//        assertNotNull(result.getData());
//        Chapter value = (Chapter) result.getData();
//        assertEquals("this is content", value.getContent());
//        assertEquals(chapter.getName(), value.getName());
//        assertEquals(chapter.getNovelName(),value.getNovelName());
//    }
//    @Test
//    public void getNovelChapterDetail_invalidChapterId_error() throws IOException {
//        String novelId = "valid-novel-id";
//        String chapterId = "invalid-chapter-id";
//        Novel novel = new Novel(novelId,"name","image","description",new Author("john-wick","john wick"),"chuong-1");
//        Chapter chapter = new Chapter(chapterId,"name","chuong-1","chuong-2",null,"test chapter",novel.getAuthor(),"this is content");
//        String chapterDetailUrl = String.format(NOVEL_DETAIL_URL, novelId) + "/" + chapterId;
//        Map<String,Object> map = new HashMap<>();
//        map.put("novelName",novel.getName());
//        map.put("author",novel.getAuthor());
//        map.put("total",1);
//        map.put("storyId",novel.getNovelId());
//        map.put("preChapter",chapter.getPreChapterId());
//        map.put("nextChapter",chapter.getNextChapterId());
//        map.put("chapterName",chapter.getName());
//        map.put("content",chapter.getContent());
//        doReturn(map).when(tangThuVienPlugin).mapNovelInfo(novelId);
//        doReturn(null).when(tangThuVienPlugin).getAdjacentChapters(novelId,chapterId);
//        doReturn(null).when(tangThuVienPlugin).getContentChapter(chapterDetailUrl);
//        DataResponse result = tangThuVienPlugin.getNovelChapterDetail(novelId,chapterId);
//        assertNotNull(result);
//        assertEquals("success", result.getStatus());
//        assertEquals(null, result.getTotalPage());
//        assertEquals(null, result.getCurrentPage());
//        assertNotNull(result.getData());
//        Chapter value = (Chapter) result.getData();
//        assertEquals("this is content", value.getContent());
//        assertEquals(chapter.getName(), value.getName());
//        assertEquals(chapter.getNovelName(),value.getNovelName());
//    }
//
//
//    @Test
//    void testGetAllNovelListChaptersError() {
//        String novelId = "dai-dao-ky";
//
//        String expectedStatus = "error";
//        String fromChapter = "chuong-1";
//        int numChapter = 10;
//
//        // Calling the method under test
//        DataResponse response = tangThuVienPlugin.getNovelListChapters(novelId, fromChapter, numChapter);
//        // Assertions
//        assertNotNull(response);
//        assertEquals(expectedStatus, response.getStatus());
//    }
//    @Test
//    void getNovelsPerPageSuccess()
//    {
//        String expectedStatus = "success";
//        int currentPage = 1;
//        int totalPage = 599;
//        int perPage = 20;
//        int size = 20;
//        DataResponse dataResponse = tangThuVienPlugin.getAllNovels(1, "");
//        List<Novel> novels = (List<Novel>) dataResponse.getData();
//        // Assertions
//        assertNotNull(dataResponse);
//        assertEquals(expectedStatus,dataResponse.getStatus());
//        assertEquals(currentPage, dataResponse.getCurrentPage());
//        assertEquals(totalPage, dataResponse.getTotalPage());
//        assertEquals(perPage, dataResponse.getPerPage());
//        assertEquals(size, novels.size());
//    }
//    @Test
//    void getNovelsPerPageError()
//    {
//        String expectedStatus = "error";
//        DataResponse dataResponse = tangThuVienPlugin.getAllNovels(600, "");
//        // Assertions
//        assertNotNull(dataResponse);
//        assertEquals(expectedStatus,dataResponse.getStatus());
//    }
//    @Test
//    void getNovelSearchSuccess()
//    {
//        String keyword = "kiếm";
//        int page = 1;
//        String orderBy = "a-z";
//        DataResponse dataResponse = tangThuVienPlugin.getNovelSearch(page, keyword, orderBy);
//        List<Novel> novels = (List<Novel>) dataResponse.getData();
//        String expectedStatus = "success";
//        int currentPage = 1;
//        int totalPage = 27;
//        String searchValue = "kiếm";
//        int size = 20;
//        // Assertions
//        assertNotNull(dataResponse);
//        assertEquals(expectedStatus,dataResponse.getStatus());
//        assertEquals(totalPage, dataResponse.getTotalPage());
//        assertEquals(currentPage, dataResponse.getCurrentPage());
//        assertEquals(searchValue, dataResponse.getSearchValue());
//        assertEquals(size, novels.size());
//    }
//    @Test
//    void getNovelSearchError1()
//    {
//        String keyword = "kiếm";
//        int page = 1000;
//        String orderBy = "a-z";
//        DataResponse dataResponse = tangThuVienPlugin.getNovelSearch(page, keyword, orderBy);
//        String expectedStatus = "error";
//        // Assertions
//        assertNotNull(dataResponse);
//        assertEquals(expectedStatus,dataResponse.getStatus());
//    }
//    @Test
//    void getNovelSearchError2()
//    {
//        String keyword = "zzzzzzzzzzzzzzzzzz";
//        int page = 1000;
//        String orderBy = "a-z";
//        DataResponse dataResponse = tangThuVienPlugin.getNovelSearch(page, keyword, orderBy);
//        String expectedStatus = "error";
//        // Assertions
//        assertNotNull(dataResponse);
//        assertEquals(expectedStatus,dataResponse.getStatus());
//    }
//}