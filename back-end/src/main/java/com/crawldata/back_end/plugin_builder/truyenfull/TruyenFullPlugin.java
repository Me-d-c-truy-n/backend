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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TruyenFullPlugin implements PluginFactory {

    /**
     *Support function
     */
    public Integer getNovelTotalChapters(String url) {
        Document doc = null;
        try {
            doc = ConnectJsoup.connect(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Elements pages = doc.select("ul[class=pagination pagination-sm] li");
        Integer totalChapters =0 ;
        if(pages.size()==0)
        {
            totalChapters = doc.select("ul[class=list-chapter] li").size();
        }
        else
        {
            StringBuilder linkEndPage = new StringBuilder();
            Element page = pages.get(pages.size()-2);
            Document docPage = null;
            switch(page.text())
            {
                case "Trang tiếp":
                    Element pageNext = pages.get(pages.size()-1);
                    linkEndPage.append(pageNext.select("a").attr("href"));
                    break;
                default:
                    linkEndPage.append(page.select("a").attr("href"));

            }
            try {
                docPage = ConnectJsoup.connect(linkEndPage.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Elements pageEnd = docPage.select("ul[class=list-chapter] li");
            String endPage = pageEnd.get(pageEnd.size()-1).text();
            Pattern pattern = Pattern.compile("\\d+");
            totalChapters = Integer.valueOf(0);
            // Match the pattern against the input string
            Matcher matcher = pattern.matcher(endPage);
            // Check if a match is found
            if (matcher.find()) {
                // Extract the matched numeric part
                String chapterNumber = matcher.group();
                totalChapters = Integer.valueOf(chapterNumber);
            } else {
                System.out.println("No chapter number found");
            }
        }
        return totalChapters;
    }
    public int getNovelTotalPages(String url) {
        Document doc = null;
        try {
            doc = ConnectJsoup.connect(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Elements pages = doc.select("ul[class=pagination pagination-sm] li");
        int totalPages = 1 ;
        if (pages.size()!=0){
            StringBuilder linkEndPage = new StringBuilder();
            Element page = pages.get(pages.size()-2);
            if(page.text().equals("Cuối »"))
            {
                linkEndPage.append(page.select("a").attr("href"));
                String linkValid = HandleString.getValidURL(linkEndPage.toString());
                Document docPage= null;
                try {
                    docPage = ConnectJsoup.connect(linkValid);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Elements allPage = docPage.select("ul[class=pagination pagination-sm] li");
                totalPages =  Integer.parseInt(allPage.get(allPage.size()-2).text().split(" ")[0]);
            }
            else if(page.text().equals("Trang tiếp"))
            {
                Element pageNext = pages.get(pages.size()-1);
                linkEndPage.append(pageNext.select("a").attr("href"));
                Document docPage= null;
                try {
                    docPage = ConnectJsoup.connect(linkEndPage.toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Elements allPage = docPage.select("ul[class=pagination pagination-sm] li");
                totalPages =  Integer.parseInt(allPage.get(allPage.size()-1).text().split(" ")[0]);
            }
            else
            {
                totalPages =Integer.parseInt( page.text());
            }
        }
        return totalPages;
    }
    @Override
    public DataResponse getNovelChapterDetail(String novelId, String chapterId) {
        String urlChapter = SourceNovels.NOVEL_MAIN+novelId+"/"+chapterId;
        String urlAuthor = SourceNovels.NOVEL_MAIN+novelId;
        Document doc = null;
        try {
            doc = ConnectJsoup.connect(urlChapter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Document docB = null;
        try {
            docB = ConnectJsoup.connect(urlAuthor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String nameAuthor = docB.select("a[itemprop=author]").text();
        Author author = new Author(HandleString.makeSlug(nameAuthor),nameAuthor);
        String novelName = doc.select("a[class=truyen-title]").first().text();
        String chapterName = doc.select("a[class=chapter-title]").first().text();
        Elements content = doc.select("div#chapter-c");
        String nextChapterURL = doc.select("a[id=next_chap]").attr("href");
        String idNextChapter = nextChapterURL.split("/").length!=1? nextChapterURL.split("/")[nextChapterURL.split("/").length-1]:"end";
        String preChapterURL = doc.select("a[id=prev_chap]").attr("href");
        String idPreChapter = preChapterURL.split("/").length != 1? preChapterURL.split("/")[preChapterURL.split("/").length-1]:"end";
        Chapter chapterDetail = new Chapter(novelId,novelName,chapterId,idNextChapter,idPreChapter,chapterName,author,content.toString());
        DataResponse dataResponse = new DataResponse();
        dataResponse.setStatus("success");
        dataResponse.setData(chapterDetail);
        return dataResponse;
    }
    @Override
    public DataResponse getNovelListChapters(String novelId, int page) {
        return null;
    }
    @Override
    public DataResponse getNovelDetail(String novelId) {
        String url = SourceNovels.NOVEL_MAIN+novelId;
        Document doc = null;
        try {
            doc = ConnectJsoup.connect(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
    }
    @Override
    public DataResponse getDetailAuthor(String authorId) {
        return null;
    }
    @Override
    public DataResponse getAllNovels(int page, String search) {
        String url = SourceNovels.FULL_NOVELS +search+ "&page="+page;
        List<Novel> novelList = new ArrayList<>();
        Document doc = null;
        try {
            doc = ConnectJsoup.connect(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Elements novels = doc.select("div[itemtype=https://schema.org/Book]");
        for(int i=0;i<novels.size()-12;i++)
        {
            Element novel = novels.get(i);
            if(!novel.text().equals("")) {
                String image = novel.selectFirst("div[data-image]").attr("data-image");
                String name = novel.selectFirst("h3").text();
                String nameAuthor = novel.selectFirst("span[class=author]").text();
                String urlDetail = "https://truyenfull.vn/"+HandleString.makeSlug(name);
                Document docDetail = null;
                try {
                    docDetail = ConnectJsoup.connect(urlDetail);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Author author = new Author(HandleString.makeSlug(nameAuthor),nameAuthor);
                String firstChapterURL = docDetail.select("ul[class=list-chapter] li").get(0).select("a").attr("href");
                String idFirstChapter = firstChapterURL.split("/")[firstChapterURL.split("/").length-1];
                String description = docDetail.selectFirst("div[itemprop=description]").toString();
                Novel novelObj = new Novel(HandleString.makeSlug(name), name, image,description, author,idFirstChapter);
                novelList.add(novelObj);
            }
        }
        int totalPages = getNovelTotalPages(url);
        DataResponse dataResponse = new DataResponse("success",totalPages,page,novelList.size(),search,novelList,"");
        dataResponse.setCurrentPage(page);
        return dataResponse;
    }
    @Override
    public DataResponse getNovelSearch(int page, String key, String orderBy) {
        return null;
    }
    /*@Override
    public int getNovelTotalPages(String url) {
        Document doc = null;
        try {
            doc = ConnectJsoup.connect(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Elements pages = doc.select("ul[class=pagination pagination-sm] li");
        int totalPages = 1 ;
        if (pages.size()!=0){
            StringBuilder linkEndPage = new StringBuilder();
            Element page = pages.get(pages.size()-2);
            if(page.text().equals("Cuối »"))
            {
                linkEndPage.append(page.select("a").attr("href"));
                String linkValid = HandleString.getValidURL(linkEndPage.toString());
                Document docPage= null;
                try {
                    docPage = ConnectJsoup.connect(linkValid);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Elements allPage = docPage.select("ul[class=pagination pagination-sm] li");
                totalPages =  Integer.parseInt(allPage.get(allPage.size()-2).text().split(" ")[0]);
            }
            else if(page.text().equals("Trang tiếp"))
            {
                Element pageNext = pages.get(pages.size()-1);
                linkEndPage.append(pageNext.select("a").attr("href"));
                Document docPage= null;
                try {
                    docPage = ConnectJsoup.connect(linkEndPage.toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Elements allPage = docPage.select("ul[class=pagination pagination-sm] li");
                totalPages =  Integer.parseInt(allPage.get(allPage.size()-1).text().split(" ")[0]);
            }
            else
            {
                totalPages =Integer.parseInt( page.text());
            }
        }
        return totalPages;
    }

    @Override
    public Integer getNovelTotalChapters(String url) {
        Document doc = null;
        try {
            doc = ConnectJsoup.connect(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Elements pages = doc.select("ul[class=pagination pagination-sm] li");
        Integer totalChapters =0 ;
        if(pages.size()==0)
        {
            totalChapters = doc.select("ul[class=list-chapter] li").size();
        }
        else
        {
            StringBuilder linkEndPage = new StringBuilder();
            Element page = pages.get(pages.size()-2);
            Document docPage = null;
            switch(page.text())
            {
                case "Trang tiếp":
                    Element pageNext = pages.get(pages.size()-1);
                    linkEndPage.append(pageNext.select("a").attr("href"));
                    break;
                default:
                    linkEndPage.append(page.select("a").attr("href"));

            }
            try {
                docPage = ConnectJsoup.connect(linkEndPage.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Elements pageEnd = docPage.select("ul[class=list-chapter] li");
            String endPage = pageEnd.get(pageEnd.size()-1).text();
            Pattern pattern = Pattern.compile("\\d+");
            totalChapters = Integer.valueOf(0);
            // Match the pattern against the input string
            Matcher matcher = pattern.matcher(endPage);
            // Check if a match is found
            if (matcher.find()) {
                // Extract the matched numeric part
                String chapterNumber = matcher.group();
                totalChapters = Integer.valueOf(chapterNumber);
            } else {
                System.out.println("No chapter number found");
            }
        }
        return totalChapters;
    }

    @Override
    public Chapter getNovelChapterDetail(String novelId, String chapterId) {
        String urlChapter = String.format("https://truyenfull.vn/%s/chuong-%s/",novelId,chapterId);
        String urlAuthor = "https://truyenfull.vn/"+novelId;
        Document doc = null;
        try {
            doc = ConnectJsoup.connect(urlChapter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Document docB = null;
        try {
            docB = ConnectJsoup.connect(urlAuthor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String nameAuthor = docB.select("a[itemprop=author]").text();
        Author author = new Author(HandleString.makeSlug(nameAuthor),nameAuthor);
        String novelName = doc.select("a[class=truyen-title]").first().text();
        String chapterName = doc.select("a[class=chapter-title]").first().text();
        Elements content = doc.select("div#chapter-c");
        Integer totalChapters = getNovelTotalChapters(urlAuthor);
        Chapter chapterDetail = new Chapter(novelId,novelName,chapterId,chapterName,totalChapters,author,content.toString());
        return chapterDetail;
    }

    @Override
    public DataResponse getNovelListChapters(String novelId, int page) {
        String url = "https://truyenfull.vn/" + novelId;
        Document doc = null;
        try {
            doc = ConnectJsoup.connect(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String name = doc.select("h3[class=title]").first().text();
        String authorName = doc.select("a[itemprop=author]").first().text();
        Author author = new Author(HandleString.makeSlug(authorName),authorName);
        List<Chapter> chapterList = new ArrayList<>();
        Integer totalChapters = getNovelTotalChapters(url);
        Integer totalPages = getNovelTotalPages(url);
        String link = String.format("https://truyenfull.vn/%s/trang-%d",novelId,page);
        Document docChap= null;
        try {
            docChap = ConnectJsoup.connect(link);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Elements chapters = docChap.select("ul[class=list-chapter] li");
        for (Element chapter : chapters) {
            Element linkElement = chapter.selectFirst("a");
            String nameChapter = linkElement.text();
            String chapterId = nameChapter.split(" ")[1].split(":")[0];
            Chapter chapterObj = new Chapter(novelId,name,chapterId,nameChapter,totalChapters,author, "");
            chapterList.add(chapterObj);
        }
        DataResponse dataResponse = new DataResponse();
        dataResponse.setData(chapterList);
        dataResponse.setTotalPage(totalPages);
        dataResponse.setPerPage(chapterList.size());
        return dataResponse;
    }

    @Override
    public Novel getNovelDetail(String novelId) {
        String url = "https://truyenfull.vn/"+novelId;
        Document doc = null;
        try {
            doc = ConnectJsoup.connect(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String name = doc.select("h3[class=title]").first().text();
        String authorName = doc.select("a[itemprop=author]").first().text();
        Author author = new Author(HandleString.makeSlug(authorName),authorName);
        int totalChapter= getNovelTotalChapters(url);
        String image = doc.selectFirst("img").attr("src");
        String description = doc.selectFirst("div[itemprop=description]").toString();
        return new Novel(novelId,name,image,description,totalChapter,author);
    }

    @Override
    public List<Novel> getAuthorNovels(String authorId) {
        String url = "https://truyenfull.vn/tac-gia/"+authorId;
        Document doc = null;
        try {
            doc = ConnectJsoup.connect(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Elements novels = doc.select("div[itemtype=https://schema.org/Book]");
        String nameAuthor = novels.get(0).selectFirst("span[class=author]").text();
        Author author = new Author(HandleString.makeSlug(nameAuthor),nameAuthor);
        List<Novel> novelList = new ArrayList<>();
        for(Element novel : novels)
        {
            String image = novel.selectFirst("div[data-image]").attr("data-image");
            String name = novel.selectFirst("h3").text();
            String link = novel.selectFirst("a").attr("href");
            int totalChapter = getNovelTotalChapters(link);
            Novel novelObj = new Novel(HandleString.makeSlug(name),name,image,"",totalChapter,author);
            novelList.add(novelObj);
        }
        return novelList;
    }

    @Override
    public List<Novel> getAllNovels(int page, String search) {
        String url = SourceNovels.FULL_NOVELS +search+ "&page="+page;
        List<Novel> novelList = new ArrayList<>();
        Document doc = null;
        try {
            doc = ConnectJsoup.connect(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Elements novels = doc.select("div[itemtype=https://schema.org/Book]");
        for(Element novel : novels)
        {
            if(!novel.text().equals("")) {
                String image = novel.selectFirst("div[data-image]").attr("data-image");
                String name = novel.selectFirst("h3").text();
                String[] textChapter = novel.select("a").text().split(" ");
                int totalChapter = Integer.parseInt(textChapter[textChapter.length-1]);
                String nameAuthor = novel.selectFirst("span[class=author]").text();
                String urlDetail = "https://truyenfull.vn/"+HandleString.makeSlug(name);
//                Document docDetail = null;
//                try {
//                    docDetail = ConnectJsoup.connect(urlDetail);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
                Author author = new Author(HandleString.makeSlug(nameAuthor),nameAuthor);
                //String description = docDetail.selectFirst("div[itemprop=description]").toString();
                Novel novelObj = new Novel(HandleString.makeSlug(name), name, image,"",totalChapter, author);
                novelList.add(novelObj);
            }
        }
        return novelList;
    }*/


}
