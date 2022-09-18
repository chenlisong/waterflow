package com.example.demo;

import com.waterflow.test.util.HttpUtil;
import com.waterflow.test.util.JsoupUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class JsoupTest {

    Logger logger = LoggerFactory.getLogger(JsoupTest.class);

    JsoupUtil jsoupUtil = new JsoupUtil();

    @Test
    public void wikiTest() throws Exception{

        Document document = Jsoup.connect("https://en.wikipedia.org/").get();
        Elements elements = document.select("#mp-itn b a");
        for(Element element: elements) {
            logger.info("title is {}, href is {}", element.attr("title"), element.absUrl("href"));
        }
        jsoupUtil.wikiGet();
    }
}
