package com.example.demo;

import com.waterflow.test.util.HttpUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HttpTest {

    Logger logger = LoggerFactory.getLogger(HttpTest.class);

    @Test
    public void getTest() throws Exception{
//        String url = "http://www.baidu.com";
//        String write2FilePath = "/opt/data/1/baidu.html";
        String url = "https://chenlisong.oss-cn-beijing.aliyuncs.com/file/che-model-withid.csv";
        String write2File = "/opt/data/che-model-withid.csv";

        String resp = HttpUtil.get(url, null);
        logger.info("http junit test resp is {}", resp);

        boolean suc = HttpUtil.download(url, write2File);
        logger.info("download baidu statue is {}", suc);
    }
}
