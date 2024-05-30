package com.crawldata.back_end.plugin_builder.tangthuvien;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.*;

public class TangThuVienPlugin implements PluginFactory {
    private static String rootUrl = "https://truyen.tangthuvien.vn/";
    private static final Logger LOGGER = LoggerFactory.getLogger(TangThuVienPlugin.class);

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

    public List<String> getAdjacentChapters(String storyId, String currentChapterId) throws IOException {
        List<String> adjacentChapters = new ArrayList<>();
        String preChapter = null;
        String nextChapter = null;

        // Url to get all list novel's chapters
        String getAllListChapterUrl = rootUrl + "/story/chapters?story_id=" + storyId;
        Document doc = Jsoup.connect(getAllListChapterUrl).get();
        Elements chapterElements = doc.getElementsByTag("li");
        Integer count = 0;
        for (Element chapterElement : chapterElements) {
            String chapterId = getChapterIdFromUrl(chapterElement.child(1).attr("href")).trim();
            if(chapterId.equals(currentChapterId))
            {
                if(count == 0)
                {
                    nextChapter = getChapterIdFromUrl(chapterElements.get(count+1).child(1).attr("href"));
                }
                else if(count == chapterElements.size()-1)
                {
                    preChapter = getChapterIdFromUrl(chapterElements.get(count-1).child(1).attr("href"));
                }
                else {
                    nextChapter = getChapterIdFromUrl(chapterElements.get(count+1).child(1).attr("href"));
                    preChapter = getChapterIdFromUrl(chapterElements.get(count-1).child(1).attr("href"));
                }
                break;
            }
            count++;
        }
        adjacentChapters.add(preChapter);
        adjacentChapters.add(nextChapter);
        return adjacentChapters;
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

            // Get preChapter and nextChapter of the novel
            Element storyHiddenElement = doc.getElementById("story_id_hidden");
            storyId = storyHiddenElement.val();
            List<String> adjacentChapters = getAdjacentChapters(storyId, chapterId);
            preChapterId = adjacentChapters.get(0);
            nextChapterId = adjacentChapters.get(1);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            return DataResponseUtils.getErrorDataResponse(e.getMessage());
        }
        try {
            chapterDetailUrl = novelDetailUrl + "/" + chapterId;
            Document doc = Jsoup.connect(chapterDetailUrl).get();
            doc.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
            Element novelTitleElement = doc.select(".col-xs-12.chapter").get(0);
            chapterName = novelTitleElement.child(1).text();
            content = doc.select(".box-chap").get(0).html().replaceAll("\\r\\n", "<br>").replaceAll("\\n", "<br>");;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            return DataResponseUtils.getErrorDataResponse(e.getMessage());
        }
        Chapter detailChapter = new Chapter(novelId, novelName,chapterId,nextChapterId,preChapterId, chapterName,  author, content);
        return new DataResponse("success", null, null,null,null, detailChapter, null);
    }

    @Override
    public DataResponse getNovelListChapters(String novelId, int page) {
        String detailNovelUrl = rootUrl + "/doc-truyen/" + novelId;
        String listChaptersUrl;
        String novelName = "";
        Author author = null;
        Integer total = 0;
        Integer storyId = 0;
        String preChapterId = null;
        String nextChapterId = null;

        List<Chapter> listChapters = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(detailNovelUrl).get();
            Element bookInformationElement = doc.select(".book-information.cf").get(0);
            novelName = bookInformationElement.child(1).child(0).text();
            String authorUrl = bookInformationElement.child(1).child(1).child(0).attr("href");
            String authorName =  bookInformationElement.child(1).child(1).child(0).text();
            author = new Author(getAuthorIdFromUrl(authorUrl), authorName);

            Element contentNavElement = doc.select(".content-nav-wrap.cf").get(0);
            total =  getTotalChapterFromText(contentNavElement.child(0).child(0).child(1).child(0).text());

            Element storyHiddenElement = doc.getElementById("story_id_hidden");
            storyId = Integer.parseInt(storyHiddenElement.val());

            listChaptersUrl = rootUrl + "/story/chapters?story_id=" + storyId;
            Document listChaptersDoc = Jsoup.connect(listChaptersUrl).get();
            Elements chapterElements = listChaptersDoc.getElementsByTag("li");
            for(Element chapterElement : chapterElements) {
                if (chapterElement.hasClass("divider-chap")) {
                    continue;
                }
                String chapterId = getChapterIdFromUrl(chapterElement.child(1).attr("href")) + "";
                String chapterName = chapterElement.child(1).child(0).text();
                listChapters.add(new Chapter(novelId, novelName, chapterId,nextChapterId,preChapterId, chapterName,  author, null));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            return DataResponseUtils.getErrorDataResponse(e.getMessage());
        }
        return new DataResponse("success",1, 1, listChapters.size(), null , listChapters, null);
    }


    /*@Override
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
                if (count > totalChaptersPerPage || (page - 1) * totalChaptersPerPage + count > total) {
                    break;
                }
                String chapterId = getChapterIdFromUrl(chapterElement.child(1).attr("href")) + "";
                String chapterName = chapterElement.child(1).child(0).text();
                listChapters.add(new Chapter(novelId, novelName, chapterId,nextChapterId,preChapterId, chapterName,  author, null));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            return DataResponseUtils.getErrorDataResponse(e.getMessage());
        }
        // Calculate total page
        Integer totalPage = calculateTotalPage(total, totalChaptersPerPage);
        return new DataResponse("success",totalPage, page, totalChaptersPerPage, null , listChapters, null);
    }*/



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
            LOGGER.error(e.getMessage(),e);
            return DataResponseUtils.getErrorDataResponse(e.getMessage());
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
            LOGGER.error(e.getMessage(),e);
            return DataResponseUtils.getErrorDataResponse(e.getMessage());
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
            LOGGER.error(e.getMessage(),e);
            return DataResponseUtils.getErrorDataResponse(e.getMessage());
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
            LOGGER.error(e.getMessage(),e);
            return DataResponseUtils.getErrorDataResponse(e.getMessage());
        }
        return new DataResponse("success", totalPage,page, null, key, lsNovels, null);
    }
}
