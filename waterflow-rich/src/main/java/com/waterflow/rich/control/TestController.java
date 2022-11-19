package com.waterflow.rich.control;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class TestController {

    Logger logger = LoggerFactory.getLogger(TestController.class);

    @RequestMapping("/a1")
    public String test(){
        logger.info("a1 123");
        return "index";
    }

    @RequestMapping("/a2")
    public String test2(){
        logger.info("a2 123");
        return "canvas";
    }

    @RequestMapping("/a3")
    public String test3(Model model){
        logger.info("a3 exec...");

        List<JSONObject> list = new ArrayList<>();

        JSONObject bugfix = new JSONObject();
        bugfix.put("x", "1-1");
        bugfix.put("y", 0);
        bugfix.put("series", "t5");
        list.add(bugfix);

        Date time = new Date();
        for(int i=0;i<365 * 10; i++) {
            time = DateUtils.addDays(time, -1);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("x", DateFormatUtils.format(time, "yyyy-MM-dd"));
            jsonObject.put("y", i * 1.12d);
            jsonObject.put("series", "t1");
            list.add(jsonObject);
        }

        time = new Date();
        for(int i=0;i<365 * 10; i++) {
            time = DateUtils.addDays(time, -1);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("x", DateFormatUtils.format(time, "yyyy-MM-dd"));
            jsonObject.put("y", 365-i);
            jsonObject.put("series", "t2");
            list.add(jsonObject);
        }

        model.addAttribute("data", JSON.toJSONString(list));
        return "antv-line";
    }
}