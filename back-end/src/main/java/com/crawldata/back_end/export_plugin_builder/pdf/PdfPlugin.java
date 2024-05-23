
import com.crawldata.back_end.export_plugin_builder.ExportPluginFactory;
import com.crawldata.back_end.model.Chapter;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import jakarta.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.springframework.core.io.ClassPathResource;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.*;
import java.awt.List;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class PdfPlugin implements ExportPluginFactory {
    @Override
    public void export(Chapter chapter, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        String headerkey = "Content-Disposition";
        String headervalue = "attachment; filename=\"" + chapter.getNovelName() + "_" + chapter.getName()  + ".pdf\"";
        response.setHeader(headerkey, headervalue);
        exportImpl(chapter,response);
    }

    private  void exportImpl(Chapter chapter, HttpServletResponse response) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        ClassPathResource fontResource = new ClassPathResource("fonts/ArialUnicodeMSRegular.ttf");
        BaseFont baseFont = BaseFont.createFont(fontResource.getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font = new Font(baseFont, 25, Font.NORMAL);
        font.setColor(Color.black);

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

        Paragraph content = new Paragraph(chapter.getContent().replaceAll("<br>","\n"), font);
        content.setAlignment(Paragraph.ALIGN_JUSTIFIED);
        document.add(content);
        document.close();
    }
}
