package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.waterflow.ccopen.bean.AdLog;
import com.waterflow.ccopen.dao.AdLogDao;
import com.waterflow.ccopen.init.Application;
import com.waterflow.ccopen.service.ReaderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@SpringBootTest(classes= Application.class)
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class ReaderTest {

    private Logger logger = LoggerFactory.getLogger(ReaderTest.class);

    @Autowired
    ReaderService readerService;

    @Test
    public void outputParamTest() throws Exception{
        String bookUrl = "https://www.xxbqg5200.com/shu/421/";

        readerService.downloadBook(bookUrl);
    }
}