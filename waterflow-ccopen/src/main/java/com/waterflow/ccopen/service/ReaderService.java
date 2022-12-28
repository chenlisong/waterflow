package com.waterflow.ccopen.service;

import com.waterflow.common.util.HttpUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ReaderService {

    Logger logger = LoggerFactory.getLogger(ReaderService.class);

    @Value("${reader.file.path}")
    String readerfilePath;

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
//        System.setProperty("webdriver.chrome.driver", "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome");

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

//        resp = new String(resp.getBytes(), "UTF-8");
//        Document doc = Jsoup.parse(resp);
//        Elements links = doc.select("a[href]");
//        for(Element element: links) {
//            logger.info("href is {}, content is {}", element.attr("href"), element.text());
//        }
    }

    // https://www.xxbqg5200.com/shu/421/
    public void listBook(WebDriver driver, String url) throws Exception{

        driver.get(url);
        driver.findElement(By.cssSelector("a[href='javascript:void(0)']")).click();

//        WebElement element = driver.findElement(By.cssSelector("a[href='javascript:void(0)']"));
//        logger.info("WebElement: {}", element.getAttribute("onclick"));
//
//        JavascriptExecutor js = (JavascriptExecutor)driver;
//        js.executeScript(element.getAttribute("onclick"));

        String resp = driver.getPageSource();
        logger.info("exec url: {}, size: {}", url, resp.length());

        List<WebElement> chaps = driver.findElements(By.cssSelector("a[itemprop='url']"));
        for(WebElement chap: chaps) {
            String href = chap.getAttribute("href");
            String text = chap.getText();
            logger.info("href: {}, text: {}", href, text);
        }


//        FileUtils.writeStringToFile(writeFile, content, "UTF-8", true);

    }

    public void downloadWithFirefox(String url) throws Exception{
        FirefoxBinary firefoxBinary = new FirefoxBinary();
        firefoxBinary.addCommandLineOptions("--headless");
        System.setProperty("webdriver.gecko.driver", "/Users/chenlisong/Downloads/app/geckodriver");
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setBinary(firefoxBinary);
        FirefoxDriver driver = new FirefoxDriver(firefoxOptions);
        try {
            driver.get(url);
            driver.manage().timeouts().implicitlyWait(4,
                    TimeUnit.SECONDS);
            System.out.println(driver.getPageSource());
        } finally {
            driver.quit();
        }
    }

}
