
import com.crawldata.back_end.model.Author;
import com.crawldata.back_end.model.Chapter;
import com.crawldata.back_end.model.Novel;
import com.crawldata.back_end.plugin_builder.PluginFactory;
import com.crawldata.back_end.response.DataResponse;
import com.crawldata.back_end.utils.ConnectJsoup;
import com.crawldata.back_end.utils.DataResponseUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.*;

public class TangThuVienPlugin implements PluginFactory {
    private final Integer TOTAL_CHAPTERS_PER_PAGE = 75;

    private final String ROOT_URL = "https://truyen.tangthuvien.vn/";
    private final String GET_ALL_LIST_CHAPTER_URL = ROOT_URL + "/story/chapters?story_id=%s";
    private final String  NOVEL_DETAIL_URL = ROOT_URL + "/doc-truyen/%s";
    private final String  LIST_CHAPTER_NOVEL_URL1 = ROOT_URL + "/story/chapters?story_id=%s";
    private final String LIST_CHAPTER_NOVEL_URL2 = ROOT_URL + "doc-truyen/page/%s"+"?page=%d"+"&limit="+ TOTAL_CHAPTERS_PER_PAGE;
    private final String AUTHOR_URL = ROOT_URL + "/tac-gia?author=%s";
    private final String SEARCH_URL = ROOT_URL + "/ket-qua-tim-kiem?term=%s" + "&page=%d";
    private final String ALL_NOVEL_URL = ROOT_URL + "/tong-hop?page=%d";
    private final Logger LOGGER = LoggerFactory.getLogger(TangThuVienPlugin.class);

    /**
     * get Author's ID from AUTHOR_URL
     * @param url The URL of the AUTHOR_URL
     * @return the author's id as string
     */
    private String getAuthorIdFromUrl(String url) {
        String[] parts = url.split("\\?");
        // Split the query parameters by "=" to separate the parameter name from the value
        String[] queryParams = parts[1].split("=");
        // The value of the "author" parameter is the second part
        // In case, the url is ".....?author="
        if(queryParams.length == 1)
        {
            return null;
        }
        return queryParams[1];
    }

    /**
     * get Novel's ID from NOVEL_DETAIL_URL
     * @param url The URL of the  NOVEL_DETAIL_URL
     * @return the novel's id as string
     **/
    private String getNovelIdFromUrl(String url)
    {
        String[] components = url.split("/");
        return components[components.length-1];
    }

    /**
     * get total chapters from text Danh sách chương ([totalChapter] chương)
     * @param text The URL of the  NOVEL_DETAIL_URL
     * @return the total chapter as Integer
     **/
    private Integer getTotalChapterFromText(String text)
    {
        String[] parts = text.split("[()]");
        // Extract the number
        // Get the second part and remove leading/trailing spaces
        String numberString = parts[1].trim().split(" ")[0];
        // Parse the number
        return Integer.parseInt(numberString);
    }

     /**
     * get Chapte's ID from NOVEL_DETAIL_URL
     * @param url The URL of the  NOVEL_DETAIL_URL
     * @return the novel's id as string
     **/
    private String getChapterIdFromUrl(String url)
    {
        String[] parts = url.split("/");
        return parts[parts.length-1].trim();
    }

    /**
     * Calculate total page for paging
     * @param totalElements the total chapters per novel
     * @param numPerPage number of chapters per page
     * @return the total page
     **/
    private Integer calculateTotalPage(Integer totalElements, Integer numPerPage) {
        if (totalElements % numPerPage == 0) {
            return totalElements / numPerPage;
        }
        return totalElements / numPerPage + 1;
    }

