package com.waterflow.rich.grab;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.waterflow.common.util.HttpUtil;
import com.waterflow.rich.bean.BuyPoint;
import com.waterflow.rich.bean.Quote;
import com.waterflow.rich.bean.QuoteView;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HkGrab {

    Logger logger = LoggerFactory.getLogger(HkGrab.class);

    @Value("${project.file.path}")
    String projectFilePath;

    static String quoteUrl = "http://ali-stock.showapi.com/realtime-k?code=%s&beginDay=%s&time=day&type=bfq&AppCode=543c83ec3f0b4978a833447a15fa8079";

    public void downloadQuoteFile(String code) {
        Date now = new Date();
        Date begin = DateUtils.addYears(now, -10);

        String beginStr = DateFormatUtils.format(begin, "yyyyMMdd");

        downloadHkFile(code, beginStr);
    }

    public void downloadHkFile(String code, String beginDate) {

        String filePath = projectFilePath + "/hk/" + code + ".json";

        long lastModify = new File(filePath).lastModified();

        Date now = new Date();
        Date yesDay = DateUtils.addDays(now, -1);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 0);
        Date endTime = calendar.getTime();

        Date fund1fEndTime = DateUtils.addDays(endTime, -1);

        /**
         * 1. 超出一天，则重新下载
         * 2. 如果中间间隔今天3点，则下载
         * 3. 如果间隔昨天3点，则下载
         */
        if(lastModify < yesDay.getTime()
                || (lastModify < endTime.getTime() && now.getTime() > endTime.getTime())
                || (lastModify < fund1fEndTime.getTime() && now.getTime() > fund1fEndTime.getTime())) {
            String downloadUrl = String.format(quoteUrl, code, beginDate);
            logger.info("quote code is {}, download url is {}, local file path is {}", code, downloadUrl, filePath);
            HttpUtil.downloadWithHeader(downloadUrl, filePath);
        }
    }

    public List<QuoteView> findAll(String code) throws Exception{
        downloadQuoteFile(code);
        String filePath = projectFilePath + "/hk/" + code + ".json";

        String fileContent = FileUtils.readFileToString(new File(filePath), "utf-8");

        JSONObject jsonObject = JSON.parseObject(fileContent);

        if(jsonObject == null || jsonObject.get("showapi_res_body") == null) {
            return null;
        }

        List<QuoteView> views = new ArrayList<>();
        JSONArray dataList = jsonObject.getJSONObject("showapi_res_body").getJSONArray("dataList");

        logger.info("first data is : {}", JSON.toJSONString(dataList.get(0)));
        logger.info("last data is : {}", JSON.toJSONString(dataList.get(dataList.size()-1)));

        for(int i=0; i<dataList.size(); i++) {
            JSONObject unit = dataList.getJSONObject(i);
            double price = unit.getDouble("close");
            String date = unit.getString("time");
            double vol = unit.getDouble("volumn");

            QuoteView qv = new QuoteView();
            qv.setHighPrice(price);
            qv.setPrice(price);
            qv.setClosePrice(price);
            qv.setBeginPrice(price);
            qv.setLowPrice(price);
            qv.setDate(date);
            qv.setTime(DateUtils.parseDate(date, "yyyyMMdd"));
            views.add(qv);
        }

        views = views.stream()
                .sorted(Comparator.comparing(QuoteView::getTime))
                .collect(Collectors.toList());
        return views;
    }

    public String hkName(String code) throws Exception{
        downloadQuoteFile(code);
        String filePath = projectFilePath + "/hk/" + code + ".json";

        String fileContent = FileUtils.readFileToString(new File(filePath), "utf-8");

        JSONObject jsonObject = JSON.parseObject(fileContent);

        if(jsonObject == null || jsonObject.get("showapi_res_body") == null) {
            return null;
        }

        List<QuoteView> views = new ArrayList<>();
        String name = jsonObject.getJSONObject("showapi_res_body").getString("name");

       return name;
    }

}
