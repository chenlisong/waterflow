package com.waterflow.rich.control;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.waterflow.rich.grab.FundGrab;
import com.waterflow.rich.strategy.RetreatStrategy;
import com.waterflow.rich.strategy.RichBean;
import com.waterflow.rich.strategy.StdRichBean;
import com.waterflow.rich.strategy.StdStrategy;
import com.waterflow.rich.util.MetaAntvUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

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
    public String std(Model model, @RequestParam(value = "code") String fundCode) {
        logger.info("fund std exec, code is {}...", fundCode);

        List<RichBean> richBeans = null;
        try{
            richBeans = fundGrab.autoGrabFundData(fundCode);

            retreat.initRichBeans(richBeans);
            retreat.handleBaseData();

            int month = 6;
            long diffTime = 1000L * 60 * 60 * 24 * 30 * month;
            List<StdRichBean> stdRichBeans = stdStrategy.convert2Std(richBeans, diffTime);

            List<JSONObject> jsonObjects = MetaAntvUtil.convert2Antv(stdRichBeans, "date", "price", "p1sd", "p1fsd", "p2sd", "p2fsd");
            List<JSONObject> list = new ArrayList<>();

            JSONObject bugfix = new JSONObject();
            bugfix.put("x", "1-1");
            bugfix.put("y", 0);
            bugfix.put("series", "t1");
            list.add(bugfix);

            list.addAll(jsonObjects);
            model.addAttribute("data", JSON.toJSONString(list));

        }catch (Exception e) {
            logger.error("error.", e);
        }

        return "antv-line";
    }
}