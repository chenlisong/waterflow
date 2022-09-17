package com.waterflow.test.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsoupUtil {

    Logger logger = LoggerFactory.getLogger(JsoupUtil.class);

    public String wikiGet() {

        try {
            Document doc = Jsoup.connect("https://en.wikipedia.org/").get();
            Elements elements = doc.select("#mp-itn b a");
            for (Element element : elements) {
                logger.info("title is {}, href is {}", element.attr("title"), element.absUrl("href"));
            }
        }catch (Exception e) {
            logger.error("error occur.", e);
        }
        return "";
    }

}
