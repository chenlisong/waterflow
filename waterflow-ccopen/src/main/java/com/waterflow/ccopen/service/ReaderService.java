package com.waterflow.ccopen.service;

import com.google.common.collect.Lists;
import com.waterflow.ccopen.bean.Catalog;
import com.waterflow.ccopen.bean.Novel;
import com.waterflow.ccopen.dao.CatalogDao;
import com.waterflow.ccopen.dao.NovelDao;
import com.waterflow.common.util.HttpUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class ReaderService {

    Logger logger = LoggerFactory.getLogger(ReaderService.class);

    @Value("${reader.file.path}")
    String readerfilePath;

    @Autowired
    NovelDao novelDao;

    @Autowired
    CatalogDao catalogDao;

    // http://www.quanben.io/n/laobingchuanqi/list.html
    public void detail(String url) {
        String htmlContent = HttpUtil.get(url, null);
        logger.info("html content: {}", htmlContent);
    }

    public WebDriver driver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--allow-http-screen-capture");
        options.addArguments("--disable-impl-side-painting");
        options.addArguments("--disable-setuid-sandbox");
        options.addArguments("--disable-seccomp-filter-sandbox");
        options.setAcceptInsecureCerts(true);

        // 屏蔽webdriver特征
//        options.addArguments("--disable-blink-features");
//        options.addArguments("--disable-blink-features=AutomationControlled");
//        options.addArguments("--incognito"); // 无痕模式

        System.setProperty("webdriver.chrome.driver", "/Users/chenlisong/Downloads/app/chromedriver");

        DesiredCapabilities chrome = DesiredCapabilities.chrome();
        chrome.setCapability(ChromeOptions.CAPABILITY,options);
        chrome.setCapability("acceptInsecureCerts",true);

        WebDriver driver = new ChromeDriver(chrome);
        return driver;
    }

    // https://www.xxbqg5200.com/shu/421/
    public void detailBook(WebDriver driver, String url, File writeFile) throws Exception{

        driver.get(url);

        String resp = driver.getPageSource();
        logger.info("exec url: {}, size: {}", url, resp.length());

        Document doc = Jsoup.parse(resp);
        String content = doc.select("#content").text();

        FileUtils.writeStringToFile(writeFile, content, "UTF-8", true);
    }

    // https://www.xxbqg5200.com/shu/421/
    public void listBook(WebDriver driver, String url) throws Exception{

        driver.get(url);
        driver.findElement(By.cssSelector("a[href='javascript:void(0)']")).click();

        String resp = driver.getPageSource();
        logger.info("exec url: {}, size: {}", url, resp.length());

        List<WebElement> chaps = driver.findElements(By.cssSelector("a[itemprop='url']"));
        for(WebElement chap: chaps) {
            String href = chap.getAttribute("href");
            String text = chap.getText();
            logger.info("href: {}, text: {}", href, text);
        }
    }

    public long insertNovel(Novel novel) {
        if(novel == null || StringUtils.isEmpty(novel.getName()) || StringUtils.isEmpty(novel.getTypeName())) {
            return -1;
        }

        novelDao.deleteByName(novel.getTypeName(), novel.getName());

        novelDao.save(novel);
        return novel.getId();
    }

    public void batchInsertCatalog(List<Catalog> catalogs) {
        if(catalogs == null || catalogs.size() <= 0) {
            return;
        }

        catalogDao.deleteByName(catalogs.get(0).getNovelId());

        for(List<Catalog> unit: Lists.partition(catalogs, 50)) {
            catalogDao.saveAll(unit);
        }
    }

}
