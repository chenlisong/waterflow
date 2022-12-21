package com.waterflow.rich.test;

import com.alibaba.fastjson.JSON;
import com.waterflow.rich.bean.Quote;
import com.waterflow.rich.grab.QuoteGrab;
import com.waterflow.rich.init.Application;
import com.waterflow.rich.strategy.StdStrategy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(classes= Application.class)
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class QuoteTest {

    private Logger logger = LoggerFactory.getLogger(QuoteTest.class);

    @Autowired
    QuoteGrab quoteGrab;

    @Autowired
    StdStrategy stdStrategy;

    @Test
    public void hello() {
        logger.info("hello is {}", "cls");
    }

    @Test
    public void grab() {
        quoteGrab.downloadQuoteFile("0600150", "20121220", "20221220");
    }

    @Test
    public void findAll() throws Exception{
        List<Quote> quoteList = quoteGrab.findAll("0600150");

        logger.info("quote is {}", JSON.toJSONString(quoteList.get(0)));
        logger.info("quote date is {}", JSON.toJSONString(quoteList.get(0).getDate()));

        int diffTime = 3 * 20;

        stdStrategy.convert2CommonStd(quoteList, diffTime);

        logger.info("quote is {}", JSON.toJSONString(quoteList.get(1000)));
        logger.info("quote date is {}", JSON.toJSONString(quoteList.get(1000).getDate()));
    }

}
