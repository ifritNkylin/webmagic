package spider.okhttp;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OkHttpSpider implements PageProcessor {

    List<String> res = new ArrayList<>();

    static String baseUrl = "https://square.github.io/okhttp/";

    Site site = Site.me()
            .setCycleRetryTimes(3)
            .setRetryTimes(3)
            .setTimeOut(1000 * 10);

    @Override
    public void process(Page page) {
        Html html = page.getHtml();

        page.putField("okHttpContent", html);
        CssSelector cssSelector = new CssSelector("body > div.md-container > main > div > div.md-sidebar.md-sidebar--primary > div > div > nav > ul > li");
        String document = html.selectDocument(cssSelector);
        Selectable select = html.select(cssSelector);
        List<String> documentForList = html.selectDocumentForList(cssSelector);
        Selectable list = html.selectList(cssSelector);

        List<String> requests = new ArrayList<>();
        if (select instanceof HtmlNode) {
            HtmlNode htmlNode = (HtmlNode) select;
            List<Element> elements = htmlNode.getElements();
            elements.forEach(element -> {
                String href = element.select("a").get(0).attr("href");
                if (StringUtils.isBlank(href) || StringUtils.startsWithAny(href, ".", "#", "http")) {
                    return;
                }
                requests.add(baseUrl + href);
            });
        }
        page.addTargetRequests(requests);
//        System.out.println(select);
//        System.out.println(list);
        System.out.println(requests);
    }

    private void getAllPageInfo(Page page) {
        Map<String, List<String>> headers = page.getHeaders();
        Html html = page.getHtml();
        Json json = page.getJson();
        Request request = page.getRequest();
        ResultItems resultItems = page.getResultItems();
        int statusCode = page.getStatusCode();
        List<Request> targetRequests = page.getTargetRequests();
        Selectable url = page.getUrl();
        Class<? extends Page> aClass = page.getClass();
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) throws IOException {
//        System.out.println(System.getProperty("https.protocols"));
//        System.setProperty("http.proxySet", "true");
//        System.setProperty("http.proxyHost", "127.0.0.1");
//        System.setProperty("http.proxyPort", "8888");
//        System.setProperty("https.protocols", "TLSv1.2,TLSv1.1,TLSv1.0,SSLv3");
        OkHttpSpider okHttpSpider = new OkHttpSpider();
        Spider.create(okHttpSpider)
                .addUrl("https://square.github.io/okhttp/")
                .thread(6 * 4)
                .addPipeline(new FilePipeline("/Users/ifrit2/IdeaProjects/webmagic/ifritSpider/src/main/resources/okhttp"))
//                .addUrl("https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Array/map")
                .run();
//                .start();
        System.out.println(okHttpSpider.res);

    }
}
