package com.crawldata.back_end.export_plugin_builder.epub;

import com.crawldata.back_end.export_plugin_builder.ExportPluginFactory;
import com.crawldata.back_end.model.Chapter;
import com.crawldata.back_end.model.Novel;
import com.crawldata.back_end.plugin_builder.PluginFactory;
import com.crawldata.back_end.response.DataResponse;
import com.crawldata.back_end.utils.AppUtils;
import com.crawldata.back_end.utils.FileUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Parser;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.zeroturnaround.zip.ZipUtil;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@NoArgsConstructor
public class EpubPlugin implements ExportPluginFactory {
    private PluginFactory pluginFactory;
    private Novel novel;
    private List<Chapter> chapterList;
    @Override
    public void export(PluginFactory plugin, String novelId, HttpServletResponse response) throws IOException {

        //read untitled.epub to use it as template.
        String epubJarFilePath = AppUtils.curDir + "/export_plugins/epub.jar";
        try (JarFile jarFile = new JarFile(epubJarFilePath)) {
            JarEntry entry = jarFile.getJarEntry("untitled.epub");
            if (entry == null) {
                System.err.println("Error: 'untitled.epub' not found in the JAR file.");
                return;
            }
            getNovelInfo(plugin, novelId);
            try (InputStream inputStream = jarFile.getInputStream(entry)) {
                String fileName = novel.getName() + " - " + novel.getAuthor().getName() + ".epub";
                fileName = fileName.replaceAll("[:/\\?\\*]", "");
                fileName = FileUtils.validate(AppUtils.curDir + "/out/" + fileName);
                FileUtils.byte2file(FileUtils.stream2byte(inputStream), fileName);
                modifyEpubFile(fileName);
                sendFileToClientAndDelete(fileName, response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getNovelInfo(PluginFactory plugin, String novelId) {
        pluginFactory = plugin;
        DataResponse dataResponse = pluginFactory.getNovelDetail(novelId);
        if(dataResponse != null && dataResponse.getStatus().equals("success")) {
            novel = (Novel) dataResponse.getData();
        }
        dataResponse = pluginFactory.getNovelListChapters(novel.getNovelId());
        if (dataResponse != null && "success".equals(dataResponse.getStatus())) {
            Object data = dataResponse.getData();
            if (data instanceof List<?> dataList) {
                if (!dataList.isEmpty() && dataList.get(0) instanceof Chapter) {
                    chapterList = (List<Chapter>) dataList;
                }
            }
        }
    }

    private void modifyEpubFile(String epubFilePath) throws IOException {
        File tempFile = File.createTempFile("epub", ".epub");
        try (ZipFile zipFile = new ZipFile(epubFilePath);
             ZipOutputStream zos = new ZipOutputStream(new java.io.FileOutputStream(tempFile))) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                try (InputStream is = zipFile.getInputStream(entry)) {
                    switch (entry.getName()) {
                        case "OEBPS/chap01.xhtml":
                            for (Chapter chapter : chapterList) {
                                // Open a new InputStream for each chapter modification
                                try (InputStream chapterIs = zipFile.getInputStream(entry)) {
                                    modifyAndSaveChapterXhtml(zos, entry, chapterIs, chapter);
                                }
                            }
                            break;
                        case "OEBPS/cover.xhtml":
                            modifyCoverXhtml(zos, entry, is);
                            break;
                        case "OEBPS/images/epublogo.png":
                            modifyEpubLogo(zos, entry);
                            break;
                        case "OEBPS/title_page.xhtml":
                            modifyTitlePageXhtml(zos, entry, is);
                            break;
                        case "OEBPS/toc.ncx":
                            modifyTocNcx(zos, entry, is);
                            break;
                        case "OEBPS/toc.xhtml":
                            modifyTocXhtml(zos, entry, is);
                            break;
                        case "OEBPS/content.opf":
                            modifyContentOpf(zos, entry, is);
                            break;
                        default:
                            // Copy other entries without modification
                            zos.putNextEntry(new ZipEntry(entry.getName()));
                            is.transferTo(zos);
                            break;
                    }
                }
                zos.closeEntry();
            }
        }

        // Replace the original EPUB file with the modified one
        File originalFile = new File(epubFilePath);
        if (!originalFile.delete() || !tempFile.renameTo(originalFile)) {
            throw new IOException("Failed to replace the original EPUB file with the modified one.");
        }
        System.out.println("Export " + novel.getName() + " - " + novel.getAuthor().getName() + ".epub success");
    }

    private void modifyCoverXhtml(ZipOutputStream zos, ZipEntry entry, InputStream is) throws IOException {
        Document document = Jsoup.parse(is, "UTF-8", "", Parser.xmlParser());

        Element titleElement = document.getElementsByTag("h1").first();
        if (titleElement != null) {
            titleElement.text(novel.getName());
        }

        Element authorElement = document.getElementsByTag("h3").first();
        if (authorElement != null) {
            authorElement.text(novel.getAuthor().getName());
        }
        zos.putNextEntry(new ZipEntry(entry.getName()));
        zos.write(document.outerHtml().getBytes());
    }

    private void modifyEpubLogo(ZipOutputStream zos, ZipEntry entry) throws IOException {
        // Use the image URL from the novel object
        String imageUrl = novel.getImage();
        byte[] newImage = downloadImage(imageUrl);
        zos.putNextEntry(new ZipEntry(entry.getName()));
        zos.write(newImage);
    }

    private byte[] downloadImage(String imageUrl) throws IOException {
        try (InputStream in = new URL(imageUrl).openStream()) {
            return in.readAllBytes();
        }
    }

    private void modifyTitlePageXhtml(ZipOutputStream zos, ZipEntry entry, InputStream is) throws IOException {
        Document document = Jsoup.parse(is, "UTF-8", "", Parser.xmlParser());
        Element titleElement = document.getElementsByTag("h1").first();
        if (titleElement != null) {
            titleElement.text(novel.getName());
        }

        Element authorElement = document.getElementsByTag("h2").first();
        if (authorElement != null) {
            authorElement.text(novel.getAuthor().getName());
        }
        zos.putNextEntry(new ZipEntry(entry.getName()));
        zos.write(document.outerHtml().getBytes());
    }

    private void modifyTocNcx(ZipOutputStream zos, ZipEntry entry, InputStream is) throws IOException {
        try {
            // Create a DocumentBuilder to parse XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document document = builder.parse(new InputSource(is));

            // Find and modify the title element
            NodeList titleNodes = document.getElementsByTagName("docTitle");
            if (titleNodes.getLength() > 0) {
                org.w3c.dom.Element titleElement = (org.w3c.dom.Element) titleNodes.item(0);
                Node textNode = titleElement.getElementsByTagName("text").item(0);
                if (textNode != null) {
                    textNode.setTextContent(novel.getName() + " - " + novel.getAuthor().getName());
                }
            }

            // Find the navMap element and add new chapters to it
            NodeList navMapNodes = document.getElementsByTagName("navMap");
            if (navMapNodes.getLength() > 0) {
                org.w3c.dom.Element navMapElement = (org.w3c.dom.Element) navMapNodes.item(0);
                int index = navMapElement.getElementsByTagName("navPoint").getLength() + 1;
                for (Chapter chapter : chapterList) {
                    org.w3c.dom.Element navPointElement = document.createElement("navPoint");
                    navPointElement.setAttribute("id", chapter.getChapterId());
                    navPointElement.setAttribute("playOrder", String.valueOf(index++));

                    org.w3c.dom.Element navLabelElement = document.createElement("navLabel");
                    org.w3c.dom.Element textElement = document.createElement("text");
                    textElement.setTextContent(chapter.getName().trim());

                    org.w3c.dom.Element contentElement = document.createElement("content");
                    contentElement.setAttribute("src", "text/"+chapter.getChapterId() + ".xhtml");

                    navLabelElement.appendChild(textElement);
                    navPointElement.appendChild(navLabelElement);
                    navPointElement.appendChild(contentElement);
                    navMapElement.appendChild(navPointElement);
                }
            }

            // Write the modified content back to the ZIP output stream
            zos.putNextEntry(new ZipEntry(entry.getName()));
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(zos);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
        } catch (ParserConfigurationException | SAXException | TransformerException e) {
            throw new IOException("Failed to modify toc.ncx", e);
        }
    }


    private void modifyTocXhtml(ZipOutputStream zos, ZipEntry entry, InputStream is) throws IOException {
        Document document = Jsoup.parse(is, "UTF-8", "", Parser.xmlParser());
        Element divElement = document.getElementsByTag("div").first();
        List<String> defaultElement = Arrays.asList("cover.xhtml", "title_page.xhtml", "copyright.xhtml", "toc.xhtml");
        List<String> defaultText = Arrays.asList("Cover", "Title Page", "Copyright", "Mục lục");
        if (divElement != null) {
            for(int i = 0; i<defaultElement.size(); i++) {
                Element liElement = document.createElement("li");
                Element aElement = document.createElement("a");
                aElement.attr("href", defaultElement.get(i));
                aElement.text(defaultText.get(i));
                liElement.appendChild(aElement);
                divElement.appendChild(liElement);
            }

            for(Chapter chapter : chapterList) {
                Element liElement = document.createElement("li");
                Element aElement = document.createElement("a");
                aElement.attr("href", "text/"+chapter.getChapterId()+".xhtml");
                aElement.text(chapter.getName().trim());
                liElement.appendChild(aElement);
                divElement.appendChild(liElement);
            }

        }
        zos.putNextEntry(new ZipEntry(entry.getName()));
        zos.write(document.outerHtml().getBytes());
    }

    private void modifyContentOpf(ZipOutputStream zos, ZipEntry entry, InputStream is) throws IOException {
        Document document = Jsoup.parse(is, "UTF-8", "", Parser.xmlParser());
        Element titleElement = document.getElementsByTag("dc:title").first();
        if (titleElement != null) {
            titleElement.text(novel.getName() + " - " + novel.getAuthor().getName());
        }

        Element manifestElement = document.getElementsByTag("manifest").first();
        if (manifestElement != null) {
            for (Chapter chapter : chapterList) {
                Element itemElement = document.createElement("item");
                itemElement.attr("id", chapter.getChapterId());
                itemElement.attr("href", "text/"+chapter.getChapterId()+".xhtml");
                itemElement.attr("media-type", "application/xhtml+xml");
                manifestElement.appendChild(itemElement);
            }
        }

        Element spineElement = document.getElementsByTag("spine").first();
        if (spineElement != null) {
            for (Chapter chapter : chapterList) {
                Element itemrefElement = document.createElement("itemref");
                itemrefElement.attr("idref", chapter.getChapterId());
                spineElement.appendChild(itemrefElement);
            }
        }
        zos.putNextEntry(new ZipEntry(entry.getName()));
        zos.write(document.outerHtml().getBytes());
    }

    private void modifyAndSaveChapterXhtml(ZipOutputStream zos, ZipEntry originalEntry, InputStream is, Chapter chapter) throws IOException {
        // Parse the original chap01.xhtml content
        Document document = Jsoup.parse(is, "UTF-8", "", Parser.xmlParser());

        // Make the necessary modifications to the chapter content
        Element titleElement = document.getElementsByTag("title").first();
        if (titleElement != null) {
            titleElement.text(chapter.getName());
        }

        Element bodyElement = document.getElementsByTag("body").first();
        if (bodyElement != null) {
            // Assuming chapter content is stored in the 'content' field
            DataResponse dataResponse = pluginFactory.getNovelChapterDetail(novel.getNovelId(), chapter.getChapterId());
            if(dataResponse != null && dataResponse.getStatus().equals("success")) {
                Chapter data = (Chapter) dataResponse.getData();
                bodyElement.html(data.getContent());
            }
        }

        // Create a new entry for the modified chapter
        String chapterEntryName = "OEBPS/text/" + chapter.getChapterId() + ".xhtml";
        zos.putNextEntry(new ZipEntry(chapterEntryName));

        // Write the modified document to the ZIP output stream
        zos.write(document.outerHtml().getBytes("UTF-8"));
    }

    private void sendFileToClientAndDelete(String fileName, HttpServletResponse response) throws IOException {
        File file = new File(fileName);

        // URL encode the filename to handle non-ASCII characters
        String encodedFileName = encodeFileName(file.getName());

        response.setContentType("application/epub+zip");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
        response.setContentLength((int) file.length());

        try (InputStream fis = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        }

        if (!file.delete()) {
            System.err.println("Failed to delete temporary file: " + file.getAbsolutePath());
        }
    }

    private String encodeFileName(String fileName) {
        try {
            return URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }
    }
}
