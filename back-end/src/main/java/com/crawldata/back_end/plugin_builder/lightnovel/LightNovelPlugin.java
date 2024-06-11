
import com.crawldata.back_end.model.Author;
import com.crawldata.back_end.model.Chapter;
import com.crawldata.back_end.model.Novel;
import com.crawldata.back_end.plugin_builder.PluginFactory;
import com.crawldata.back_end.response.DataResponse;
import com.crawldata.back_end.utils.AppUtils;
import com.crawldata.back_end.utils.DataResponseUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

/**
 * A plugin implementation for fetching data related to light novels from a remote API.
 */
public class LightNovelPlugin implements PluginFactory {

    // Constants for API endpoints and other configurations
    private final String NOVEL_LIST_CHAPTERS_API = "https://lightnovel.vn/_next/data/mr0xON8OCekMptRE89Z-Z/truyen/%s/danh-sach-chuong.json?page=%s";
    private final String NOVEL_DETAIL_API = "https://lightnovel.vn/_next/data/mr0xON8OCekMptRE89Z-Z/truyen/%s.json";
    private final String AUTHOR_DETAIL_API = "https://lightnovel.vn/_next/data/mr0xON8OCekMptRE89Z-Z/tac-gia/%s.json";
    private final String ALL_NOVELS_API = "https://lightnovel.vn/_next/data/mr0xON8OCekMptRE89Z-Z/the-loai.json?sort=doc-nhieu&page=%s";
    private final String NOVEL_SEARCH_API = "https://lightnovel.vn/_next/data/mr0xON8OCekMptRE89Z-Z/the-loai.json?sort=doc-nhieu&page=%s&keyword=%s";
    private final String CHAPTER_DETAIL_API = "https://lightnovel.vn/_next/data/mr0xON8OCekMptRE89Z-Z/truyen/%s/%s.json";
    private final String IMAGE_URL_PREFIX = "https://static.lightnovel.vn/cdn-cgi/image/w=500,f=auto";
    private final String CRAW_CHAPTER_URL = "https://lightnovel.vn/truyen/%s/%s";
    private static final String CHROME_DRIVER_PATH = "/plugins/chromedriver.exe";

    private final int MAX_RETRIES = 3;
    private final int LIST_CHAPTERS_CAP = 50;
    private static final int TIMEOUT_SECONDS = 5;
    private static final int THREAD_POOL_SIZE = 5;

    /**
     * Reverses a slug string by replacing dashes and spaces with URL-encoded spaces.
     *
     * @param slug The slug string to reverse.
     * @return The reversed slug string.
     */
    private String reverseSlugging(String slug) {
        return slug.replaceAll("-", "%20").replaceAll(" ", "%20");
    }

