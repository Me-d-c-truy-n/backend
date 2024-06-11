import com.crawldata.back_end.export_plugin_builder.ExportPluginFactory;
import com.crawldata.back_end.model.Chapter;
import com.crawldata.back_end.model.Novel;
import com.crawldata.back_end.plugin_builder.PluginFactory;
import com.crawldata.back_end.response.DataResponse;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.html.simpleparser.StyleSheet;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.jsoup.Jsoup;
import org.springframework.core.io.ClassPathResource;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PdfPlugin implements ExportPluginFactory {
    @Getter
    private PluginFactory pluginFactory;
    @Getter
    private Novel novel;
    private List<Chapter> chapterList =new ArrayList<>();
    private final int maxThreadNum = 3;
    @Getter
    private List<Chapter> listDetailChapter;
    private List<ReadDataThread> listThreads;



    @Override
    public void export(PluginFactory plugin, String novelId, String fromChapterId, int numChapters, HttpServletResponse response) throws IOException {
        getNovelInfo(plugin,novelId, fromChapterId, numChapters);
        listDetailChapter = new ArrayList<>();
        listThreads = new ArrayList<>();
        try {
            getDetailChapter();
            generatePdfAllChapter(response);
        }
        catch (Exception e)
        {
           e.printStackTrace();
        }
    }

    public void getDetailChapter() {
        int totalChapters = chapterList.size();
        int chaptersPerThread = 0;
        Integer remainingChapters = 0;

        if(totalChapters % maxThreadNum == 0)
        {
            chaptersPerThread = totalChapters/maxThreadNum;
        }
        else {
            chaptersPerThread = totalChapters / maxThreadNum ;
            remainingChapters = totalChapters - chaptersPerThread*maxThreadNum;

        }

        for(int i = 0 ; i < maxThreadNum ; i++)
        {
            List<Chapter> chapters = new ArrayList<>();
            if(i == maxThreadNum - 1 && totalChapters % maxThreadNum != 0){
                chapters = chapterList.subList(i * chaptersPerThread, i * chaptersPerThread +chaptersPerThread+ remainingChapters);
            }
            else {
                chapters = chapterList.subList(i * chaptersPerThread, i * chaptersPerThread + chaptersPerThread);
            }
            ReadDataThread thread = new ReadDataThread(chapters, this);
            thread.start();
            listThreads.add(thread);
        }

        try {
            joinThread();
        }catch (Exception e) {
            e.printStackTrace();
        }

        sortChaptersASCByName(listDetailChapter);
    }

    public Double getChapterNumber(String chapterName)
    {
        String[] components = chapterName.split(":");
        Double chapterNumber = Double.valueOf(components[0].split(" ")[1].trim());
        return  chapterNumber;
    }

    public void sortChaptersASCByName(List<Chapter> chapters)
    {
        Collections.sort(chapters, new Comparator<Chapter>() {
            @Override
            public int compare(Chapter o1, Chapter o2) {
                Double chapter1Number = getChapterNumber(o1.getName());
                Double chapter2Number = getChapterNumber(o2.getName());
                Double delta = chapter1Number - chapter2Number;
                if(delta == 0)
                {
                    return 0;
                }
                else if(delta <0)
                {
                    return -1;
                }
                else {
                    return 1;
                }
            }
        });
    }

    public void joinThread() throws InterruptedException {
        for(ReadDataThread thread : listThreads)
        {
            thread.join();
        }
    }

    public int getIndexFromChapterId(List<Chapter> chapters, String fromChapterId)
    {

        for(int i = 0 ; i < chapters.size() ; i++)
        {
            if(chapters.get(i).getChapterId().equals(fromChapterId))
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * Retrieves and sets the novel information and chapter list from the plugin.
     * @param plugin The plugin factory instance.
     * @param novelId The ID of the novel to retrieve information for.
     */
    private void getNovelInfo(PluginFactory plugin, String novelId, String fromChapterId, int numChapters) {
        pluginFactory = plugin;
        DataResponse dataResponse = pluginFactory.getNovelDetail(novelId);
        if(dataResponse != null && dataResponse.getStatus().equals("success")) {
            novel = (Novel) dataResponse.getData();
        }
        dataResponse = pluginFactory.getNovelListChapters(novel.getNovelId(), fromChapterId, numChapters);
        if (dataResponse != null && "success".equals(dataResponse.getStatus())) {
            Object data = dataResponse.getData();
            if (data instanceof List<?> dataList) {
                if (!dataList.isEmpty() && dataList.get(0) instanceof Chapter) {
                    List<Chapter> chapters = (List<Chapter>) dataList;
                    int index = getIndexFromChapterId(chapters, fromChapterId);
                    if(index != -1) {
                        int count = 0;
                        for(int i = index; i < chapters.size(); i++) {
                            if(count == chapters.size() || count == numChapters)
                            {
                                break;
                            }
                            chapterList.add(chapters.get(i));
                            count++;
                        }
                    }
                }
            }
        }
    }

    private void generatePdfAllChapter(HttpServletResponse response) throws DocumentException, IOException {
        // Create a ByteArrayOutputStream to hold the PDF data
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Create a new Document
        Document document = new Document(PageSize.A4);
        // Get a PdfWriter instance
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        // Open the document
        document.open();
        ClassPathResource fontResource = new ClassPathResource("fonts/ArialUnicodeMSRegular.ttf");
        FontFactory.register(fontResource.getURI().toString(), "CustomFont");
        BaseFont baseFont = BaseFont.createFont(fontResource.getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        // Title font
        Font titleFont = new Font(baseFont, 35, Font.BOLD);
        titleFont.setColor(Color.black);
        // Font for author
        Font authorFont = new Font(baseFont, 25, Font.NORMAL);
        authorFont.setColor(Color.black);
        // Normal font
        Font normalFont = new Font(baseFont, 12, Font.NORMAL);
        normalFont.setColor(Color.BLACK);
        // Chapter title font
        Font chapterTitleFont = new Font(baseFont, 28, Font.BOLD);
        chapterTitleFont.setColor(Color.black);

        // Add novel title
        Paragraph novelTitle = new Paragraph(novel.getName(), titleFont);
        novelTitle.setAlignment(Element.ALIGN_CENTER);  // Center align the title
        novelTitle.setSpacingAfter(20);
        document.add(novelTitle);

        // Add author
        Paragraph author = new Paragraph("Tác giả: " + novel.getAuthor().getName(), authorFont);
        author.setAlignment(Element.ALIGN_CENTER);  // Center align the title
        author.setSpacingAfter(30);
        document.add(author);

        /// Add Image novel
        Image coverImage = Image.getInstance(new URL(novel.getImage()));
        coverImage.setAlignment(Image.ALIGN_CENTER);
        coverImage.scaleToFit(400, 400);  // Scale image to fit within 400x400 pixels
        document.add(coverImage);

        // Create root outline
        PdfOutline rootOutline = writer.getRootOutline();

        for (Chapter detailChapter : listDetailChapter)
        {
                document.newPage();
                int pageNumber = writer.getPageNumber();

                // Create chapter title with an anchor
                Anchor anchor = new Anchor(detailChapter.getName(), chapterTitleFont);
                anchor.setName(detailChapter.getName());
                Paragraph chapterTitle = new Paragraph(anchor);
                chapterTitle.setAlignment(Element.ALIGN_CENTER);
                document.add(chapterTitle);
                document.add(new Paragraph("\n\n"));

                // Add bookmark for the chapter
                PdfDestination destination = new PdfDestination(PdfDestination.FITH);
                writer.getDirectContent().localDestination(detailChapter.getName(), destination);
                PdfOutline chapterOutline = new PdfOutline(rootOutline, PdfAction.gotoLocalPage(pageNumber, destination, writer),detailChapter.getName());

                // Add content
                // Create a stylesheet and add the custom font to it
                StyleSheet styles = new StyleSheet();
                styles.loadTagStyle("body", "face", "CustomFont");
                styles.loadTagStyle("body", "encoding", "Identity-H");
                styles.loadTagStyle("body", "size", "15pt");
                styles.loadTagStyle("p", "style", "text-align: justify;");

                // Parse the HTML content
                org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(detailChapter.getContent());
                String htmlContent = jsoupDoc.body().html();
                // Parse the HTML content and add it to the document
                List<Element> elements = HTMLWorker.parseToList(new StringReader(htmlContent), styles);
                for (Element element : elements) {
                    document.add(element);
                }
        }

        // Create filename
        String filename  = "";
        if(chapterList.size() == 0)
        {
             filename = novel.getName();
        }
        else if(chapterList.size() == 1)
        {
            filename = novel.getName() + "_" + chapterList.get(0).getChapterId();
        }
        else {
            filename = novel.getName() + "_" + chapterList.get(0).getChapterId() + "-" + chapterList.get(chapterList.size()-1).getChapterId();
        }

        document.close();
        // Set the content type and headers for the response
        response.setContentType("application/pdf");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment; filename=" + new String((filename + ".pdf").getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
        response.setContentLength(baos.size());
        // Write the PDF to the response output stream

        try (OutputStream os = response.getOutputStream()) {
            baos.writeTo(os);
            os.flush();
            os.close();
            baos.close();
        }
    }
}

class ReadDataThread extends Thread{
    private List<Chapter> listChapter;
    private PdfPlugin pdf;
    public ReadDataThread()
    {
    }
    public ReadDataThread(List<Chapter> listChapter, PdfPlugin pdf)
    {
        this.listChapter = listChapter;
        this.pdf = pdf;
    }

    @Override
    public void run() {
        PluginFactory plugin = pdf.getPluginFactory();

        for(Chapter chapter : listChapter)
        {
            Chapter contentChapter = plugin.getContentChapter(chapter.getNovelId(), chapter.getChapterId());
            pdf.getListDetailChapter().add(contentChapter);
        }
    }
}
