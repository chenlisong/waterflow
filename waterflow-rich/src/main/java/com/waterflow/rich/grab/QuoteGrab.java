package com.waterflow.rich.grab;

import com.waterflow.common.util.HttpUtil;
import com.waterflow.rich.bean.Quote;
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
public class QuoteGrab {

    Logger logger = LoggerFactory.getLogger(QuoteGrab.class);

    @Value("${project.file.path}")
    String projectFilePath;

    //demo url: https://quotes.money.163.com/service/chddata.html?code=0601398&start=20200720&end=20250508
//    static String quoteUrl = "https://quotes.money.163.com/service/chddata.html?code=%s&start=%s&end=%s";
    static String quoteUrl = "http://quotes.money.163.com/service/chddata.html?code=%s&start=%s&end=%s&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;VOTURNOVER;VATURNOVER";

    public void downloadQuoteFile(String code) {
        Date now = new Date();
        Date begin = DateUtils.addYears(now, -10);
        Date end = DateUtils.addDays(now, 1);

        String endStr = DateFormatUtils.format(end, "yyyyMMdd");
        String beginStr = DateFormatUtils.format(begin, "yyyyMMdd");

        downloadQuoteFile(code, beginStr, endStr);
    }

    public void downloadQuoteFile(String code, String beginDate, String endDate) {

        String filePath = projectFilePath + "/quotes/" + code + ".csv";

        if(!StringUtils.isNumeric(code)) {
            logger.info("quote code is valiable, code is {}", code);
            return;
        }

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
            String downloadUrl = String.format(quoteUrl, code, beginDate, endDate);
            logger.info("quote code is {}, download url is {}, local file path is {}", code, downloadUrl, filePath);
            HttpUtil.download(downloadUrl, filePath);
        }
    }

    public List<Quote> findAll(String code) throws Exception{

        String filePath = projectFilePath + "/quotes/" + code + ".csv";
        List<String> lines = FileUtils.readLines(new File(filePath), "GBK");

        List<Quote> result = new ArrayList<>();
        lineLoop : for(String line: lines) {
            String[] lineSplit = line.split(",");
            if(lineSplit[0] != null && lineSplit[0].equals("日期"))
                continue lineLoop;

            String date = lineSplit[0];
            double closePrice = Double.parseDouble(lineSplit[3]);
            double highPrice = Double.parseDouble(lineSplit[4]);
            double lowPrice = Double.parseDouble(lineSplit[5]);
            double beginPrice = Double.parseDouble(lineSplit[6]);
            int vol = Integer.parseInt(lineSplit[10]);

            Quote quote = new Quote();
            quote.setDate(date);
            quote.setTime(DateUtils.parseDate(date, "yyyy-MM-dd"));

            quote.setClosePrice(closePrice);
            quote.setHighPrice(highPrice);
            quote.setLowPrice(lowPrice);
            quote.setBeginPrice(beginPrice);
            quote.setVol(vol);
            quote.setPrice(closePrice);
            result.add(quote);
        }

        result = result.stream()
                .sorted(Comparator.comparing(Quote::getTime))
                .filter(quote -> quote.getClosePrice() > 0)
                .collect(Collectors.toList());

//        Quote pre = null;
//        for(Quote quote: result) {
//            quote.setPre(pre);
//            pre = quote;
//        }

        return result;
    }

    public String quoteName(String code) throws Exception{

        String filePath = projectFilePath + "/quotes/" + code + ".csv";
        List<String> lines = FileUtils.readLines(new File(filePath), "GBK");

        if(lines != null && lines.size() > 1) {
            return lines.get(1).split(",")[2];
        }
        return "未知";
    }

}
