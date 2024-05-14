package com.crawldata.back_end.service;
import com.crawldata.back_end.dto.*;
import com.crawldata.back_end.response.DataResponse;
import com.crawldata.back_end.utils.HandleString;
import com.crawldata.back_end.utils.SourceNovels;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TruyenFullService {


    //get information of a comic full chapters
    public int getEndPage(String url) throws IOException {
        Document doc = Jsoup.connect(url).timeout(10*1000).get();
        Elements pages = doc.select("ul[class=pagination pagination-sm] li");
        int totalPages = 1 ;
        if (pages.size()!=0){
            StringBuilder linkEndPage = new StringBuilder();

            Element page = pages.get(pages.size()-2);
            if(page.text().equals("Cuối »"))
            {
                linkEndPage.append(page.select("a").attr("href"));
                String linkValid = HandleString.getValidURL(linkEndPage.toString());
                Document docPage= Jsoup.connect(linkValid).timeout(10*1000).get();
                Elements allPage = docPage.select("ul[class=pagination pagination-sm] li");
                totalPages =  Integer.parseInt(allPage.get(allPage.size()-2).text().split(" ")[0]);
            }
            else if(page.text().equals("Trang tiếp"))
            {
                Element pageNext = pages.get(pages.size()-1);
                linkEndPage.append(pageNext.select("a").attr("href"));
                Document docPage= Jsoup.connect(linkEndPage.toString()).timeout(10*1000).get();
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

    //get total chapters
    public Integer  getTotalChapters(String url) throws IOException {
        Document doc = Jsoup.connect(url).timeout(0).get();
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
                Document docPage = Jsoup.connect(linkEndPage.toString()).timeout(10*1000).get();
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
            else if(page.text().equals("Trang tiếp"))
            {
                Element pageNext = pages.get(pages.size()-1);
                linkEndPage.append(pageNext.select("a").attr("href"));
                Document docPage = Jsoup.connect(linkEndPage.toString()).timeout(10*1000).get();
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
                linkEndPage.append(page.select("a").attr("href"));
                Document docPage = Jsoup.connect(linkEndPage.toString()).timeout(10*1000).get();
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
        }
        return totalChapters;
    }
    //get detail chapter
   public ChapterDetail getDetailChapter(String idNovel, String idChapter) throws IOException {
        String urlChapter=String.format("https://truyenfull.vn/%s/chuong-%s/",idNovel,idChapter);
        String urlAuthor = "https://truyenfull.vn/"+idNovel;
        Document doc = Jsoup.connect(urlChapter).timeout(10*1000).get();
        Document docB = Jsoup.connect(urlAuthor).timeout(10*1000).get();
        String novelID = idNovel;
        String chapterId = idChapter;
        //Name author
        String nameAuthor =docB.select("a[itemprop=author]").text();
        //Create author
        Author author = new Author(HandleString.makeSlug(nameAuthor),nameAuthor);
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
    public DataResponse getAllChapters(String idNovel, int page) throws IOException {
        String url = "https://truyenfull.vn/"+idNovel;
        Document doc = Jsoup.connect(url).timeout(10*1000).get();
        String name = doc.select("h3[class=title]").first().text();
        //author
        String authorName = doc.select("a[itemprop=author]").first().text();
        //Create author
        Author author = new Author(HandleString.makeSlug(authorName),authorName);
        //chapter
        List<Chapter> chapterList = new ArrayList<>();
        Integer totalChapters = getTotalChapters(url);
        Integer totalPages = getEndPage(url);
        String link = String.format("https://truyenfull.vn/%s/trang-%d",idNovel,page);
        Document docChap= Jsoup.connect(link).timeout(10*1000).get();
        Elements chapters = docChap.select("ul[class=list-chapter] li");
        int move =1 ;
        for (Element chapter : chapters) {
            Element linkElement = chapter.selectFirst("a");
            String nameChapter = linkElement.text();
            Chapter chapterObj = new Chapter(idNovel,name,"chuong-"+(move++),nameChapter,totalChapters,author);
            chapterList.add(chapterObj);
        }
        DataResponse dataResponse = new DataResponse();
        dataResponse.setData(chapterList);
        dataResponse.setTotalPage(totalPages);
        dataResponse.setPerPage(chapterList.size());
        return dataResponse;
    }

    //get detail novel
    public NovelDetail getDetailNovel(String idNovel) throws IOException {
        String url = "https://truyenfull.vn/"+idNovel;
        Document doc = Jsoup.connect(url).timeout(10*1000).get();
        String name = doc.select("h3[class=title]").first().text();
        //author
        String authorName = doc.select("a[itemprop=author]").first().text();
        //Create author
        Author author = new Author(HandleString.makeSlug(authorName),authorName);
        //chapter
        int totalChapter= getTotalChapters(url);
        //image
        String image = doc.selectFirst("img").attr("src");
        //description
        String description = doc.selectFirst("div[itemprop=description]").toString();
        return new NovelDetail(idNovel,name,image,description,totalChapter,author);
    }

    //get list novel of an author base on id
    public List<Novel> getNovelsAuthor(String idAuthor) throws IOException {
        String url = "https://truyenfull.vn/tac-gia/"+idAuthor;
        Document doc = Jsoup.connect(url).timeout(10*1000).get();
        Elements novels = doc.select("div[itemtype=https://schema.org/Book]");
        String nameAuthor = novels.get(0).selectFirst("span[class=author]").text();
        //Create author
        Author author = new Author(HandleString.makeSlug(nameAuthor),nameAuthor);
        List<Novel> novelList = new ArrayList<>();
        for(Element novel : novels)
        {
            String image = novel.selectFirst("div[data-image]").attr("data-image");
            String name = novel.selectFirst("h3").text();
            String link = novel.selectFirst("a").attr("href");
            int totalChapter = getTotalChapters(link);
            Novel novelObj = new Novel(HandleString.makeSlug(name),name,image,totalChapter,author);
            novelList.add(novelObj);
        }
        return novelList;
    }

    //get all novels
    public List<Novel> getAllNovels(int page,String search) throws IOException {
        String url = SourceNovels.fullNovels +search+ "&page="+page;
        List<Novel> novelList = new ArrayList<>();
        Document doc = Jsoup.connect(url).timeout(10*1000).get();
        Elements novels = doc.select("div[itemtype=https://schema.org/Book]");
        for(Element novel : novels)
        {
            if(!novel.text().equals("")) {
                String image = novel.selectFirst("div[data-image]").attr("data-image");
                String name = novel.selectFirst("h3").text();
                String link = novel.selectFirst("a").attr("href");
                link = link.replaceAll("\\s", "");
                int totalChapter = getTotalChapters(link);
                String nameAuthor = novel.selectFirst("span[class=author]").text();
                //Create author
                Author author = new Author(HandleString.makeSlug(nameAuthor),nameAuthor);
                Novel novelObj = new Novel(HandleString.makeSlug(name), name, image, totalChapter, author);
                novelList.add(novelObj);
            }
        }
        return novelList;
    }
}
