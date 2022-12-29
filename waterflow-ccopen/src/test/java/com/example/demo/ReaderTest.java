package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.waterflow.ccopen.bean.AdLog;
import com.waterflow.ccopen.bean.Catalog;
import com.waterflow.ccopen.bean.Novel;
import com.waterflow.ccopen.dao.AdLogDao;
import com.waterflow.ccopen.init.Application;
import com.waterflow.ccopen.service.ReaderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes= Application.class)
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class ReaderTest {

    private Logger logger = LoggerFactory.getLogger(ReaderTest.class);

    @Autowired
    ReaderService readerService;

    @Value("${reader.file.path}")
    String readerfilePath;

    @Test
    public void downloadTest() throws Exception{
//        String url = "http://www.quanben.io/n/laobingchuanqi/list.html";
//        String bookUrl = "https://www.baidu.com";
//        String bookUrl = "http://www.zhihu.com";
        String url = "http://www.quanben.io/n/laobingchuanqi/%d.html";

        WebDriver webDriver = readerService.driver();

        File writeFile = new File(readerfilePath + "/老兵传奇.txt");
        for(int i=1; i<=2459; i++) {

            String newUrl = String.format(url, i);
            readerService.detailBook(webDriver, newUrl, writeFile);
        }

        webDriver.quit();
    }

    @Test
    public void listTest() throws Exception{
        String url = "http://www.quanben.io/n/laobingchuanqi/list.html";

        WebDriver webDriver = readerService.driver();

        readerService.listBook(webDriver, url, new Novel());

        webDriver.quit();
    }

    @Test
    public void grabTest() throws Exception{
        String url = "http://www.quanben.io";
        WebDriver webDriver = readerService.driver();

        Map<String, String> cMap = readerService.listC(webDriver, url);

        for(String text: cMap.keySet()) {
            String cUrl = cMap.get(text);
            cLoop: for(int i=1; i<5; i++) {
                String tempBookUrl = cUrl.replace(".html", "_" + i + ".html");

                // 书名、url
                List<String> urls = readerService.loop(webDriver, tempBookUrl);

                for(String bookUrl: urls) {
                    Novel novel = new Novel();
                    List<Catalog> catalogs = readerService.listBook(webDriver, bookUrl+"list.html", novel);

                    long novelId = readerService.insertNovel(novel);
                    catalogs.stream().forEach(catalog -> catalog.setNovelId(novelId));

                    readerService.batchInsertCatalog(catalogs);
                    logger.info("book insert suc. name is: {}", novel.getName());
                }

                if(urls == null || urls.size() < 12) {
                    break cLoop;
                }
            }
        }

        webDriver.quit();
    }

    @Test
    public void insertSqlTest() {
        Novel novel = new Novel();
        novel.setName("123");
        novel.setTypeName("123");
        novel.setContent("123");
        novel.setCreateTime(new Date());
        novel.setAuthor("author");
        long id = readerService.insertNovel(novel);
        logger.info("id : {}", id);
    }
}