    /**
     * Return adjacent chapters of current chapter
     * If the current chapter is the first chapter, the previous chapter is null
     * If the current chapter is the last chapter, the next chapter is null
     * @param storyId the story id of novels
     * @param currentChapterId the current chapter's id
     * @return the list adjacent chapters
     **/
    public List<String> getAdjacentChapters(String storyId, String currentChapterId) throws IOException {
        List<String> adjacentChapters = new ArrayList<>();
        String preChapter = null;
        String nextChapter = null;

        // Url to get all list novel's chapters
        String getAllListChapterUrl = String.format(GET_ALL_LIST_CHAPTER_URL,storyId);
        Document doc = ConnectJsoup.connect(getAllListChapterUrl);
        Elements chapterElements = doc.getElementsByTag("li");
        Integer count = 0;
        for (Element chapterElement : chapterElements) {
            String chapterId = getChapterIdFromUrl(chapterElement.child(1).attr("href")).trim();
            if(chapterId.equals(currentChapterId))
            {
                if(count == 0)
                {
                    nextChapter = getChapterIdFromUrl(chapterElements.get(count+1).child(1).attr("href")).trim();
                }
                else if(count == chapterElements.size()-1)
                {
                    preChapter = getChapterIdFromUrl(chapterElements.get(count-1).child(1).attr("href")).trim();
                }
                else {
                    nextChapter = getChapterIdFromUrl(chapterElements.get(count+1).child(1).attr("href")).trim();
                    preChapter = getChapterIdFromUrl(chapterElements.get(count-1).child(1).attr("href")).trim();
                }
                break;
            }
            count++;
        }
        adjacentChapters.add(preChapter);
        adjacentChapters.add(nextChapter);
        return adjacentChapters;
    }


    public List<String> getContentChapter(String url)  {
        String chapterName = "";
        String content = "";
        List<String> contentChapter = new ArrayList<>();
        try {
            Document doc = ConnectJsoup.connect(url);
            doc.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
            Element novelTitleElement = doc.select(".col-xs-12.chapter").get(0);
            chapterName = novelTitleElement.child(1).text();
            content = doc.select(".box-chap").get(0).html().replaceAll("\\r\\n", "<br>").replaceAll("\\n", "<br>");;
        }catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        contentChapter.add(chapterName);
        contentChapter.add(content);
        return contentChapter;
    }

    @Override
    public DataResponse getNovelChapterDetail(String novelId, String chapterId) {
        String novelName = "";
        Author author = null;
        Integer total = 0;
        String chapterName = "";
        String content = "";
        String novelDetailUrl = "";
        String chapterDetailUrl = "";
        String preChapterId = null;
        String nextChapterId = null;
        String storyId = "";

        try {
            novelDetailUrl = String.format(NOVEL_DETAIL_URL, novelId);
            Document doc1 = ConnectJsoup.connect(novelDetailUrl);

            // Get novel name
            Element bookInformationElement = doc1.select(".book-information.cf").get(0);
            novelName = bookInformationElement.child(1).child(0).text();

            // Author url
            String authorUrl = bookInformationElement.child(1).child(1).child(0).attr("href");
            String authorName =  bookInformationElement.child(1).child(1).child(0).text();
            author = new Author(getAuthorIdFromUrl(authorUrl), authorName);

            // Get total
            Element contentNavElement = doc1.select(".content-nav-wrap.cf").get(0);
            total =  getTotalChapterFromText(contentNavElement.child(0).child(0).child(1).child(0).text());

            // Get preChapter and nextChapter of the novel
            Element storyHiddenElement = doc1.getElementById("story_id_hidden");
            storyId = storyHiddenElement.val();
            List<String> adjacentChapters = getAdjacentChapters(storyId, chapterId);
            preChapterId = adjacentChapters.get(0);
            nextChapterId = adjacentChapters.get(1);

            // Get detail Novel Chapter
            chapterDetailUrl = novelDetailUrl + "/" + chapterId;
            List<String> contentChapter = getContentChapter(chapterDetailUrl);
            chapterName = contentChapter.get(0);
            content = contentChapter.get(1);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return DataResponseUtils.getErrorDataResponse(e.getMessage());
        }

        Chapter detailChapter = new Chapter(novelId, novelName,chapterId,nextChapterId,preChapterId, chapterName,  author, content);

        return new DataResponse().status("success").data(detailChapter);
    }

