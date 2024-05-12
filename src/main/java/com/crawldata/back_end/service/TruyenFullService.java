package com.crawldata.back_end.service;

import com.crawldata.back_end.dto.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TruyenFullService {


    //get information of a comic full chapters
    public int getEndPage(String url) throws IOException {
        Document doc = Jsoup.connect(url).timeout(5000).get();
        Elements pages = doc.select("ul[class=pagination pagination-sm] li");
        int endPage = 0;
        try {
            int parsedValue = Integer.parseInt(pages.get(pages.size()-1).text().split(" ")[0]);
            endPage = parsedValue;
        } catch (NumberFormatException e) {
            int parsedValue = Integer.parseInt(pages.get(pages.size() - 2).text().split(" ")[0]);
            endPage = parsedValue;
        }
        return endPage;
    }
    public  DetailComic getAComic(String url) throws IOException {
        Document doc = Jsoup.connect(url).timeout(5000).get();
        //name
        Element name = doc.select("h3[class=title]").first();
        //author
        Element author = doc.select("a[itemprop=author]").first();
        //category - not
        Elements category= doc.select("a[itemprop=genre]");
        //status
        Element status= doc.select("span[class=text-primary]").first();
        if(status ==null)
        {
            status = doc.select("span[class=text-success]").first();
        }
        if(status ==null)
        {
            status = doc.select("span[class=text-warning]").first();
        }
        //chapter
        Elements pages = doc.select("ul[class=pagination pagination-sm] li");
        StringBuilder linkEndPage = new StringBuilder();
        //image
        String coverImage = doc.selectFirst("img").attr("src");
        for(Element page: pages)
        {
            if(page.text().equals("Cuối »"))
            {
                linkEndPage.append(page.select("a").attr("href"));
            }
        }
        int endPage = getEndPage(linkEndPage.toString());
        List<Chapter> chapterList = new ArrayList<>();
        Elements chapters = doc.select("ul[class=list-chapter] li");
        for (Element chapter : chapters) {
            Element linkElement = chapter.selectFirst("a");
            String topic = linkElement.text();
            String link = linkElement.attr("href");
//            Chapter chapterObj = new Chapter(topic,link);
//            chapterList.add(chapterObj);
        }
        return new DetailComic(name.text(),author.text(),category.text(),status.text(),coverImage,chapterList,1,endPage);
    }
    //get end page

    //get all link comic of category
    public Category getComicsCategory(String url) throws IOException {
        Document doc = Jsoup.connect(url).timeout(5000).get();
        Elements pages = doc.select("ul[class=pagination pagination-sm] li");
        Element nameCategory = doc.selectFirst("h2");
        String lin = pages.get(pages.size()-2).selectFirst("a").attr("href");
        int endPage =getEndPage(lin);
        List<ComicCategory> comicList = new ArrayList<>();
        Document doc2 = Jsoup.connect(url).timeout(5000).get();
        Elements books = doc2.select("div[itemtype=https://schema.org/Book]");
        for (Element book : books) {
            Element titleElement = book.select("a").first();
            if(titleElement!=null) {
                String name = titleElement.text();
                String link = titleElement.attr("href");
                Element authorElement = book.selectFirst("span[class=author]");
                String author = authorElement.text();
                Element coverElement = book.selectFirst("div[data-image]");
                String coverImage = coverElement.attr("data-image");
                ComicCategory bookObj = new ComicCategory(name, coverImage, author, link);
                comicList.add(bookObj);
            }
        }
        return new Category(nameCategory.text(),comicList,1,endPage);
    }

    // new version
    //get total chapters
    public Integer getTotalChapters(String url) throws IOException {
        Document doc = Jsoup.connect(url).timeout(5000).get();
        Elements pages = doc.select("ul[class=pagination pagination-sm] li");
        Integer totalChapters =0 ;
        if(pages.size()==0)
        {
            totalChapters = doc.select("ul[class=list-chapter] li").size();
        }
        else {
            StringBuilder linkEndPage = new StringBuilder();
            Element page = pages.get(pages.size()-2);
            if(page.text().equals("Cuối »"))
            {
                linkEndPage.append(page.select("a").attr("href"));
                Document docPage = Jsoup.connect(linkEndPage.toString()).timeout(5000).get();
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
            else
            {
                totalChapters =Integer.valueOf( page.text());
            }
        }
        return totalChapters;
    }
    //get detail chapter
   public ChapterDetail getDetailChapter(String idNovel, String idChapter) throws IOException {
        String urlChapter=String.format("https://truyenfull.vn/%s/%s/",idNovel,idChapter);
        String urlAuthor = "https://truyenfull.vn/"+idNovel;
        Document doc = Jsoup.connect(urlChapter).timeout(5000).get();
        Document docB = Jsoup.connect(urlAuthor).timeout(5000).get();
        String novelID = idNovel;
        String chapterId = idChapter;
        //Name author
        String nameAuthor =docB.select("a[itemprop=author]").text();
        //Create author
        Author author = new Author("hi",nameAuthor);
        author.setIdSlug(nameAuthor);
        //Create detail chapters
        String novelName = doc.select("a[class=truyen-title]").first().text();
        //get chapter
        String chapterName = doc.select("a[class=chapter-title]").first().text();
        //get content of a chapter
        Elements content = doc.select("div#chapter-c");
        Integer totalChapters = getTotalChapters(urlAuthor);
        ChapterDetail chapterDetail = new ChapterDetail(novelID,novelName,chapterId,chapterName,totalChapters,author,content.toString());
        return chapterDetail;
    }

    // get list chapters of novel
    public List<Chapter> getAllChapters(String idNovel) throws IOException {
        String url = "https://truyenfull.vn/"+idNovel;
        Document doc = Jsoup.connect(url).timeout(5000).get();
        String name = doc.select("h3[class=title]").first().text();
        //author
        String authorName = doc.select("a[itemprop=author]").first().text();
        //Create author
        Author author = new Author("hi",authorName);
        author.setIdSlug(authorName);
        //chapter
        Elements pages = doc.select("ul[class=pagination pagination-sm] li");
        int totalPages =0 ;
        if (pages.size()!=0){
            StringBuilder linkEndPage = new StringBuilder();
            Element page = pages.get(pages.size()-2);
            if(page.text().equals("Cuối »"))
            {
                linkEndPage.append(page.select("a").attr("href"));
                Document docPage= Jsoup.connect(linkEndPage.toString()).timeout(5000).get();
                Elements allPage = docPage.select("ul[class=pagination pagination-sm] li");
                totalPages =  Integer.parseInt(allPage.get(allPage.size()-2).text().split(" ")[0]);
            }
            else
            {
                totalPages =Integer.parseInt( page.text());
            }
        }
        List<Chapter> chapterList = new ArrayList<>();
        Integer totalChapters = getTotalChapters(url);
        for(int i=1;i<totalPages;i++)
        {
            String link = String.format("https://truyenfull.vn/%s/trang-%d",idNovel,i);
            Document docChap= Jsoup.connect(link).timeout(5000).get();
            Elements chapters = doc.select("ul[class=list-chapter] li");
            int move =1 ;
            for (Element chapter : chapters) {
                Element linkElement = chapter.selectFirst("a");
                String nameChapter = linkElement.text();
                Chapter chapterObj = new Chapter(idNovel,name,"chuong-"+(move++),nameChapter,totalChapters,author);
               chapterList.add(chapterObj);
            }
        }
        return chapterList;
    }
}
