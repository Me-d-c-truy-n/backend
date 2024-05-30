
import com.crawldata.back_end.export_plugin_builder.ExportPluginFactory;
import com.crawldata.back_end.model.Chapter;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.html.simpleparser.StyleSheet;
import jakarta.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.springframework.core.io.ClassPathResource;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.List;


public class PdfPlugin implements ExportPluginFactory {
    @Override
    public void export(Chapter chapter, HttpServletResponse response)  {
        response.setContentType("application/pdf");
        try {
            generatePdf(chapter,response);
        }
        catch (Exception e) {
             response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void generatePdf(Chapter chapter, HttpServletResponse response) throws DocumentException, IOException {
        // Create a ByteArrayOutputStream to hold the PDF data
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Create a new Document
        Document document = new Document(PageSize.A4);
        // Get a PdfWriter instance
        PdfWriter.getInstance(document, baos);
        // Open the document
        document.open();

        ClassPathResource fontResource = new ClassPathResource("fonts/ArialUnicodeMSRegular.ttf");
        FontFactory.register(fontResource.getURI().toString(), "CustomFont");

        BaseFont baseFont = BaseFont.createFont(fontResource.getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font = new Font(baseFont, 25, Font.NORMAL);
        font.setColor(Color.black);

        // Add novel title
        Paragraph novelName = new Paragraph(chapter.getNovelName() , font);
        novelName.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(novelName);

        font.setSize(20);
        Paragraph chapterName = new Paragraph(chapter.getName(), font);
        chapterName.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(chapterName);

        font.setSize(15);
        Paragraph authorName = new Paragraph("Tác giả: " + chapter.getAuthor().getName(), font);
        authorName.setAlignment(Paragraph.ALIGN_CENTER);
        authorName.setSpacingAfter(20);
        document.add(authorName);

        // Create a stylesheet and add the custom font to it
        StyleSheet styles = new StyleSheet();
        styles.loadTagStyle("body", "face", "CustomFont");
        styles.loadTagStyle("body", "encoding", "Identity-H");
        styles.loadTagStyle("body", "size", "15pt");
        styles.loadTagStyle("p", "style", "text-align: justify;");

        // Parse the HTML content
        org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(chapter.getContent());
        String htmlContent = jsoupDoc.body().html();
        // Parse the HTML content and add it to the document
        List<Element> elements = HTMLWorker.parseToList(new StringReader(htmlContent), styles);
        for (Element element : elements) {
            document.add(element);
        }

        // Close the document
        document.close();
        // Set the content type and headers for the response
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" + chapter.getName() + "_" + chapter.getChapterId()  + ".pdf");
        response.setCharacterEncoding("UTF-8");
        response.setContentLength(baos.size());
        // Write the PDF to the response output stream
        OutputStream os = response.getOutputStream();
        baos.writeTo(os);
        os.flush();
        os.close();
        baos.close();
    }
}
