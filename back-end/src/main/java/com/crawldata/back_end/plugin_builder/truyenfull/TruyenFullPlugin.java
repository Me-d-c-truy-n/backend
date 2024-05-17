package com.crawldata.back_end.plugin_builder.truyenfull;

import com.crawldata.back_end.model.Author;
import com.crawldata.back_end.model.Chapter;
import com.crawldata.back_end.model.Novel;
import com.crawldata.back_end.plugin_builder.PluginFactory;
import com.crawldata.back_end.response.DataResponse;
import com.crawldata.back_end.utils.ConnectJsoup;
import com.crawldata.back_end.utils.HandleString;
import com.crawldata.back_end.utils.SourceNovels;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
public class TruyenFullPlugin implements PluginFactory {
    public int getNovelTotalPages(String url) {
        Document doc = null;
        try {
            doc = ConnectJsoup.connect(url);
            Elements pages = doc.select("ul[class=pagination pagination-sm] li");
            int totalPages = 1;
            if (pages.size() != 0) {
                StringBuilder linkEndPage = new StringBuilder();
                Element page = pages.get(pages.size() - 2);
                if (page.text().equals("Cuối »")) {
                    linkEndPage.append(page.select("a").attr("href"));
                    String linkValid = HandleString.getValidURL(linkEndPage.toString());
                    Document docPage = ConnectJsoup.connect(linkValid);
                    Elements allPage = docPage.select("ul[class=pagination pagination-sm] li");
                    totalPages = Integer.parseInt(allPage.get(allPage.size() - 2).text().split(" ")[0]);
                } else if (page.text().equals("Trang tiếp")) {
                    Element pageNext = pages.get(pages.size() - 1);
                    linkEndPage.append(pageNext.select("a").attr("href"));
                    Document docPage =  ConnectJsoup.connect(linkEndPage.toString());
                    Elements allPage = docPage.select("ul[class=pagination pagination-sm] li");
                    totalPages = Integer.parseInt(allPage.get(allPage.size() - 1).text().split(" ")[0]);
                } else {
                    totalPages = Integer.parseInt(page.text());
                }
            }
            return totalPages;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public  String getEndSlugFromUrl(String url)
    {
        String[] parts = url.split("/");
        return parts[parts.length-1];
    }
    public  String getValidPreChapter(String idChapter)
    {
       String[] parts = idChapter.split("-");
       int numberChap = Integer.parseInt(parts[parts.length-1]);
       if(numberChap>1)
           return "chuong-"+ (numberChap-1);
        return null;
    }
    public int  getChapterEnd(String url)  {
        StringBuilder linkEndPage = new StringBuilder();
        int chapterEnd = 0;
        try {
            Document doc = ConnectJsoup.connect(url);
            Elements pages = doc.select("ul[class=pagination pagination-sm] li");
            if (pages.size() == 0) {
                chapterEnd = doc.select("ul[class=list-chapter] li").size();
                return chapterEnd;
            } else {
                Element page = pages.get(pages.size() - 2);
                if (page.text().equals("Trang tiếp")) {
                    Element pageNext = pages.get(pages.size() - 1);
                    linkEndPage.append(pageNext.select("a").attr("href"));
                }
                else {
                    linkEndPage.append(page.select("a").attr("href"));
                }
            }
            doc = ConnectJsoup.connect(linkEndPage.toString());
            Elements chaptersLink =  doc.select("ul[class=list-chapter] li a");
            String linkEndChapter = chaptersLink.get(chaptersLink.size()-1).attr("href");
            String idEndChapter = getEndSlugFromUrl(linkEndChapter);
            String[] parts = idEndChapter.split("-");
            chapterEnd = Integer.parseInt(parts[parts.length-1]);
            return chapterEnd;
        }
        catch (Exception e)
        {
            throw  new RuntimeException(e.getMessage());
        }
    }
    public  String getValidNextChapter(String idChapter,int endChapter)
    {
        String[] parts = idChapter.split("-");
        int numberChap = Integer.parseInt(parts[parts.length-1]);
        if(numberChap<endChapter)
            return "chuong"+ (numberChap+1);
        return null;
    }
    @Override
    public DataResponse getNovelChapterDetail(String novelId, String chapterId) {
        String urlChapter = SourceNovels.NOVEL_MAIN+novelId+"/"+chapterId;
        String urlAuthor = SourceNovels.NOVEL_MAIN+novelId;
        Document doc = null;
        try
        {
        doc = ConnectJsoup.connect(urlAuthor);
        String nameAuthor = doc.select("a[itemprop=author]").text();
        Author author = new Author(HandleString.makeSlug(nameAuthor),nameAuthor);
        doc = ConnectJsoup.connect(urlChapter);
        String novelName = doc.select("a[class=truyen-title]").first().text();
        String chapterName = doc.select("a[class=chapter-title]").first().text();
        Elements content = doc.select("div#chapter-c");
        String nextChapterURL = doc.select("a[id=next_chap]").attr("href");
        String idNextChapter = nextChapterURL.split("/").length!=1? nextChapterURL.split("/")[nextChapterURL.split("/").length-1]:null;
        String preChapterURL = doc.select("a[id=prev_chap]").attr("href");
        String idPreChapter = preChapterURL.split("/").length != 1? preChapterURL.split("/")[preChapterURL.split("/").length-1]:null;
        Chapter chapterDetail = new Chapter(novelId,novelName,chapterId,idNextChapter,idPreChapter,chapterName,author,content.toString());
        DataResponse dataResponse = new DataResponse();
        dataResponse.setStatus("success");
        dataResponse.setData(chapterDetail);
        return dataResponse;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DataResponse getNovelListChapters(String novelId, int page) {
        String url = SourceNovels.NOVEL_MAIN + novelId;
        Document doc = null;
        try {
        doc = ConnectJsoup.connect(url);
        String name = doc.select("h3[class=title]").first().text();
        String authorName = doc.select("a[itemprop=author]").first().text();
        Author author = new Author(HandleString.makeSlug(authorName),authorName);
        List<Chapter> chapterList = new ArrayList<>();
        Integer totalPages = getNovelTotalPages(url);
        String link = String.format("https://truyenfull.vn/%s/trang-%d",novelId,page);
        doc = ConnectJsoup.connect(link);
        Elements chapters = doc.select("ul[class=list-chapter] li");
        int endChapter = getChapterEnd(SourceNovels.NOVEL_MAIN+novelId);
        for (Element chapter : chapters) {
            String nameChapter = chapter.selectFirst("a").text();
            String linkChapter = chapter.selectFirst("a").attr("href");
            String idChapter = linkChapter.split("/")[linkChapter.split("/").length-1];
            String idPreChapter = getValidPreChapter(idChapter);
            String idNextChapter = getValidNextChapter(idChapter,endChapter);
           Chapter chapterObj = new Chapter(novelId,name,idChapter,idNextChapter,idPreChapter,nameChapter,author, "");
            chapterList.add(chapterObj);
        }
        DataResponse dataResponse = new DataResponse();
        dataResponse.setStatus("success");
        dataResponse.setData(chapterList);
        dataResponse.setTotalPage(totalPages);
        dataResponse.setPerPage(chapterList.size());
        return dataResponse;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public DataResponse getNovelDetail(String novelId) {
        String url = SourceNovels.NOVEL_MAIN+novelId;
        Document doc = null;
        try {
            doc = ConnectJsoup.connect(url);
        String name = doc.select("h3[class=title]").first().text();
        String authorName = doc.select("a[itemprop=author]").first().text();
        Author author = new Author(HandleString.makeSlug(authorName),authorName);
        String firstChapterURL = doc.select("ul[class=list-chapter] li").get(0).select("a").attr("href");
        String idFirstChapter = firstChapterURL.split("/")[firstChapterURL.split("/").length-1];
        String image = doc.selectFirst("img").attr("src");
        String description = doc.selectFirst("div[itemprop=description]").toString();
        Novel novel = new Novel(novelId,name,image,description,author,idFirstChapter);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setData(novel);
        dataResponse.setStatus("success");
        return dataResponse;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public DataResponse getDetailAuthor(String authorId) {
        String url =  SourceNovels.NOVEL_MAIN+ "tac-gia/"+authorId;
        Document doc = null;
        try {
            doc = ConnectJsoup.connect(url);
        Elements novels = doc.select("div[itemtype=https://schema.org/Book]");
        String nameAuthor = novels.get(0).selectFirst("span[class=author]").text();
        Author author = new Author(HandleString.makeSlug(nameAuthor),nameAuthor);
        List<Novel> novelList = new ArrayList<>();
        for(Element novel : novels)
        {
            String image = novel.selectFirst("div[data-image]").attr("data-image");
            String name = novel.selectFirst("h3").text();
            String link = novel.selectFirst("a").attr("href");
            String idNovel = getEndSlugFromUrl(link);
            doc = ConnectJsoup.connect(link);
            String firstChapterURL = doc.select("ul[class=list-chapter] li").get(0).select("a").attr("href");
            String idFirstChapter = firstChapterURL.split("/")[firstChapterURL.split("/").length-1];
            String description = doc.selectFirst("div[itemprop=description]").toString();
            Novel novelObj = new Novel(idNovel,name,image,description,author,idFirstChapter);
            novelList.add(novelObj);
        }
        DataResponse dataResponse = new DataResponse("success",1,1,novelList.size(),null,novelList,null);
        return dataResponse;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public DataResponse getAllNovels(int page, String search) {
        String url = HandleString.getValidURL(SourceNovels.FULL_NOVELS +search+ "&page="+page);
        List<Novel> novelList = new ArrayList<>();
        Document doc = null;
        try {
            doc = ConnectJsoup.connect(url);
            Elements novels = doc.select("div[itemtype=https://schema.org/Book]");
                for (int i = 0; i < novels.size()&&i<18; i++) {
                    Element novel = novels.get(i);
                    if (!novel.text().equals("")) {
                        String image = novel.selectFirst("div[data-image]").attr("data-image");
                        String novelUrl =novel.selectFirst("a").attr("href");
                        String idNovel = getEndSlugFromUrl(novelUrl);
                        String nameNovel = novel.selectFirst("h3").text();
                        String nameAuthor = novel.selectFirst("span[class=author]").text();
                        String urlDetail = SourceNovels.NOVEL_MAIN + idNovel;
                        doc = ConnectJsoup.connect(urlDetail);
                        Author author = new Author(HandleString.makeSlug(nameAuthor), nameAuthor);
                        if(novel.select("span[class=book-text]").size()==1) continue;
                        String idFirstChapter = "chuong-1";
                        String description = doc.selectFirst("div[itemprop=description]").toString();
                        Novel novelObj = new Novel(idNovel, nameNovel, image, description, author, idFirstChapter);
                        novelList.add(novelObj);
                    }
                }
            int totalPages = getNovelTotalPages(url);
            DataResponse dataResponse = new DataResponse("success", totalPages, page, novelList.size(), search, novelList, "");
            dataResponse.setCurrentPage(page);
            return dataResponse;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public DataResponse getNovelSearch(int page, String key, String orderBy) {
        String url = HandleString.getValidURL( SourceNovels.FULL_NOVELS +key+ "&page="+page);
        List<Novel> novelList = new ArrayList<>();
        Document doc = null;
        try {
            doc = ConnectJsoup.connect(url);
        Elements novels = doc.select("div[itemtype=https://schema.org/Book]");
        for(int i=0;i<novels.size()&&i<18;i++)
        {
            Element novel = novels.get(i);
            if(!novel.text().equals("")) {
                String image = novel.selectFirst("div[data-image]").attr("data-image");
                String novelUrl =novel.selectFirst("a").attr("href");
                String idNovel = getEndSlugFromUrl(novelUrl);
                String name = novel.selectFirst("h3").text();
                String nameAuthor = novel.selectFirst("span[class=author]").text();
                String urlDetail = SourceNovels.NOVEL_MAIN+idNovel;
                doc= ConnectJsoup.connect(urlDetail);
                Author author = new Author(HandleString.makeSlug(nameAuthor),nameAuthor);
                if(novel.select("span[class=book-text]").size()==1) continue;
                String idFirstChapter = "chuong-1";
                String description = doc.selectFirst("div[itemprop=description]").toString();
                Novel novelObj = new Novel(idNovel , name, image,description, author,idFirstChapter);
                novelList.add(novelObj);
            }
        }
        novelList.sort((Novel novel1,Novel novel2)->
        {
            return orderBy.equals("a-z")? novel1.getName().compareTo(novel2.getName()):novel2.getName().compareTo(novel1.getName());
        });
        int totalPages = getNovelTotalPages(url);
        DataResponse dataResponse = new DataResponse("success",totalPages,page,novelList.size(),key,novelList,"");
        dataResponse.setCurrentPage(page);
        return dataResponse;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
