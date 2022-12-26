package com.waterflow.rich.test;

import com.alibaba.fastjson.JSON;
import com.waterflow.rich.bean.BuyPoint;
import com.waterflow.rich.bean.Quote;
import com.waterflow.rich.bean.QuoteView;
import com.waterflow.rich.grab.QuoteGrab;
import com.waterflow.rich.init.Application;
import com.waterflow.rich.service.QuoteService;
import com.waterflow.rich.strategy.StdRichBean;
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

    @Autowired
    QuoteService quoteService;

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

    @Test
    public void buypoint() throws Exception{
        String[] codes = new String[]{"0600150", "001018"};
        for(String code: codes) {
            List<QuoteView> views = quoteService.quoteViews(code);
            quoteService.writeLink(views);
            stdStrategy.convert2CommonStd(views, 2 * 20);

            List<BuyPoint> points = quoteService.buyPointList(views);

            String content = BuyPoint.output(points);
            logger.info("code: {}, views: {}", code, JSON.toJSONString(points));
            logger.info("code: {}, content: {}", code, content);
        }

    }

    @Test
    public void breach() throws Exception{
        String[] codes = new String[]{"0601020"};
        int diffTime = 1 * 20;

        for(String code: codes) {
            List<QuoteView> views = quoteService.quoteViews(code);

            quoteService.writeLink(views);
            StdRichBean result = stdStrategy.breach(views, diffTime);
            logger.info("code: {}, result: {}", code, JSON.toJSONString(result));
        }
    }

}
