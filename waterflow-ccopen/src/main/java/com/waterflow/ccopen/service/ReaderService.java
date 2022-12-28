package com.waterflow.ccopen.service;

import com.waterflow.common.util.HttpUtil;
import com.waterflow.common.util.JsoupUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReaderService {

    Logger logger = LoggerFactory.getLogger(ReaderService.class);

    @Value("${reader.file.path}")
    String readerfilePath;

    // https://www.xxbqg5200.com/shu/421/
    public void downloadBook(String url) throws Exception{
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        System.setProperty("webdriver.chrome.driver", "/Users/chenlisong/Downloads/app/chromedriver");
        WebDriver driver = new ChromeDriver(options);
        driver.get(url);

//        String resp = HttpUtil.get(url, headers);
        String resp = driver.getPageSource();

        resp = new String(resp.getBytes(), "GB2312");
        Document doc = Jsoup.parse(resp);
        Elements links = doc.select("a[href]");
        for(Element element: links) {
            logger.info("href is {}, content is {}", element.attr("href"), element.text());
        }
    }

}
