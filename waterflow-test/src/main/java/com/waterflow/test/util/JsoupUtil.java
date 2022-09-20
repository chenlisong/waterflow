package com.waterflow.test.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class JsoupUtil {

    Logger logger = LoggerFactory.getLogger(JsoupUtil.class);

    public Map<String, Integer> proxyGet() {
        Map<String, Integer> proxys = new HashMap<>();
        try {
            Document document = Jsoup.connect("https://free.kuaidaili.com/free/").get();
            Elements ipElements = document.select("td[data-title=IP]");
            Elements portElements = document.select("td[data-title=PORT]");

            elementLoop: for(int i=0; i<ipElements.size(); i++) {
                Element ipElement = ipElements.get(i);
                Element portElement = portElements.get(i);

                if(ipElement == null || portElement == null || ipElement.childNode(0) == null
                        || portElement.childNode(0) == null) {
                    continue elementLoop;
                }

                String ipElementStr = ipElement.childNode(0).toString();
                String portElementStr = portElement.childNode(0).toString();
                logger.info("jsoup free kuaidaili data, ip is {}, port is {}", ipElementStr, portElementStr);
                try{
                    Integer portElementInt = Integer.parseInt(portElementStr);
                    if(!StringUtils.isEmpty(ipElementStr) && !StringUtils.isEmpty(portElementStr)
                        && portElementInt != null) {
                        proxys.put(ipElementStr, portElementInt);
                    }
                }catch (Exception e) {
                    logger.error("jsoup parse port error.", e);
                }
            }
        }catch (Exception e) {
            logger.error("jsoup proxyGet error", e);
        }
        return proxys;
    }


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
