package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.waterflow.ccopen.bean.AdLog;
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

        readerService.listBook(webDriver, url);

        webDriver.quit();
    }
}