    /**
     * Retrieves the slug of a chapter based on its index within the novel.
     *
     * @param novelId      The ID of the novel.
     * @param chapterIndex The index of the chapter within the novel.
     * @return The slug of the chapter, or null if the chapter is not found.
     */
    private String getChapterSlugFromChapterIndex(String novelId, int chapterIndex) {
        int page = (int) Math.ceil((double) chapterIndex / LIST_CHAPTERS_CAP);
        int chapterPos = chapterIndex - LIST_CHAPTERS_CAP*(page-1);
        String apiUrl = String.format(NOVEL_LIST_CHAPTERS_API, novelId, page == 1 ? 0 : page);
        JsonObject jsonObject;
        try {
            jsonObject = connectAPI(apiUrl);
            if (jsonObject != null && jsonObject.has("pageProps")) {
                JsonObject pageObject= jsonObject.getAsJsonObject("pageProps");
                JsonArray dataArray = pageObject.getAsJsonArray("chapterList");
                if(dataArray != null && !dataArray.isEmpty() && dataArray.size() >= chapterPos) {
                    return dataArray.get(chapterPos-1).getAsJsonObject().get("slug").getAsString();
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Connects to the given API URL and returns the JSON response as a JsonObject.
     * If the connection fails, it retries the request up to the specified number of times.
     *
     * @param url The URL of the API to connect to.
     * @return The JSON response as a JsonObject, or null if an error occurs after all retries.
     * @throws IOException If an I/O error occurs while connecting to the API.
     */
    private JsonObject connectAPI(String url) throws IOException {
        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpGet request = new HttpGet(url);
                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        String result = EntityUtils.toString(entity);
                        return new Gson().fromJson(result, JsonObject.class);
                    }
                }
            } catch (HttpStatusException e) {
                if (e.getStatusCode() == 404) {
                    System.out.println("HTTP 404 error fetching URL: " + url);
                } else {
                    System.out.println("HTTP error fetching URL: " + url + " Status=" + e.getStatusCode());
                }
            } catch (IOException e) {
                System.out.println("Failed to connect to " + url + " on attempt " + (attempt + 1));
                attempt++;
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(1000); // Wait before retrying
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    System.out.println("Max retries reached for URL: " + url);
                    throw new IOException("Max retries reached for URL: " + url);
                }
            }

        }
        return null;
    }

    /**
     * Retrieves the novel details by its slug.
     *
     * @param slug The slug of the novel.
     * @return The JsonObject representing the novel details.
     */
    private JsonObject getNovelDetailBySlug(String slug) {
        String apiUrl = String.format(NOVEL_DETAIL_API, slug);

        try {
            JsonObject jsonObject = connectAPI(apiUrl);
            if (jsonObject!= null && jsonObject.has("pageProps")) {
                return jsonObject.getAsJsonObject("pageProps").getAsJsonObject("book");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    /**
     * Creates a Novel object from the given JsonObject.
     *
     * @param novelObject The JsonObject representing the novel data.
     * @return The created Novel object.
     */
    public Novel createNovelByJsonData(JsonObject novelObject) {
        String name = novelObject.get("name").getAsString();
        String novelId = novelObject.get("slug").getAsString();
        JsonObject authorObject = novelObject.getAsJsonObject("author");
        Author author = new Author(removeTrailingPart(authorObject.get("slug").getAsString()), authorObject.get("name").getAsString());
        String idFirstChapter = "chuong-1";
        String image = IMAGE_URL_PREFIX + novelObject.get("coverUrl").getAsString();
        String description = novelObject.get("introduction").getAsString().replaceAll("\n", "<br>");
        return new Novel(novelId, name, image, description, author, idFirstChapter);
    }

    /**
     * Creates a Chapter object from the given JsonObject and Novel object.
     *
     * @param chapterObject The JsonObject representing the chapter data.
     * @param novel The Novel object to which the chapter belongs.
     * @return The created Chapter object.
     */
    public Chapter createChapterByJsonData(JsonObject chapterObject, Novel novel) {
        String name = chapterObject.get("name").getAsString();
        String chapterId = "chuong-" + chapterObject.get("index").getAsString();
        return new Chapter(novel.getNovelId(), novel.getName(), chapterId, null, null, name, novel.getAuthor(), "");
    }

    /**
     * Removes the trailing part from the authorId.
     *
     * @param authorId The author ID.
     * @return The author ID without the trailing part.
     */
    public String removeTrailingPart(String authorId) {
        int lastHyphenIndex = authorId.lastIndexOf('-');
        if (lastHyphenIndex != -1) {
            return authorId.substring(0, lastHyphenIndex);
        }
        return authorId;
    }

    public static Map<String, Integer> parseCssText(List<String> cssText) {
        Map<String, Integer> cssOrderMap = new HashMap<>();

        for (String cssEntry : cssText) {
            // Remove unnecessary characters and split the string
            String[] parts = cssEntry.replace("{", "").replace("}", "").replace("order:", "").replace(";", "").trim().split("\\s+");

            if (parts.length == 2) {
                String className = parts[0].trim();
                int orderValue = Integer.parseInt(parts[1].trim());
                cssOrderMap.put(className, orderValue);
            }
        }

        return cssOrderMap;
    }

    /**
     * Retrieves the chapter content through web crawling.
     *
     * @param novelId     The ID of the novel.
     * @param chapterSlug The slug of the chapter.
     * @return The content of the chapter, or null if an error occurs.
     */
    public String getChapterContentThroughCrawling(String novelId, String chapterSlug) {
        String url = String.format(CRAW_CHAPTER_URL, novelId, chapterSlug);
        Document doc = null;
        WebDriver driver = null;
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                System.out.println("lightnovel is using Chrome WebDriver");
                System.setProperty("webdriver.chrome.driver", AppUtils.curDir + CHROME_DRIVER_PATH);

                ChromeOptions options = new ChromeOptions();
                options.addArguments("--headless");
                options.addArguments("--disable-gpu");
                options.addArguments("--window-size=1920,1080");

                driver = new ChromeDriver(options);
                driver.get(url);

                // Scroll to bottom to load more content
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("window.scrollTo(0, document.body.scrollHeight);");

                // Execute JavaScript to get the CSS text
                String script =
                        "var cssText = [];" +
                                "var classes = document.styleSheets[7].rules || document.styleSheets[7].cssRules;" +
                                "for (var x = 0; x < classes.length; x++) {" +
                                "    cssText.push(classes[x].cssText || classes[x].style.cssText);" +
                                "}" +
                                "return cssText;";

                List<String> cssTextList = (List<String>) js.executeScript(script);

                // Wait for the content to be present
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
                wait.until(ExpectedConditions.presenceOfElementLocated(By.id("chapterContent")));

                // Parse the page source with Jsoup
                doc = Jsoup.parse(driver.getPageSource());

                // Select all <p> elements within the content
                Elements paragraphs = doc.select("#chapterContent p");

                // Create a StringBuilder to store the extracted content
                StringBuilder contentBuilder = new StringBuilder();
                Map<String, Integer> cssOrderMap = parseCssText(cssTextList);
                // Loop through each <p> element and sort its <t...> tags
                for (Element paragraph : paragraphs) {
                    Elements tTags = paragraph.children();
                    List<Element> tTagList = new ArrayList<>(tTags);
                    if (tTagList.isEmpty()) {
                        contentBuilder.append(paragraph.toString().trim()).append("\n");
                        continue;
                    }
                    // Sort tTags based on the order defined in cssOrderMap
                    tTagList.sort(Comparator.comparingInt(tag -> cssOrderMap.getOrDefault(tag.tagName(), Integer.MAX_VALUE)));

                    // Reconstruct the paragraph content with sorted <t...> tags
                    StringBuilder paragraphContent = new StringBuilder("<p>");
                    for (Element tTag : tTagList) {
                        paragraphContent.append(tTag.html().replaceAll("&nbsp;", " "));
                    }
                    paragraphContent.append("</p>");

                    contentBuilder.append(paragraphContent.toString().trim()).append("\n"); // Append the sorted content
                }

                // Return the extracted content as a string
                return contentBuilder.toString();
            } catch (TimeoutException e) {
                System.out.println("Timed out waiting for page to load");
            } catch (WebDriverException e) {
                System.out.println("Error fetching chapter content: " + e.getMessage());
            } finally {
                if (driver != null) {
                    driver.quit();
                }
            }
        }
        return null;
    }

    @Override
    public DataResponse getNovelChapterDetail(String novelId, String chapterId) {
        JsonObject novelObject = getNovelDetailBySlug(novelId);
        if(novelObject == null) {
            return DataResponseUtils.getErrorDataResponse("Novel not found on this server");
        }
        Novel novel = createNovelByJsonData(novelObject);

        Chapter result = getContentChapter(novelId, chapterId);
        if(result == null) {
            return DataResponseUtils.getErrorDataResponse("Chapter not found");
        }
        // Create chapter details
        Chapter chapterDetail = new Chapter(novelId, novel.getName(), chapterId, result.getNextChapterId(), result.getPreChapterId(), result.getName(), novel.getAuthor(), result.getContent());

        // Prepare response
        DataResponse dataResponse = new DataResponse();
        dataResponse.setStatus("success");
        dataResponse.setData(chapterDetail);

        return dataResponse;
    }

    @Override
    public DataResponse getNovelListChapters(String novelId, int page) {
        JsonObject novelObject = getNovelDetailBySlug(novelId);
        if (novelObject == null) {
            return DataResponseUtils.getErrorDataResponse("Novel not found on this server");
        }

        Novel novel = createNovelByJsonData(novelObject);
        String apiUrl = String.format(NOVEL_LIST_CHAPTERS_API, novelObject.get("slug").getAsString(), page == 1 ? 0 : page);
        JsonObject jsonObject = null;
        List<Chapter> chapterList = new ArrayList<>();

        try {
            jsonObject = connectAPI(apiUrl);
            if (jsonObject != null && jsonObject.has("pageProps")) {
                JsonObject pageProps = jsonObject.getAsJsonObject("pageProps");
                JsonArray dataArray = pageProps.getAsJsonArray("chapterList");
                // Loop through the data and get the subset for the requested page
                for (int i = 0; i < dataArray.size(); i++) {
                    JsonObject chapterObject = dataArray.get(i).getAsJsonObject();
                    chapterObject.addProperty("index", (page - 1) * LIST_CHAPTERS_CAP + i + 1);
                    chapterList.add(createChapterByJsonData(chapterObject, novel));
                }
                int totalChapters = pageProps.has("total") ? pageProps.get("total").getAsInt() : chapterList.size();
                int totalPages = (int) Math.ceil((double) totalChapters / LIST_CHAPTERS_CAP);
                return new DataResponse("success", totalPages, page, chapterList.size(), null, chapterList, "");
            } else {
                return DataResponseUtils.getErrorDataResponse("No chapters found");
            }
        } catch (IOException e) {
            return DataResponseUtils.getErrorDataResponse("Failed to connect to the API");
        }
    }

    @Override
    public DataResponse getNovelListChapters(String novelId) {
        JsonObject novelObject = getNovelDetailBySlug(novelId);
        if (novelObject == null) {
            return DataResponseUtils.getErrorDataResponse("Novel not found on this server");
        }

        Novel novel = createNovelByJsonData(novelObject);
        int chapterCount = novelObject.get("chapterCount").getAsInt();
        int totalPages = (int) Math.ceil((double) chapterCount / LIST_CHAPTERS_CAP);

        List<Chapter> chapterList = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(Math.min(totalPages, Runtime.getRuntime().availableProcessors()));
        List<Future<List<Chapter>>> futures = new ArrayList<>();

        for (int page = 1; page <= totalPages; page++) {
            int finalPage = page;
            Callable<List<Chapter>> task = () -> {
                String apiUrl = String.format(NOVEL_LIST_CHAPTERS_API, novelObject.get("slug").getAsString(), finalPage == 1 ? 0 : finalPage);
                List<Chapter> chapters = new ArrayList<>();
                try {
                    JsonObject jsonObject = connectAPI(apiUrl);
                    if (jsonObject != null && jsonObject.has("pageProps")) {
                        JsonObject pageProps = jsonObject.getAsJsonObject("pageProps");
                        JsonArray dataArray = pageProps.getAsJsonArray("chapterList");
                        for (int i = 0; i < dataArray.size(); i++) {
                            JsonObject chapterObject = dataArray.get(i).getAsJsonObject();
                            chapterObject.addProperty("index", (finalPage - 1) * LIST_CHAPTERS_CAP + i + 1);
                            chapters.add(createChapterByJsonData(chapterObject, novel));
                        }
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    // Return an empty list if there is an error
                    return chapters;
                }
                return chapters;
            };
            futures.add(executorService.submit(task));
        }

        executorService.shutdown();
        try {
            for (Future<List<Chapter>> future : futures) {
                chapterList.addAll(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e.getMessage());
            return DataResponseUtils.getErrorDataResponse("Error while fetching chapters in parallel");
        }

        return new DataResponse("success", totalPages, 1, chapterList.size(), null, chapterList, "");
    }

    @Override
    public DataResponse getNovelDetail(String novelId) {
        JsonObject novelObject = getNovelDetailBySlug(novelId);
        if (novelObject != null) {
            Novel novel = createNovelByJsonData(novelObject);
            return new DataResponse("success", null, null, null, null, novel, "");
        } else {
            return DataResponseUtils.getErrorDataResponse("Novel not found on this server");
        }
    }

    @Override
    public DataResponse getAuthorDetail(String authorId) {
        String apiUrl = String.format(AUTHOR_DETAIL_API, authorId);
        JsonObject jsonObject = null;
        try {
            jsonObject = connectAPI(apiUrl);
            if (jsonObject != null && jsonObject.has("author")) {
                List<Novel> novelList = new ArrayList<>();
                if (jsonObject.has("data")) {
                    JsonArray dataArray = jsonObject.getAsJsonArray("data");
                    // Loop through the data
                    for (int i = 0; i < dataArray.size(); i++) {
                        JsonObject novelObject = dataArray.get(i).getAsJsonObject();
                        novelList.add(createNovelByJsonData(novelObject));
                    }
                }
                return new DataResponse("success", 1, 1, novelList.size(), null, novelList, null);
            } else {
                return DataResponseUtils.getErrorDataResponse("No authors found");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return DataResponseUtils.getErrorDataResponse("Failed to connect to the API");
        }

    }

    @Override
    public DataResponse getAllNovels(int page, String search) {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        String apiUrl = String.format(ALL_NOVELS_API, page);
        JsonObject jsonObject = null;
        try {
            jsonObject = connectAPI(apiUrl);
            List<Novel> novelList = new ArrayList<>();
            if (jsonObject != null && jsonObject.has("pageProps")) {
                JsonObject pageObject = jsonObject.getAsJsonObject("pageProps");
                JsonArray dataArray = pageObject.getAsJsonArray("data");
                List<Future<Novel>> futures = new ArrayList<>();

                for (int i = 0; i < dataArray.size(); i++) {
                    String novelSlug = dataArray.get(i).getAsJsonObject().get("slug").getAsString();
                    Future<Novel> future = executorService.submit(() -> {
                        JsonObject novelObject = getNovelDetailBySlug(novelSlug);
                        return novelObject != null ? createNovelByJsonData(novelObject) : null;
                    });
                    futures.add(future);
                }

                for (Future<Novel> future : futures) {
                    try {
                        Novel novel = future.get();
                        if (novel != null) {
                            novelList.add(novel);
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        System.err.println("Error fetching novel details: " + e.getMessage());
                    }
                }

                int totalItems = pageObject.has("total") ? pageObject.get("total").getAsInt() : dataArray.size();
                int totalPages = (int) Math.ceil((double) totalItems / 18);
                return new DataResponse("success", totalPages, page, novelList.size(), search, novelList, "");
            } else {
                return DataResponseUtils.getErrorDataResponse("Failed to fetch novels");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return DataResponseUtils.getErrorDataResponse("Failed to connect to the API");
        } finally {
            executorService.shutdown();
        }
    }

    @Override
    public DataResponse getNovelSearch(int page, String key, String orderBy) {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        String apiUrl = String.format(NOVEL_SEARCH_API, page, reverseSlugging(key));
        JsonObject jsonObject = null;
        try {
            jsonObject = connectAPI(apiUrl);
            List<Novel> novelList = new ArrayList<>();
            if (jsonObject != null && jsonObject.has("pageProps")) {
                JsonObject pageObject = jsonObject.getAsJsonObject("pageProps");
                JsonArray dataArray = pageObject.getAsJsonArray("data");
                List<Future<Novel>> futures = new ArrayList<>();

                for (int i = 0; i < dataArray.size(); i++) {
                    String novelSlug = dataArray.get(i).getAsJsonObject().get("slug").getAsString();
                    Future<Novel> future = executorService.submit(() -> {
                        JsonObject novelObject = getNovelDetailBySlug(novelSlug);
                        return novelObject != null ? createNovelByJsonData(novelObject) : null;
                    });
                    futures.add(future);
                }

                for (Future<Novel> future : futures) {
                    try {
                        Novel novel = future.get();
                        if (novel != null) {
                            novelList.add(novel);
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        System.err.println("Error fetching novel details: " + e.getMessage());
                    }
                }

                int totalItems = pageObject.has("total") ? pageObject.get("total").getAsInt() : dataArray.size();
                int totalPages = (int) Math.ceil((double) totalItems / 18);
                return new DataResponse("success", totalPages, page, novelList.size(), key, novelList, "");
            } else {
                return DataResponseUtils.getErrorDataResponse("Failed to fetch novels");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return DataResponseUtils.getErrorDataResponse("Failed to connect to the API");
        } finally {
            executorService.shutdown();
        }
    }

    @Override
    public Chapter getContentChapter(String novelId, String chapterId) {
        String chapterSlug = getChapterSlugFromChapterIndex(novelId, Integer.parseInt(chapterId.split("-")[1]));
        String urlChapter = String.format(CHAPTER_DETAIL_API, novelId, chapterSlug);

        JsonObject doc = null;
        try {
            doc = connectAPI(urlChapter);
            if (doc != null && doc.has("pageProps")) {
                JsonObject pageProps = doc.getAsJsonObject("pageProps");
                JsonObject chapterObject = pageProps.getAsJsonObject("chapter");
                if (chapterObject != null) {
                    String chapterName = chapterObject.get("name").getAsString();
                    String content = null;
                    if(chapterObject.has("data") && !chapterObject.get("data").isJsonNull()) {
                        content = getChapterContentThroughCrawling(novelId, chapterSlug);
                        content = content.replaceAll("\n", "<br>");
                    } else {
                        content = chapterObject.get("content").getAsString();
                        content = content.replaceAll("\n", "<br><br>");
                    }

                    int chapterIndex = Integer.parseInt(chapterId.split("-")[1]);
                    String nextChapterId = pageProps.has("nextChapter") && !pageProps.get("nextChapter").isJsonNull() ? "chuong-" + (chapterIndex + 1) : null;
                    String preChapterId = pageProps.has("prevChapter") && !pageProps.get("prevChapter").isJsonNull() ? "chuong-" + (chapterIndex - 1) : null;

                    // Create chapter details

                    return new Chapter(novelId, null, chapterId, nextChapterId, preChapterId, chapterName, null, content);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
