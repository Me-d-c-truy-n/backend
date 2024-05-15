package com.crawldata.back_end.plugin_builder.truyenfull;

import com.crawldata.back_end.model.*;
import com.crawldata.back_end.plugin_builder.PluginTemplate;
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

public class TruyenFullPlugin implements PluginTemplate {
    public int getEndPage(String url) throws IOException {
        Document doc = ConnectJsoup.connect(url);
        Elements pages = doc.select("ul[class=pagination pagination-sm] li");
        int totalPages = 1 ;
        if (pages.size()!=0){
            StringBuilder linkEndPage = new StringBuilder();

            Element page = pages.get(pages.size()-2);
            if(page.text().equals("Cuối »"))
            {
                linkEndPage.append(page.select("a").attr("href"));
                String linkValid = HandleString.getValidURL(linkEndPage.toString());
                Document docPage= ConnectJsoup.connect(linkValid);
                Elements allPage = docPage.select("ul[class=pagination pagination-sm] li");
                totalPages =  Integer.parseInt(allPage.get(allPage.size()-2).text().split(" ")[0]);
            }
            else if(page.text().equals("Trang tiếp"))
            {
                Element pageNext = pages.get(pages.size()-1);
                linkEndPage.append(pageNext.select("a").attr("href"));
                Document docPage= ConnectJsoup.connect(linkEndPage.toString());
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

    public Integer  getTotalChapters(String url) throws IOException {
        Document doc = ConnectJsoup.connect(url);
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
                Document docPage = ConnectJsoup.connect(linkEndPage.toString());
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
                Document docPage = ConnectJsoup.connect(linkEndPage.toString());
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
                Document docPage = ConnectJsoup.connect(linkEndPage.toString());
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

    public ChapterDetail getDetailChapter(String idNovel, String idChapter) throws IOException {
        String urlChapter=String.format("https://truyenfull.vn/%s/chuong-%s/",idNovel,idChapter);
        String urlAuthor = "https://truyenfull.vn/"+idNovel;
        Document doc = ConnectJsoup.connect(urlChapter);
        Document docB = ConnectJsoup.connect(urlAuthor);
        String novelID = idNovel;
        String chapterId = idChapter;
        String nameAuthor =docB.select("a[itemprop=author]").text();
        Author author = new Author(HandleString.makeSlug(nameAuthor),nameAuthor);
        String novelName = doc.select("a[class=truyen-title]").first().text();
        String chapterName = doc.select("a[class=chapter-title]").first().text();
        Elements content = doc.select("div#chapter-c");
        Integer totalChapters = getTotalChapters(urlAuthor);
        ChapterDetail chapterDetail = new ChapterDetail(novelID,novelName,chapterId,chapterName,totalChapters,author,content.toString());
        return chapterDetail;
    }


    public DataResponse getAllChapters(String idNovel, int page) throws IOException {
        String url = "https://truyenfull.vn/"+idNovel;
        Document doc = ConnectJsoup.connect(url);
        String name = doc.select("h3[class=title]").first().text();
        String authorName = doc.select("a[itemprop=author]").first().text();
        Author author = new Author(HandleString.makeSlug(authorName),authorName);
        List<Chapter> chapterList = new ArrayList<>();
        Integer totalChapters = getTotalChapters(url);
        Integer totalPages = getEndPage(url);
        String link = String.format("https://truyenfull.vn/%s/trang-%d",idNovel,page);
        Document docChap= ConnectJsoup.connect(link);
        Elements chapters = docChap.select("ul[class=list-chapter] li");
        for (Element chapter : chapters) {
            Element linkElement = chapter.selectFirst("a");
            String nameChapter = linkElement.text();
            String idChapter = nameChapter.split(" ")[1].split(":")[0];
            Chapter chapterObj = new Chapter(idNovel,name,idChapter,nameChapter,totalChapters,author);
            chapterList.add(chapterObj);
        }
        DataResponse dataResponse = new DataResponse();
        dataResponse.setData(chapterList);
        dataResponse.setTotalPage(totalPages);
        dataResponse.setPerPage(chapterList.size());
        return dataResponse;
    }


    public NovelDetail getDetailNovel(String idNovel) throws IOException {
        String url = "https://truyenfull.vn/"+idNovel;
        Document doc = ConnectJsoup.connect(url);
        String name = doc.select("h3[class=title]").first().text();
        String authorName = doc.select("a[itemprop=author]").first().text();
        Author author = new Author(HandleString.makeSlug(authorName),authorName);
        int totalChapter= getTotalChapters(url);
        String image = doc.selectFirst("img").attr("src");
        String description = doc.selectFirst("div[itemprop=description]").toString();
        return new NovelDetail(idNovel,name,image,description,totalChapter,author);
    }


    public List<Novel> getNovelsAuthor(String idAuthor) throws IOException {
        String url = "https://truyenfull.vn/tac-gia/"+idAuthor;
        Document doc = ConnectJsoup.connect(url);
        Elements novels = doc.select("div[itemtype=https://schema.org/Book]");
        String nameAuthor = novels.get(0).selectFirst("span[class=author]").text();
        Author author = new Author(HandleString.makeSlug(nameAuthor),nameAuthor);
        List<Novel> novelList = new ArrayList<>();
        for(Element novel : novels)
        {
            String image = novel.selectFirst("div[data-image]").attr("data-image");
            String name = novel.selectFirst("h3").text();
            String link = novel.selectFirst("a").attr("href");
            int totalChapter = getTotalChapters(link);
            Novel novelObj = new Novel(HandleString.makeSlug(name),name,image,"",totalChapter,author);
            novelList.add(novelObj);
        }
        return novelList;
    }

    public List<Novel> getAllNovels(int page,String search) throws IOException {
        String url = SourceNovels.FULL_NOVELS +search+ "&page="+page;
        List<Novel> novelList = new ArrayList<>();
        Document doc = ConnectJsoup.connect(url);
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
                String urlDetail = "https://truyenfull.vn/"+HandleString.makeSlug(name);
                Document docDetail = ConnectJsoup.connect(urlDetail);
                Author author = new Author(HandleString.makeSlug(nameAuthor),nameAuthor);
                String description = docDetail.selectFirst("div[itemprop=description]").toString();
                Novel novelObj = new Novel(HandleString.makeSlug(name), name, image, description,totalChapter, author);
                novelList.add(novelObj);
            }
        }
        return novelList;
    }
}
