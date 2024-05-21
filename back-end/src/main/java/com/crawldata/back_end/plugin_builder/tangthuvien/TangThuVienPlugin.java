
import com.crawldata.back_end.model.Author;
import com.crawldata.back_end.model.Chapter;
import com.crawldata.back_end.model.Novel;
import com.crawldata.back_end.plugin_builder.PluginFactory;
import com.crawldata.back_end.response.DataResponse;
import com.crawldata.back_end.utils.DataResponseUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.*;

public class TangThuVienPlugin implements PluginFactory {
    private static String rootUrl = "https://truyen.tangthuvien.vn/";
    private static Integer totalChaptersPerPage = 75;
    private final String getAllNovelErrorMessage = "Error when getting all novels. The page value may be not valid or the server has errors when get data";
    private final String getNovelSearchErrorMessage = "Error when getting novel search. The key may be not found or the page is not valid!";
    private final String getDetailAuthorErrorMessage = "Error when getting detal author. The author ID may be not found.";
    private final String getDetailNovelErrorMessage = "Error when getting detail novel. The novel id may not be valid!";
    private final String getDetailChapterErrorMessage = "Error when getting detail chapter. The chapter ID may not be valid!";
    private final String getNovelListChaptersErrorMessage = "Error when getting chapters from novel. The novel ID or the page is not valid!";

    private String getAuthorIdFromUrl(String url) {
        String[] parts = url.split("\\?");
        // Split the query parameters by "=" to separate the parameter name from the value
        String[] queryParams = parts[1].split("=");
        // The value of the "author" parameter is the second part
        return queryParams[1];
    }
    private String getNovelIdFromUrl(String url)
    {
        String[] components = url.split("/");
        return components[components.length-1];
    }
    private Integer getTotalChapterFromText(String text)
    {
        String[] parts = text.split("[()]");
        // Extract the number
        // Get the second part and remove leading/trailing spaces
        String numberString = parts[1].trim().split(" ")[0];
        // Parse the number
        return Integer.parseInt(numberString);
    }

    private String getChapterIdFromUrl(String url)
    {
        String[] parts = url.split("/");
        return parts[parts.length-1];
    }
    private Integer calculateTotalPage(Integer totalElements, Integer numPerPage) {
        if (totalElements % numPerPage == 0) {
            return totalElements / numPerPage;
        }
        return totalElements / numPerPage + 1;
    }

    public Integer getNumberFromChapterId(String chapterId)
    {
        String[] parts = chapterId.split("-");
        return Integer.parseInt(parts[parts.length-1]);
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
        Integer maxCurrentChapter;
        Integer minCurrentChapter;
        String preChapterId = null;
        String nextChapterId = null;

        try {
            novelDetailUrl = rootUrl + "/doc-truyen/" + novelId;
            Document doc = Jsoup.connect(novelDetailUrl).get();

            // Get novel name
            Element bookInformationElement = doc.select(".book-information.cf").get(0);
            novelName = bookInformationElement.child(1).child(0).text();

            // Author url
            String authorUrl = bookInformationElement.child(1).child(1).child(0).attr("href");
            String authorName =  bookInformationElement.child(1).child(1).child(0).text();
            author = new Author(getAuthorIdFromUrl(authorUrl), authorName);

            // Get total
            Element contentNavElement = doc.select(".content-nav-wrap.cf").get(0);
            total =  getTotalChapterFromText(contentNavElement.child(0).child(0).child(1).child(0).text());

            // Get maxCurrentChapter, get minCurrentChapter
            Element listChaptersElement = doc.select("ul.cf").get(1);
            String firstChapterUrl = listChaptersElement.child(1).child(0).attr("href");
            minCurrentChapter = getNumberFromChapterId(getChapterIdFromUrl(firstChapterUrl));

            Element listNewestChapterElement = doc.select("ul.cf").get(0);
            String newestChapterUrl =  listNewestChapterElement.child(0).child(0).attr("href");
            maxCurrentChapter = getNumberFromChapterId(getChapterIdFromUrl(newestChapterUrl));

        } catch (Exception e) {
            return DataResponseUtils.getErrorDataResponse(getDetailNovelErrorMessage);
        }

        try {
            chapterDetailUrl = novelDetailUrl + "/" + chapterId;
            Document doc = Jsoup.connect(chapterDetailUrl).get();
            Element novelTitleElement = doc.select(".col-xs-12.chapter").get(0);
            chapterName = novelTitleElement.child(1).text();
            content = doc.select(".box-chap").get(0).html();

            // get preChapter and nextChapter
            Integer currentNumberChapterId = getNumberFromChapterId(chapterId);
            if(currentNumberChapterId > minCurrentChapter && currentNumberChapterId < maxCurrentChapter)
            {
                preChapterId = "chuong-" + (currentNumberChapterId-1);
                nextChapterId = "chuong-" + (currentNumberChapterId + 1);
            }
            else if(currentNumberChapterId.equals(minCurrentChapter) && currentNumberChapterId < maxCurrentChapter )
            {
                nextChapterId =  "chuong-" + (currentNumberChapterId + 1);
            }
            else if(currentNumberChapterId.equals(maxCurrentChapter) && currentNumberChapterId > minCurrentChapter)
            {
                preChapterId = "chuong-" + (currentNumberChapterId-1);
            }
        } catch (Exception e) {
            return DataResponseUtils.getErrorDataResponse(getDetailChapterErrorMessage);
        }

        Chapter detailChapter = new Chapter(novelId, novelName,chapterId,nextChapterId,preChapterId, chapterName,  author, content);
        return new DataResponse("success", null, null,null,null, detailChapter, null);
    }


