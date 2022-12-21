package com.waterflow.rich.control;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.waterflow.rich.bean.Quote;
import com.waterflow.rich.bean.QuoteView;
import com.waterflow.rich.grab.QuoteGrab;
import com.waterflow.rich.service.QuoteService;
import com.waterflow.rich.strategy.StdStrategy;
import com.waterflow.rich.util.MetaAntvUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
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

            int diffTime = month * 20;
            stdStrategy.convert2CommonStd(quoteViews, diffTime);

            Date begin = DateUtils.addYears(new Date(), skipYear);
            quoteViews = quoteViews.stream()
                    .filter(bean -> bean.getTime().getTime() > begin.getTime())
                    .collect(Collectors.toList());

            quoteService.writeLink(quoteViews);
            quoteService.avgCommon(quoteViews, "price", "avg5Price", -1, 5);

            double stand = quoteViews.stream().min(Comparator.comparing(view -> view.getP2fsd()))
                            .get().getP2fsd();
            quoteService.signleDo(quoteViews, stand);

            quoteService.avgCommon(quoteViews, "vol", "avg5Vol", stand, 5);
            quoteService.avgCommon(quoteViews, "pressure", "avg5Pressure", stand, 5);

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
        }catch (Exception e) {
            logger.error("error.", e);
        }

        return "quote";
    }
}