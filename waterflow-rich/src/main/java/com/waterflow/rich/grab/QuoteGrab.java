package com.waterflow.rich.grab;

import com.waterflow.common.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class QuoteGrab {

    Logger logger = LoggerFactory.getLogger(QuoteGrab.class);

    //demo url: https://quotes.money.163.com/service/chddata.html?code=0601398&start=20200720&end=20250508
    static String quoteUrl = "https://quotes.money.163.com/service/chddata.html?code=%s&start=%s&end=%s";

    public void downloadQuoteFile(String code, String beginDate, String endDate) {

        String filePath = System.getProperty("file.path");
        if(StringUtils.isEmpty(filePath)) {
            filePath = System.getProperty("user.dir");
        }
        filePath = filePath + "/quotes/" + code + ".data";

        if(!StringUtils.isNumeric(code)) {
            logger.info("quote code is valiable, code is {}", code);
            return;
        }

        String downloadUrl = String.format(quoteUrl, code, beginDate, endDate);
        logger.info("quote code is {}, download url is {}, local file path is {}", code, downloadUrl, filePath);

        HttpUtil.download(downloadUrl, filePath);
    }

}
