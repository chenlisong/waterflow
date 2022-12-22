package com.waterflow.rich.control;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.waterflow.rich.bean.*;
import com.waterflow.rich.grab.FundGrab;
import com.waterflow.rich.grab.QuoteGrab;
import com.waterflow.rich.service.FundService;
import com.waterflow.rich.service.QuoteService;
import com.waterflow.rich.strategy.RichBean;
import com.waterflow.rich.strategy.StdStrategy;
import com.waterflow.rich.util.LocalCacheUtil;
import com.waterflow.rich.util.MetaAntvUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/quote")
public class QuoteController {

    Logger logger = LoggerFactory.getLogger(QuoteController.class);

    @Autowired
    StdStrategy stdStrategy;

    @Autowired
    QuoteGrab quoteGrab;

    @Autowired
    QuoteService quoteService;

    @Autowired
    FundService fundService;

    @Autowired
    FundGrab fundGrab;

    @RequestMapping("/fund/std")
    public String fundStd(Model model, @RequestParam(value = "code", defaultValue = "0600150") String code,
                      @RequestParam(value = "month", defaultValue = "3") int month,
                      @RequestParam(value = "skipYear", defaultValue = "-3") int skipYear) throws Exception{
        logger.info("quote std exec, code is {}...", code);

        List<RichBean> richBeans = fundGrab.autoGrabFundData(code);
        try{
            List<QuoteView> quoteViews = richBeans.stream()
                    .map(richBean -> {
                        return QuoteView.convert(richBean);
                    })
                    .collect(Collectors.toList());
            quoteService.writeLink(quoteViews);


            int diffTime = month * 20;
            stdStrategy.convert2CommonStd(quoteViews, diffTime);

            Date output = DateUtils.addYears(new Date(), skipYear);
            Date beginCal = DateUtils.addMonths(output, month);
            quoteViews = quoteViews.stream()
                    .filter(bean -> bean.getTime().getTime() > beginCal.getTime())
                    .collect(Collectors.toList());

            quoteService.avgCommon(quoteViews, "price", "avg5Price", -1, 5);

            double stand = quoteViews.stream().min(Comparator.comparing(view -> view.getP2fsd()))
                    .get().getP2fsd();
            quoteService.signleDo(quoteViews);

            quoteViews = quoteViews.stream()
                    .filter(bean -> bean.getTime().getTime() > output.getTime())
                    .collect(Collectors.toList());

            List<JSONObject> jsonObjects = MetaAntvUtil.convert2AntvWithSuperClass(quoteViews,
                    "date", "price", "p1sd", "p1fsd", "p2sd", "p2fsd"
                    , "avg5Price", "buyFlag");
            List<JSONObject> list = new ArrayList<>();

            JSONObject bugfix = new JSONObject();
            bugfix.put("x", "1-1");
            bugfix.put("y", 0);
            bugfix.put("series", "t1");
            list.add(bugfix);

            list.addAll(jsonObjects);
            model.addAttribute("data", JSON.toJSONString(list));

            String fundName = fundGrab.fundName(code);
            model.addAttribute("fundName", fundName + "/" + code);

            String content = quoteService.outputBuyPoint(quoteViews);
            model.addAttribute("content", content);
        }catch (Exception e) {
            logger.error("error.", e);
        }

        return "quote";
    }

    @RequestMapping("/std")
    public String std(Model model, @RequestParam(value = "code", defaultValue = "0600150") String code,
                      @RequestParam(value = "month", defaultValue = "3") int month,
                      @RequestParam(value = "skipYear", defaultValue = "-3") int skipYear) {
        logger.info("quote std exec, code is {}...", code);

        quoteGrab.downloadQuoteFile(code);

        List<Quote> quotes = null;
        try{
            quotes = quoteGrab.findAll(code);

            List<QuoteView> quoteViews = quotes.stream()
                    .map(quote -> {
                        return QuoteView.convert(quote);
                    })
                    .collect(Collectors.toList());
            quoteService.writeLink(quoteViews);

            int diffTime = month * 20;
            stdStrategy.convert2CommonStd(quoteViews, diffTime);

            Date output = DateUtils.addYears(new Date(), skipYear);
            Date beginCal = DateUtils.addMonths(output, month);
            quoteViews = quoteViews.stream()
                    .filter(bean -> bean.getTime().getTime() > beginCal.getTime())
                    .collect(Collectors.toList());

            quoteService.avgCommon(quoteViews, "price", "avg5Price", -1, 20);

            double stand = quoteViews.stream().min(Comparator.comparing(view -> view.getP2fsd()))
                            .get().getP2fsd();
            quoteService.signleDo(quoteViews);

            quoteService.avgCommon(quoteViews, "vol", "avg5Vol", stand, 5);
            quoteService.avgCommon(quoteViews, "pressure", "avg5Pressure", stand, 5);

            quoteViews = quoteViews.stream()
                    .filter(bean -> bean.getTime().getTime() > output.getTime())
                    .collect(Collectors.toList());
            List<JSONObject> jsonObjects = MetaAntvUtil.convert2AntvWithSuperClass(quoteViews,
                        "date", "price", "p1sd", "p1fsd", "p2sd", "p2fsd"
                                , "avg5Price", "avg5Vol", "avg5Pressure", "buyFlag");
            List<JSONObject> list = new ArrayList<>();

            JSONObject bugfix = new JSONObject();
            bugfix.put("x", "1-1");
            bugfix.put("y", 0);
            bugfix.put("series", "t1");
            list.add(bugfix);

            list.addAll(jsonObjects);
            model.addAttribute("data", JSON.toJSONString(list));

            String fundName = quoteGrab.quoteName(code);
            model.addAttribute("fundName", fundName + "/" + code);

            String content = quoteService.outputBuyPoint(quoteViews);
            model.addAttribute("content", content);
        }catch (Exception e) {
            logger.error("error.", e);
        }

        return "quote";
    }

