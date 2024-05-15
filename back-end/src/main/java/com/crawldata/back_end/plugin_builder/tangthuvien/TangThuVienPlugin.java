package com.crawldata.back_end.plugin_builder.tangthuvien;

import com.crawldata.back_end.model.Author;
import com.crawldata.back_end.model.Chapter;
import com.crawldata.back_end.model.Novel;
import com.crawldata.back_end.plugin_builder.PluginFactory;
import com.crawldata.back_end.response.DataResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TangThuVienPlugin implements PluginFactory {
    private static String rootUrl = "https://truyen.tangthuvien.vn/";
    private static Integer totalChaptersPerPage = 75;

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

    private Integer calculateTotalPage(Integer totalElements, Integer numPerPage)
    {
        if(totalElements % numPerPage == 0)
        {
            return totalElements / numPerPage;
        }
        return totalElements/numPerPage + 1;
    }

    private Integer getChapterIdFromUrl(String url)
    {
        String[] parts = url.split("/");
        String[] parts2 = parts[parts.length-1].split("-");
        return Integer.parseInt(parts2[parts2.length-1]);
    }

    @Override
    public int getNovelTotalPages(String url) {
        return 0;
    }

    @Override
    public Integer getNovelTotalChapters(String url) {
        return 0;
    }

    @Override
    public Chapter getNovelChapterDetail(String novelId, String chapterId) {

        String novelName = "";
        Author author = null;
        Integer total = 0;
        String chapterName = "";
        String content = "";
        String novelDetailUrl = "";
        String chapterDetailUrl = "";

        try {
            novelDetailUrl = rootUrl + "/doc-truyen/" + novelId;
            Document doc = Jsoup.connect(novelDetailUrl).get();
            Element bookInformationElement = doc.select(".book-information.cf").get(0);
            novelName = bookInformationElement.child(1).child(0).text();

            String authorUrl = bookInformationElement.child(1).child(1).child(0).attr("href");
            String authorName =  bookInformationElement.child(1).child(1).child(0).text();
            author = new Author(getAuthorIdFromUrl(authorUrl), authorName);

            Element contentNavElement = doc.select(".content-nav-wrap.cf").get(0);
            total =  getTotalChapterFromText(contentNavElement.child(0).child(0).child(1).child(0).text());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            chapterDetailUrl = novelDetailUrl + "/chuong-" + chapterId;
            Document doc = Jsoup.connect(chapterDetailUrl).get();
            Element novelTitleElement = doc.select(".col-xs-12.chapter").get(0);
            chapterName = novelTitleElement.child(1).text();
            content = doc.select(".box-chap").get(0).text();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Chapter(novelId, novelName, chapterId, chapterName, total, author, content);
    }

    @Override
    public DataResponse getNovelListChapters(String novelId, int page) {
        String detailNovelurl = rootUrl + "/doc-truyen/" + novelId;
        String listChaptersUrl = "";
        String novelName = "";
        Author author = null;
        Integer total = 0;
        Integer secreteId = 0;
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

            // get list chapters from novelId and Page
            detailNovelurl = rootUrl + "doc-truyen/page/" +secreteId+"?page=" +(page-1)+"&limit="+ totalChaptersPerPage;
            Document listChaptersDoc = Jsoup.connect(detailNovelurl).get();
            Elements chapterElements = doc.getElementsByTag("li");
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
                String chapterId = getChapterIdFromUrl(chapterElement.child(1).attr("href")) + "";
                String chapteName = chapterElement.child(1).child(0).text();
                listChapters.add(new Chapter(novelId, novelName, chapterId, chapterId, total, author, null));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Calculate total page
        Integer totalPage = calculateTotalPage(total, totalChaptersPerPage);
        return new DataResponse("success", page, totalPage, totalChaptersPerPage, null , listChapters);
    }

    @Override
    public Novel getNovelDetail(String novelId) {
        String  detailNoveUrl = rootUrl + "/doc-truyen/" + novelId;
        String novelName = "";
        String image = "";

        Author author = null;
        String description = "";
        Integer total = 0;

        try {
            Document doc = Jsoup.connect(detailNoveUrl).get();
            Element bookInformationElement = doc.select(".book-information.cf").get(0);
            image = bookInformationElement.child(0).child(0).child(0).attr("src");
            novelName = bookInformationElement.child(1).child(0).text();

            String authorUrl = bookInformationElement.child(1).child(1).child(0).attr("href");
            String authorName =  bookInformationElement.child(1).child(1).child(0).text();
            author = new Author(getAuthorIdFromUrl(authorUrl), authorName);

            Element contentNavElement = doc.select(".content-nav-wrap.cf").get(0);
            total =   getTotalChapterFromText(contentNavElement.child(0).child(0).child(1).child(0).text());


            Element bookContentElement = doc.select(".book-content-wrap.cf").get(0);
            description = bookContentElement.child(0).child(0).child(0).child(0).html();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new Novel(novelId,novelName,image,description,total,author);
    }

    @Override
    public List<Novel> getAuthorNovels(String authorId) {
        
        return List.of();
    }

    @Override
    public List<Novel> getAllNovels(int page, String search) {
        return List.of();
    }
}
