package com.waterflow.rich.grab;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.waterflow.common.util.HttpUtil;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;

@Service
public class FundGrab {

    Logger logger = LoggerFactory.getLogger(FundGrab.class);

    static String fundUrl = "http://fund.eastmoney.com/pingzhongdata/%s.js";

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

    public void convertFile2Bean(String fundCode) throws Exception{

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

//        JSONArray jsonArray = JSON.parseArray(data.toString());

        logger.info("data content is {}", data);
    }

    public void writeData2DB() {

    }

    private String filePath(String fundCode) {
        String filePath = System.getProperty("file.path");
        if(StringUtils.isEmpty(filePath)) {
            filePath = System.getProperty("user.dir");
        }
        filePath = filePath + "/fund/" + fundCode + ".data";

        return filePath;
    }

}
