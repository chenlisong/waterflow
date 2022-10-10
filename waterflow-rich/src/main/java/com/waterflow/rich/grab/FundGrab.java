package com.waterflow.rich.grab;

import com.waterflow.common.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FundGrab {

    Logger logger = LoggerFactory.getLogger(FundGrab.class);

    static String fundUrl = "http://fund.eastmoney.com/pingzhongdata/%s.js";

    public void downloadFundFile(String fundCode) {

        String filePath = System.getProperty("file.path");
        if(StringUtils.isEmpty(filePath)) {
            filePath = System.getProperty("user.dir");
        }
        filePath = filePath + "/fund/" + fundCode + ".data";

        if(!StringUtils.isNumeric(fundCode) || fundCode.length() != 6) {
            logger.info("fund code is valiable, code is {}", fundCode);
            return;
        }

        String downloadUrl = String.format(fundUrl, fundCode);
        logger.info("code is {}, download url is {}, local file path is {}", fundCode, downloadUrl, filePath);

        HttpUtil.download(downloadUrl, filePath);
    }

    public void convertFile2Bean() {

    }

    public void writeData2DB() {

    }

}