    @RequestMapping("/profit")
    public String profit(Model model, @RequestParam(value = "code", defaultValue = "0600150") String code,
                      @RequestParam(value = "month", defaultValue = "2") int month,
                      @RequestParam(value = "skipYear", defaultValue = "-5") int skipYear) {
        logger.info("quote profit exec, code is {}...", code);

        quoteGrab.downloadQuoteFile(code);

        try{
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

            double stand = quoteViews.stream().min(Comparator.comparing(view -> view.getP2fsd()))
                    .get().getP2fsd();
            quoteService.signleDo(quoteViews);

            Profit profit = new Profit(10 * 10000, 0);

            List<Profit> profitList = new ArrayList<>();
            profit(quoteViews, profit, profitList);

            List<JSONObject> jsonObjects = MetaAntvUtil.convert2AntvWithSuperClass(profitList,
                    "date", "all", "stock", "cash");
            List<JSONObject> list = new ArrayList<>();

            JSONObject bugfix = new JSONObject();
            bugfix.put("x", "1-1");
            bugfix.put("y", 0);
            bugfix.put("series", "t1");
            list.add(bugfix);

            list.addAll(jsonObjects);
            model.addAttribute("data", JSON.toJSONString(list));

            String fundName = null;
            if(code.length() != 6) {
                fundName = quoteGrab.quoteName(code);
            }else {
                fundName = fundGrab.fundName(code);
            }
            model.addAttribute("fundName", fundName + "/" + code);

            model.addAttribute("content", "");
        }catch (Exception e) {
            logger.error("error.", e);
        }

        return "quote";
    }

    @RequestMapping("/checkview")
    public String checkview(Model model) {

        String[] codes = new String[] {
                "004432", "110020", "501302", "004488", "004532", "003765", "004069", "002656", "001631", "001455",
                "0600428", "0601020", "0600150", "0601989", "1002594",
                "159915", "159920", "159919", "159922", "159949", "159941", "159865", "159857", "159883", "159870", "159828", "159952", "159938", "159813", "159905", "159916", "159869", "159929", "159837", "159867", "159801", "159843", "159861", "159939", "159824", "159886", "159850", "159945", "159930", "159863", "159954", "159839", "159871"};

        try{
            String cacheKey = "checkview::data";
            String html = LocalCacheUtil.instance().getWithTtl(cacheKey, 1);
            if(html == null) {
                List<CheckView> views = quoteService.checkView(codes);
                html = quoteService.checkViewHtml(views);

                LocalCacheUtil.instance().set(cacheKey, html);
            }

            model.addAttribute("content", html);
        }catch (Exception e) {
            logger.error("checkview error.", e);
        }

        return "checkview";
    }


    static int profitYear = -2;

    private void profit(List<QuoteView> quoteViews, Profit profit, List<Profit> profitList) {

        // 保存3年队列值
        LinkedList<ProfitQueueBean> queue = new LinkedList<>();

        Date profitBeginTime = DateUtils.addYears(quoteViews.get(quoteViews.size()-1).getTime(), profitYear);

        for(QuoteView qv : quoteViews) {
            profit.price = qv.getPrice();
            profit.date = qv.getDate();

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

            if(qv.getTime().getTime() > profitBeginTime.getTime()) {
                if(qv.getBuyFlag() > 0) {
                    double avgBuyTime = queue.stream()
                            .filter(bean -> bean.type == 1)
                            .mapToInt(ProfitQueueBean::getCnt)
                            .average().orElse(0);
                    profit.buy(avgBuyTime);
                }else if(qv.getBuyFlag() < 0) {
                    double avgSellTime = queue.stream()
                            .filter(bean -> bean.type == 2)
                            .mapToInt(ProfitQueueBean::getCnt)
                            .average().orElse(0);
                    profit.sell(avgSellTime);
                }else {
                    profit.nothing();
                }

                profitList.add(profit.copy(qv));
            }
        }
    }
}