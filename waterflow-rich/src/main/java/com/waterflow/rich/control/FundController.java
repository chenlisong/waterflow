package com.waterflow.rich.control;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.waterflow.rich.grab.FundGrab;
import com.waterflow.rich.strategy.RetreatStrategy;
import com.waterflow.rich.strategy.RichBean;
import com.waterflow.rich.strategy.StdRichBean;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/fund")
public class FundController {

    Logger logger = LoggerFactory.getLogger(FundController.class);

    @Autowired
    FundGrab fundGrab;

    @Autowired
    RetreatStrategy retreat;

    @Autowired
    StdStrategy stdStrategy;

    @RequestMapping("/std")
    public String std(Model model, @RequestParam(value = "code", defaultValue = "110020") String fundCode,
                      @RequestParam(value = "month", defaultValue = "12") int month) {
        logger.info("fund std exec, code is {}...", fundCode);

        List<RichBean> richBeans = null;
        try{
            richBeans = fundGrab.autoGrabFundData(fundCode);

            retreat.initRichBeans(richBeans);
            retreat.handleBaseData();

            long diffTime = 1000L * 60 * 60 * 24 * 30 * month;

            Date begin = DateUtils.addYears(new Date(), -1);
            List<StdRichBean> stdRichBeans = stdStrategy.convert2Std(richBeans, diffTime);
            stdRichBeans = stdRichBeans.stream()
                    .filter(bean -> bean.getTime().getTime() > begin.getTime())
                    .collect(Collectors.toList());

            List<JSONObject> jsonObjects = MetaAntvUtil.convert2Antv(stdRichBeans, "date", "price", "p1sd", "p1fsd", "p2sd", "p2fsd");
            List<JSONObject> list = new ArrayList<>();

            JSONObject bugfix = new JSONObject();
            bugfix.put("x", "1-1");
            bugfix.put("y", 0);
            bugfix.put("series", "t1");
            list.add(bugfix);

            list.addAll(jsonObjects);
            model.addAttribute("data", JSON.toJSONString(list));

            String fundName = fundGrab.fundName(fundCode);
            model.addAttribute("fundName", fundName + "/" + fundCode);
        }catch (Exception e) {
            logger.error("error.", e);
        }

        return "antv-line";
    }

    @RequestMapping("/std/diff")
    public String stdDiff(Model model, @RequestParam(value = "code", defaultValue = "110020") String fundCode,
                      @RequestParam(value = "shortmonth", defaultValue = "3") int shortmonth,
                      @RequestParam(value = "longmonth", defaultValue = "12") int longmonth) {
        logger.info("fund std diff exec, code is {}...", fundCode);

        try{
            List<StdRichBean> stdRichBeans = outputRichBeans(fundCode, shortmonth);
            List<StdRichBean> stdRichBeansLong = outputRichBeans(fundCode, longmonth);

            List<JSONObject> jsonObjects = MetaAntvUtil.convert2Antv(stdRichBeans, "date", "price", "p1sd", "p1fsd", "p2sd", "p2fsd");
            List<JSONObject> jsonObjectsLong = MetaAntvUtil.convert2AntvWithAppend(stdRichBeansLong, "_long", "date", "p1sd", "p1fsd", "p2sd", "p2fsd");
            jsonObjects.addAll(jsonObjectsLong);
            List<JSONObject> list = new ArrayList<>();

            JSONObject bugfix = new JSONObject();
            bugfix.put("x", "1-1");
            bugfix.put("y", 0);
            bugfix.put("series", "t1");
            list.add(bugfix);

            list.addAll(jsonObjects);
            model.addAttribute("data", JSON.toJSONString(list));

            String fundName = fundGrab.fundName(fundCode);
            model.addAttribute("fundName", fundName + "/" + fundCode);
        }catch (Exception e) {
            logger.error("error.", e);
        }

        return "antv-line-diff";
    }

    private List<StdRichBean> outputRichBeans(String fundCode, int month) {
        List<StdRichBean> stdRichBeans = null;
        try{
            List<RichBean> richBeans = fundGrab.autoGrabFundData(fundCode);

            retreat.initRichBeans(richBeans);
            retreat.handleBaseData();

            long diffTime = 1000L * 60 * 60 * 24 * 30 * month;

            Date begin = DateUtils.addYears(new Date(), -5);
            stdRichBeans = stdStrategy.convert2Std(richBeans, diffTime);
            stdRichBeans = stdRichBeans.stream()
                    .filter(bean -> bean.getTime().getTime() > begin.getTime())
                    .collect(Collectors.toList());
        }catch (Exception e) {
            logger.error("error.", e);
        }
        return stdRichBeans;
    }
}