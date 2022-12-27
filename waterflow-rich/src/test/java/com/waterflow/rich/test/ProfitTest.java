package com.waterflow.rich.test;

import com.alibaba.fastjson.JSON;
import com.waterflow.rich.bean.*;
import com.waterflow.rich.grab.FundGrab;
import com.waterflow.rich.grab.QuoteGrab;
import com.waterflow.rich.init.Application;
import com.waterflow.rich.service.FundService;
import com.waterflow.rich.service.QuoteService;
import com.waterflow.rich.strategy.RichBean;
import com.waterflow.rich.strategy.StdStrategy;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toMap;

import static java.util.Map.Entry.comparingByValue;

@SpringBootTest(classes= Application.class)
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class ProfitTest {

    private Logger logger = LoggerFactory.getLogger(ProfitTest.class);

    @Autowired
    QuoteGrab quoteGrab;

    @Autowired
    QuoteService quoteService;

    @Autowired
    StdStrategy stdStrategy;

    @Autowired
    FundGrab fundGrab;

    static int month = 1;

    static int skipYear = -5;

    static int profitYear = -2;

    static double buyPer = 0.2;

    static double sellPer = 0.5;

    @Value(value="${quote.codes}")
    String quoteCodes;
    @Test
    public void less2Year() throws Exception {

        Date time = new Date();
        time = DateUtils.addYears(time, -1);

        List<String> result = new ArrayList<>();

        for(String code : quoteCodes.split(",")) {
            if(code.length() == 6) {
                List<RichBean> richBeans = fundGrab.autoGrabFundData(code);
                RichBean richBean = richBeans.get(0);

                if(richBean == null || richBean.getTime() == null || richBean.getTime().getTime() > time.getTime()) {
                    result.add(code);
                }
            }else {
                List<Quote> quotes = quoteGrab.findAll(code);
                Quote quote = quotes.get(0);
                if(quote == null || quote.getTime() == null || quote.getTime().getTime() > time.getTime()) {
                    result.add(code);
                }
            }
        }

        logger.info("less two year code is {}", JSON.toJSONString(result));

    }

    @Test
    public void stdTimes() throws Exception{
//        String[] fundCodes = new String[] {"159852"};

        List<CheckView> views = quoteService.checkView(quoteCodes.split(","));

        logger.info("views is {}", JSON.toJSONString(views));

    }



    @Test
    public void profit() throws Exception{

//        String[] fundCodes = FundService.FUNDCODES;
        String[] fundCodes = new String[] {"515790"};

//        String[] quoteCodes = new String[]{"0601989", "0600150", "0601020", "0600428"};
        String[] quoteCodes = new String[]{};

        for(String code: quoteCodes) {
            double profitMoney = quoteProfit(code);
            String name = quoteGrab.quoteName(code);
            logger.info("{}/{}, profit is {}", name, code, profitMoney);
        }

        for(String code: fundCodes) {
            double profitMoney = quoteProfit(code);
            String name = fundGrab.fundName(code);
            logger.info("{}/{}, profit is {}", name, code, profitMoney);
        }

    }

    public double quoteProfit(String code) throws Exception{

        List<QuoteView> quoteViews = null;
        if(code.length() != 6) {
            quoteGrab.downloadQuoteFile(code);
            List<Quote> quotes = quoteGrab.findAll(code);

            quoteViews = quotes.stream()
                    .map(quote -> {
                        return QuoteView.convert(quote);
                    })
                    .collect(Collectors.toList());
        }else {
            List<RichBean> richBeans = fundGrab.autoGrabFundData(code);
            quoteViews = richBeans.stream()
                    .map(richBean -> {
                        return QuoteView.convert(richBean);
                    })
                    .collect(Collectors.toList());
        }
        quoteService.writeLink(quoteViews);

        int diffTime = month * 20;
        stdStrategy.convert2CommonStd(quoteViews, diffTime);

        Date output = DateUtils.addYears(new Date(), skipYear);
        Date beginCal = DateUtils.addMonths(output, month);
        quoteViews = quoteViews.stream()
                .filter(bean -> bean.getTime().getTime() > beginCal.getTime())
                .collect(Collectors.toList());

        quoteService.signleDo(quoteViews);

        Profit profit = new Profit(10 * 10000, 0);
        profit(quoteViews, profit);

        return profit.cash + profit.price * profit.stock;
    }

    private void profit(List<QuoteView> quoteViews, Profit profit) {

        // 保存3年队列值
        LinkedList<ProfitQueueBean> queue = new LinkedList<>();

        Date profitBeginTime = DateUtils.addYears(quoteViews.get(quoteViews.size()-1).getTime(), profitYear);

        for(QuoteView qv : quoteViews) {
            profit.price = qv.getPrice();
            // 维护队尾
            if(qv.getBuyFlag() > 0) {
                ProfitQueueBean last = queue.peekLast();

                if(last == null || last.type == 2) {
                    queue.offerLast(new ProfitQueueBean(qv.getTime(), 1, 1));
                }else {
                    last.cnt = last.cnt + 1;
                }
            }else if(qv.getBuyFlag() < 0) {
                ProfitQueueBean last = queue.peekLast();

                if(last == null || last.type == 1) {
                    queue.offerLast(new ProfitQueueBean(qv.getTime(), 2, 1));
                }else {
                    last.cnt = last.cnt + 1;
                }
            }

            // 维护队头
            long queueTime = DateUtils.addYears(qv.getTime(), -3).getTime();
            while(queue.size() > 0 && queue.getFirst().time.getTime() < queueTime) {
                queue.pollFirst();
            }

            if(qv.getBuyFlag() != 0 && qv.getTime().getTime() > profitBeginTime.getTime()) {
                double avgBuyTime = queue.stream()
                        .filter(bean -> bean.type == 1)
                        .mapToInt(ProfitQueueBean::getCnt)
                        .average().orElse(0);

                double avgSellTime = queue.stream()
                        .filter(bean -> bean.type == 2)
                        .mapToInt(ProfitQueueBean::getCnt)
                        .average().orElse(0);

                if(qv.getBuyFlag() > 0) {
                    double tmpBuyPer = buyPer > 0 ? buyPer : 1/avgBuyTime;
                    profit.buy(tmpBuyPer);
                }else if(qv.getBuyFlag() < 0) {
                    double tmpSellPer = sellPer > 0 ? sellPer : 1/avgSellTime;
                    profit.sell(tmpSellPer);
                }
            }
        }
    }

}
