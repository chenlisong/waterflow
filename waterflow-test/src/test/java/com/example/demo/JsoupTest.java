package com.example.demo;

import com.waterflow.test.util.HttpUtil;
import com.waterflow.test.util.JsoupUtil;
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
    public void wikiTest() {
        jsoupUtil.wikiGet();
    }
}
