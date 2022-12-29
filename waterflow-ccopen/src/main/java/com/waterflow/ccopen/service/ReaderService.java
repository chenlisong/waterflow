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
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;

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

    // http://www.quanben.io/n/chuanyuejizhanshijie/list.html 找到所有章节
    public List<Catalog> listBook(WebDriver driver, String url, Novel novel) throws Exception{
        List<Catalog> catalogs = new ArrayList<>();
        Map<String, String> result = new LinkedHashMap<>();
        driver.get(url);
        driver.findElement(By.cssSelector("a[href='javascript:void(0)']")).click();

        WebElement author = driver.findElement(By.cssSelector("span[itemprop='author']"));
        WebElement name = driver.findElement(By.cssSelector("span[itemprop='name']"));
        WebElement type = driver.findElement(By.cssSelector("span[itemprop='category']"));
        WebElement description = driver.findElement(By.cssSelector("div[itemprop='description']"));

        Date now = new Date();
        novel.setAuthor(author.getText());
        novel.setTypeName(type.getText());
        novel.setContent(description.getText());
        novel.setName(name.getText());
        novel.setCreateTime(now);
        novel.setLastChap(0);
        novel.setUrl(url);

        List<WebElement> chaps = driver.findElements(By.cssSelector("a[itemprop='url']"));
        for(WebElement chap: chaps) {
            String href = chap.getAttribute("href");
            String text = chap.getText();
            logger.info("href: {}, text: {}", href, text);
            Catalog catalog = new Catalog();
            catalog.setCreateTime(now);
            catalog.setFileIndex(0L);
            catalog.setName(text);
            catalog.setUrl(href);
            catalogs.add(catalog);
        }

        return catalogs;
    }

    // quanben.io // 遍历首页，找目录
    public Map<String, String> listC(WebDriver driver, String url) throws Exception{
        Map<String, String> result = new LinkedHashMap<>();

        driver.get(url);
        List<WebElement> chaps = driver.findElements(By.cssSelector("a[itemprop='url']"));
        for(WebElement chap: chaps) {
            String href = chap.getAttribute("href");
            String text = chap.getText();

            if(href != null && href.contains("www.quanben.io/c/") && !result.containsKey(text)) {
                result.put(text, href);
            }
        }

        return result;
    }

    // quanben.io 循环目录，找书
    public List<String> loop(WebDriver driver, String url) throws Exception{
        List<String> urls = new ArrayList<>();

        driver.get(url);
        List<WebElement> chaps = driver.findElements(By.cssSelector("a[itemprop='url']"));
        for(WebElement chap: chaps) {
            String href = chap.getAttribute("href");
            String text = chap.getText();

            if(href != null && href.contains("www.quanben.io/n/")) {
                urls.add(href);
            }
        }

        return urls;
    }

    public long insertNovel(Novel novel) {

//        novelDao.deleteByName(novel.getTypeName(), novel.getName());

        novelDao.saveAndFlush(novel);
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
        catalogDao.flush();
    }

}
