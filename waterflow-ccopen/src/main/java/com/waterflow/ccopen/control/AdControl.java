package com.waterflow.ccopen.control;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.waterflow.ccopen.bean.AdLog;
import com.waterflow.ccopen.dao.AdLogDao;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@RequestMapping(value = "/ad")
public class AdControl {

    Logger logger = LoggerFactory.getLogger(AdControl.class);

    @Resource
    AdLogDao adLogDao;

    @GetMapping("/allow")
    public String allow(@RequestParam(value = "userId") String userId) {

        AdLog top = adLogDao.findTop(userId, 1);
        JSONObject result = new JSONObject();
        Date now = new Date();

        if(top == null || top.getId() == null) {
            result.put("allow", true);
            result.put("totast", "首次进入使用免除广告，VIP通道生效中");
            return JSON.toJSONString(result);
        }
        String diffSecondStr = DurationFormatUtils.formatPeriod(top.getAdTime().getTime(), now.getTime(),"s");
        Integer diffSecond = Integer.valueOf(diffSecondStr);
        if(diffSecond < 60 && diffSecond >= 0) {
            result.put("allow", true);
            result.put("totast", "60秒内免除广告，VIP通道生效中");
            return JSON.toJSONString(result);
        }

        result.put("allow", false);
        result.put("totast", "VIP通道已过期，看过广告后可使用");
        return JSON.toJSONString(result);
    }

    @GetMapping("/done")
    public String done(@RequestParam(value = "userId") String userId) {
        JSONObject result = new JSONObject();

        AdLog top = adLogDao.findTop(userId, 1);
        Date now = new Date();
        String diffSecondStr = DurationFormatUtils.formatPeriod(top.getAdTime().getTime(), now.getTime(),"s");
        Integer diffSecond = Integer.valueOf(diffSecondStr);
        if(diffSecond < 5) {
            result.put("status", 2);
            return JSON.toJSONString(result);
        }

        AdLog adLog = new AdLog();

        adLog.setUserId(userId);
        adLog.setPlatform(1);
        adLog.setReadStatus(2);
        adLog.setAdTime(new Date());
        adLogDao.save(adLog);


        result.put("status", 1);
        return JSON.toJSONString(result);
    }

}