    @Override
    public DataResponse getNovelListChapters(String novelId, String fromChapterId, int numChapters) {
        String detailNovelUrl = String.format(NOVEL_DETAIL_URL, novelId);
        String listChaptersUrl;
        String novelName = "";
        Author author = null;
        Integer total = 0;
        Integer storyId = 0;
        String preChapterId = null;
        String nextChapterId = null;

        List<Chapter> listChapters = new ArrayList<>();
        try {
            Document doc = ConnectJsoup.connect(detailNovelUrl);
            Element bookInformationElement = doc.select(".book-information.cf").get(0);
            novelName = bookInformationElement.child(1).child(0).text();
            String authorUrl = bookInformationElement.child(1).child(1).child(0).attr("href");
            String authorName =  bookInformationElement.child(1).child(1).child(0).text();
            author = new Author(getAuthorIdFromUrl(authorUrl), authorName);

            Element contentNavElement = doc.select(".content-nav-wrap.cf").get(0);
            total =  getTotalChapterFromText(contentNavElement.child(0).child(0).child(1).child(0).text());

            Element storyHiddenElement = doc.getElementById("story_id_hidden");
            storyId = Integer.parseInt(storyHiddenElement.val());

            listChaptersUrl = String.format(LIST_CHAPTER_NOVEL_URL1, storyId);
            Document listChaptersDoc = Jsoup.connect(listChaptersUrl).get();
            Elements chapterElements = listChaptersDoc.getElementsByTag("li");
            for(Element chapterElement : chapterElements) {
                if (chapterElement.hasClass("divider-chap")) {
                    continue;
                }
                String chapterId = getChapterIdFromUrl(chapterElement.child(1).attr("href"));
                String chapterName = chapterElement.child(1).child(0).text();
                listChapters.add(new Chapter().novelId(novelId).novelName(novelName).chapterId(chapterId).nextChapterId(nextChapterId).preChapterId(preChapterId).name(chapterName).author(author));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            return DataResponseUtils.getErrorDataResponse(e.getMessage());
        }

        return new DataResponse().status("success").totalPage(1).currentPage(1).perPage(listChapters.size()).data(listChapters);
    }

    @Override
    public DataResponse getNovelListChapters(String novelId, int page) {
        String detailNovelurl = String.format(NOVEL_DETAIL_URL, novelId);
        String listChaptersUrl;
        String novelName = "";
        Author author = null;
        Integer total = 0;
        Integer secreteId = 0;
        String preChapterId = "";
        String nextChapterId = "";
        List<Chapter> listChapters = new ArrayList<>();
        try {
            Document doc = ConnectJsoup.connect(detailNovelurl);
            Element bookInformationElement = doc.select(".book-information.cf").get(0);
            novelName = bookInformationElement.child(1).child(0).text();
            String authorUrl = bookInformationElement.child(1).child(1).child(0).attr("href");
            String authorName =  bookInformationElement.child(1).child(1).child(0).text();
            author = new Author(getAuthorIdFromUrl(authorUrl), authorName);

            Element contentNavElement = doc.select(".content-nav-wrap.cf").get(0);
            total =  getTotalChapterFromText(contentNavElement.child(0).child(0).child(1).child(0).text());

            Element storyHiddenElement = doc.getElementById("story_id_hidden");
            secreteId = Integer.parseInt(storyHiddenElement.val());

            listChaptersUrl = String.format(LIST_CHAPTER_NOVEL_URL2, secreteId, page-1, TOTAL_CHAPTERS_PER_PAGE);
            Document listChaptersDoc = Jsoup.connect(listChaptersUrl).get();
            Elements chapterElements = listChaptersDoc.getElementsByTag("li");
            int count = 0;
            for(Element chapterElement : chapterElements) {
                if (chapterElement.hasClass("divider-chap") || chapterElement.childrenSize() == 1) {
                    continue;
                }
                count++;
                if (count > TOTAL_CHAPTERS_PER_PAGE || (page - 1) * TOTAL_CHAPTERS_PER_PAGE + count > total) {
                    break;
                }
                String chapterId = getChapterIdFromUrl(chapterElement.child(1).attr("href")) + "";
                String chapterName = chapterElement.child(1).child(0).text();
                listChapters.add(new Chapter().novelId(novelId).novelName(novelName).chapterId(chapterId).nextChapterId(nextChapterId).preChapterId(preChapterId).name(chapterName).author(author));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            return DataResponseUtils.getErrorDataResponse(e.getMessage());
        }
        // Calculate total page
        Integer totalPage = calculateTotalPage(total, TOTAL_CHAPTERS_PER_PAGE);

        return new DataResponse().status("success").totalPage(totalPage).currentPage(page).perPage(TOTAL_CHAPTERS_PER_PAGE).data(listChapters);
    }

    @Override
    public DataResponse getNovelDetail(String novelId) {
        String  detailNoveUrl = String.format(NOVEL_DETAIL_URL, novelId);
        String novelName = "";
        String image = "";
        String firstChapterId = "";
        Author author = null;
        String description = "";

        try {
            Document doc = ConnectJsoup.connect(detailNoveUrl);
            Element bookInformationElement = doc.select(".book-information.cf").get(0);

            // get image
            image = bookInformationElement.child(0).child(0).child(0).attr("src");

            // get novel name
            novelName = bookInformationElement.child(1).child(0).text();

            // get author
            String authorUrl = bookInformationElement.child(1).child(1).child(0).attr("href");
            String authorName =  bookInformationElement.child(1).child(1).child(0).text();
            author = new Author(getAuthorIdFromUrl(authorUrl), authorName);

            // get description
            Element bookContentElement = doc.select(".book-content-wrap.cf").get(0);
            description = bookContentElement.child(0).child(0).child(0).child(0).html();

            // get first chapter id
            Element listChaptersElement = doc.select("ul.cf").get(1);
            String firstChapterUrl = listChaptersElement.child(1).child(0).attr("href");
            firstChapterId = getChapterIdFromUrl(firstChapterUrl);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            return DataResponseUtils.getErrorDataResponse(e.getMessage());
        }

        Novel novel = new Novel(novelId,novelName,image,description,author, firstChapterId);
        return new DataResponse().status("success").data(novel);
    }

    @Override
    public DataResponse getAuthorDetail(String authorId) {
        String authorUrl = String.format(AUTHOR_URL, authorId);
        String authorName = "";
        List<Novel> listNovel = new ArrayList<>();
        try {
            Document doc = ConnectJsoup.connect(authorUrl);
            Element authorPhotoElement = doc.getElementById("authorId");
            authorName = authorPhotoElement.child(1).child(0).text();
            Element bookImgTextElement = doc.getElementsByClass("book-img-text").get(0);
            Elements bookElements = bookImgTextElement.child(0).children();
            for(Element bookElement : bookElements)
            {
                String novelId;
                Author author;
                String novelName;
                String imageURL;
                String description;

                if(bookElement.childrenSize() > 1)
                {
                    imageURL = bookElement.child(0).child(0).child(0).attr("src");
                    String novelUrl  = bookElement.child(0).child(0).attr("href");
                    novelId = getNovelIdFromUrl(novelUrl);
                    novelName = bookElement.child(1).child(0).text();
                    author = new Author(authorId, authorName);

                    description = bookElement.child(1).child(2).html();
                    listNovel.add(new Novel().noveId(novelId).name(novelName).image(imageURL).description(description).author(author));
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            return DataResponseUtils.getErrorDataResponse(e.getMessage());
        }

        return new DataResponse().status("success").data(listNovel);
    }

    @Override
    public DataResponse getAllNovels(int page, String search) {
        List<Novel> lsNovel = new ArrayList<>();
        Integer totalPage = 0;
        Integer perPage = 0;
        String allNovelsUrl = String.format(ALL_NOVEL_URL, page);

        try {
            Document doc = ConnectJsoup.connect(allNovelsUrl);
            Element parentElement = doc.getElementsByClass("book-img-text").get(0);
            // Get total page
            Elements bookElements =  parentElement.child(0).children();
            Element totalPageElement = doc.select("ul.pagination").get(0);
            totalPage = Integer.parseInt(totalPageElement.child(totalPageElement.childrenSize()-2).child(0).text());
            perPage = bookElements.size();

            // Initial data
            String novelId;
            Author author;
            String detailNovelUrl;
            String novelName;
            String imageURL;
            String description;

            for(Element bookElement : bookElements)
            {
                detailNovelUrl = bookElement.child(0).child(0).attr("href");
                novelId = getNovelIdFromUrl(detailNovelUrl);
                imageURL = bookElement.child(0).child(0).child(0).attr("src");
                novelName = bookElement.child(1).child(0).child(0).text();

                String authorName = bookElement.child(1).child(1).child(1).text();
                String authorUrlWebSite =  bookElement.child(1).child(1).child(1).attr("href");
                String authorId = getAuthorIdFromUrl(authorUrlWebSite);
                author = new Author(authorId, authorName);

                description =  bookElement.child(1).child(2).html();
                lsNovel.add(new Novel().noveId(novelId).name(novelName).image(imageURL).description(description).author(author));
            }
        } catch (Exception e)
        {
            LOGGER.error(e.getMessage(),e);
            return DataResponseUtils.getErrorDataResponse(e.getMessage());
        }
        return new DataResponse().status("success").totalPage(totalPage).currentPage(page).perPage(perPage).data(lsNovel);
    }

    @Override
    public DataResponse getNovelSearch(int page, String key, String orderBy) {
        String searchUrl = String.format(SEARCH_URL, key, page);
        Integer totalPage = 1;
        List<Novel> lsNovels = new ArrayList<>();
        try {
            Document doc = ConnectJsoup.connect(searchUrl);
            Element parentElement = doc.getElementsByClass("book-img-text").get(0);
            // Get total page
            try {
                Element totalPageElement = doc.select("ul.pagination").get(0);
                totalPage = Integer.parseInt(totalPageElement.child(totalPageElement.childrenSize()-2).child(0).text());
            } catch (Exception e) {
                System.out.println("No such a pagingation element");
            }

            Elements bookElements =  parentElement.child(0).children();
            // Initial data
            String novelId;
            Author author;
            String detailNovelUrl;
            String novelName;
            String imageURL;
            String description;

            for(Element bookElement : bookElements)
            {
                detailNovelUrl = bookElement.child(0).child(0).attr("href");
                novelId =getNovelIdFromUrl(detailNovelUrl);
                imageURL = bookElement.child(0).child(0).child(0).attr("src");
                novelName = bookElement.child(1).child(0).child(0).text();

                String authorName = bookElement.child(1).child(1).child(1).text();
                String authorUrlWebSite =  bookElement.child(1).child(1).child(1).attr("href");
                String authorId =getAuthorIdFromUrl(authorUrlWebSite);
                author = new Author(authorId, authorName);
                description =  bookElement.child(1).child(2).html();

                lsNovels.add(new Novel().noveId(novelId).name(novelName).image(imageURL).description(description).author(author));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            return DataResponseUtils.getErrorDataResponse(e.getMessage());
        }
        return new DataResponse().status("success").totalPage(totalPage).currentPage(page).searchValue(key).data(lsNovels);
    }

    @Override
    public Chapter getContentChapter(String novelId, String chapterId) {
        String chapterDetailUrl = String.format(NOVEL_DETAIL_URL, novelId) + "/" + chapterId;
        List<String> contentChapter = getContentChapter(chapterDetailUrl);
        String chapterName = contentChapter.get(0);
        String content = contentChapter.get(1);
        return new Chapter().content(content).name(chapterName);
    }
}