    @Override
    public DataResponse getNovelListChapters(String novelId, int page) {
        String detailNovelurl = rootUrl + "/doc-truyen/" + novelId;
        String listChaptersUrl;
        String novelName = "";
        Author author = null;
        Integer total = 0;
        Integer secreteId = 0;
        String preChapterId = "";
        String nextChapterId = "";
        List<Chapter> listChapters = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(detailNovelurl).get();
            Element bookInformationElement = doc.select(".book-information.cf").get(0);
            novelName = bookInformationElement.child(1).child(0).text();
            String authorUrl = bookInformationElement.child(1).child(1).child(0).attr("href");
            String authorName =  bookInformationElement.child(1).child(1).child(0).text();
            author = new Author(getAuthorIdFromUrl(authorUrl), authorName);

            Element contentNavElement = doc.select(".content-nav-wrap.cf").get(0);
            total =  getTotalChapterFromText(contentNavElement.child(0).child(0).child(1).child(0).text());

            Element storyHiddenElement = doc.getElementById("story_id_hidden");
            secreteId = Integer.parseInt(storyHiddenElement.val());

            listChaptersUrl = rootUrl + "doc-truyen/page/" +secreteId+"?page=" +(page-1)+"&limit="+ totalChaptersPerPage;
            Document listChaptersDoc = Jsoup.connect(listChaptersUrl).get();
            Elements chapterElements = listChaptersDoc.getElementsByTag("li");
            int count = 0;
            for(Element chapterElement : chapterElements) {
                if (chapterElement.hasClass("divider-chap") || chapterElement.childrenSize() == 1) {
                    continue;
                }
                count++;
                System.out.println("Count : " + (page - 1) * totalChaptersPerPage + count + " , Total : " + total);
                if (count > totalChaptersPerPage || (page - 1) * totalChaptersPerPage + count > total) {
                    break;
                }
                System.out.println(chapterElement.html());
                String chapterId = getChapterIdFromUrl(chapterElement.child(1).attr("href")) + "";
                String chapterName = chapterElement.child(1).child(0).text();
                listChapters.add(new Chapter(novelId, novelName, chapterId,nextChapterId,preChapterId, chapterName,  author, null));
            }
        } catch (Exception e) {
            return DataResponseUtils.getErrorDataResponse(getNovelListChaptersErrorMessage);
        }
        // Calculate total page
        Integer totalPage = calculateTotalPage(total, totalChaptersPerPage);
        return new DataResponse("success",totalPage, page, totalChaptersPerPage, null , listChapters, null);
    }

    @Override
    public DataResponse getNovelDetail(String novelId) {
        String  detailNoveUrl = rootUrl + "/doc-truyen/" + novelId;
        String novelName = "";
        String image = "";
        String firstChapterId = "";
        Author author = null;
        String description = "";

        try {
            Document doc = Jsoup.connect(detailNoveUrl).get();
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
            return DataResponseUtils.getErrorDataResponse(getDetailNovelErrorMessage);
        }
        Novel novel = new Novel(novelId,novelName,image,description,author, firstChapterId);
        return new DataResponse("success", null, null,null,null, novel, null);
    }

    @Override
    public DataResponse getAuthorDetail(String authorId) {
        String authorUrl = rootUrl + "/tac-gia?author=" + authorId;
        String authorName = "";
        List<Novel> listNovel = new ArrayList<>();
        String firstChapter = "";
        try {
            Document doc = Jsoup.connect(authorUrl).get();
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
                    listNovel.add(new Novel(novelId, novelName, imageURL, description, author, firstChapter));
                }
            }
        }
        catch (Exception e) {
            return DataResponseUtils.getErrorDataResponse(getDetailAuthorErrorMessage);
        }
        return new DataResponse("success", null, null,null,null, listNovel, null);
    }

    @Override
    public DataResponse getAllNovels(int page, String search) {
        List<Novel> lsNovel = new ArrayList<>();
        Integer totalPage = 0;
        Integer perPage = 0;
        String url = rootUrl + "/tong-hop?page=" + page;

        try {
            Document doc = Jsoup.connect(url).get();
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
            Integer totalChapter;
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
                totalChapter = Integer.parseInt(bookElement.child(1).child(1).child(7).child(0).text());
                description =  bookElement.child(1).child(2).html();

                lsNovel.add(new Novel(novelId, novelName, imageURL, description, author, null));
            }
        } catch (Exception e)
        {
            return DataResponseUtils.getErrorDataResponse(getAllNovelErrorMessage);
        }
        return new DataResponse("success", totalPage, page,perPage,null, lsNovel, null);
    }

    @Override
    public DataResponse getNovelSearch(int page, String key, String orderBy) {
        String url = rootUrl + "/ket-qua-tim-kiem?term=" + key + "&page=" + page;
        Integer totalPage = 1;
        List<Novel> lsNovels = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).get();
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

                lsNovels.add(new Novel(novelId, novelName, imageURL, description,author , null));
            }
        } catch (Exception e) {
            return DataResponseUtils.getErrorDataResponse(getNovelSearchErrorMessage);
        }
        Collections.sort(lsNovels, new Comparator<Novel>() {
            @Override
            public int compare(Novel o1, Novel o2) {
                return orderBy.equals("z-a") ?  o2.getName().compareToIgnoreCase(o1.getName()) : o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        return new DataResponse("success", totalPage,page, null, key, lsNovels, null);
    }
}
