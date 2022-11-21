package com.waterflow.rich.grab;

import com.waterflow.common.util.HttpUtil;
import com.waterflow.rich.strategy.RichBean;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FundGrab {

    Logger logger = LoggerFactory.getLogger(FundGrab.class);

    static String fundUrl = "http://fund.eastmoney.com/pingzhongdata/%s.js";

    @Value("${project.file.path}")
    String projectFilePath;

    public String fundName(String fundCode) throws Exception {
        autoDownloadFundFile(fundCode);

        String fundName = null;
        //1. 加载本地数据，汇集成基础数据
        String dataFilePath = filePath(fundCode);
        File file = new File(dataFilePath);
        if(!file.isFile()) {
            return fundName;
        }
        String content = FileUtils.readFileToString(file, "utf-8");

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        engine.eval(content);

        fundName = (String) engine.get("fS_name");
        return fundName;
    }

    public List<RichBean> autoGrabFundData(String fundCode) throws Exception{
        //1. 加载本地数据，汇集成基础数据
        autoDownloadFundFile(fundCode);

        List<RichBean> richBeans = convertJsCode2RichBean(fundCode);

        richBeans = richBeans.stream()
                .sorted((p1, p2) -> p1.getTime().compareTo(p2.getTime()))
                .collect(Collectors.toList());

        return richBeans;
    }

    public void autoDownloadFundFile(String fundCode) {

        String dataFilePath = projectFilePath + "/fund/data/" + fundCode + ".js";

        long lastModify = new File(dataFilePath).lastModified();

        Date now = new Date();
        Date yesDay = DateUtils.addDays(now, -1);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.set(Calendar.HOUR, 15);
        calendar.set(Calendar.MONTH, 30);
        Date fundEndTime = calendar.getTime();

        Date fund1fEndTime = DateUtils.addDays(fundEndTime, -1);

        /**
         * 1. 超出一天，则重新下载
         * 2. 如果中间间隔今天3点，则下载
         * 3. 如果间隔昨天3点，则下载
         */
        if(lastModify < yesDay.getTime()
                || (lastModify < fundEndTime.getTime() && now.getTime() > fundEndTime.getTime())
                || (lastModify < fund1fEndTime.getTime() && now.getTime() > fund1fEndTime.getTime())) {
            String downloadUrl = String.format(fundUrl, fundCode);
            HttpUtil.download(downloadUrl, dataFilePath);
        }
    }

    public void downloadFundFile(String fundCode) {

        String filePath = filePath(fundCode);

        if(!StringUtils.isNumeric(fundCode) || fundCode.length() != 6) {
            logger.info("fund code is valiable, code is {}", fundCode);
            return;
        }

        String downloadUrl = String.format(fundUrl, fundCode);
        logger.info("fund code is {}, download url is {}, local file path is {}", fundCode, downloadUrl, filePath);

        HttpUtil.download(downloadUrl, filePath);
    }

    public List<RichBean> convertFile2Bean(String fundCode) throws Exception{
        List<RichBean> result = new ArrayList<>();

        String filePath = filePath(fundCode);
        File file = new File(filePath);
        if(!file.exists()) {
            logger.info("file not exist. file path is {}", filePath);
        }

        String content = FileUtils.readFileToString(file, "utf-8");

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        engine.eval(content);
        ScriptObjectMirror data = (ScriptObjectMirror)engine.get("Data_netWorthTrend");
        for(Object unitObject : data.values()) {
            ScriptObjectMirror unit = (ScriptObjectMirror) unitObject;

            Double xFloat = Double.parseDouble(unit.get("x").toString());
            Long x = Long.parseLong(new BigDecimal(xFloat).toString());
            Double y = Double.parseDouble(unit.get("y").toString());
//            logger.info("x is {}, and y is {}", x, y);
            result.add(new RichBean(new Date(x), y));
        }
//        JSONArray jsonArray = JSON.parseArray(data.toString());

        logger.info("data content is {}", data);
        return result;
    }

    public List<RichBean> convertJsCode2RichBean(String fundCode) throws Exception{
        List<RichBean> result = new ArrayList<>();
        String filePath = filePath(fundCode);

        File file = new File(filePath);
        if(!file.isFile()) {
            return result;
        }

        String content = FileUtils.readFileToString(file, "utf-8");

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        engine.eval(content);
        ScriptObjectMirror data = (ScriptObjectMirror)engine.get("Data_netWorthTrend");
        for(Object unitObject : data.values()) {
            ScriptObjectMirror unit = (ScriptObjectMirror) unitObject;

            Double xFloat = Double.parseDouble(unit.get("x").toString());
            Long x = Long.parseLong(new BigDecimal(xFloat).toString());
            Double y = Double.parseDouble(unit.get("y").toString());
//            logger.info("x is {}, and y is {}", x, y);
            result.add(new RichBean(new Date(x), y));
        }

        return result;
    }

    public String filePath(String fundCode) {
        String filePath = projectFilePath + "/fund/data/" + fundCode + ".js";
        return filePath;
    }

}
