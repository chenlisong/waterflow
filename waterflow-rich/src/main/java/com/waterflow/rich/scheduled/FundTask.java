package com.waterflow.rich.scheduled;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.waterflow.rich.grab.FundGrab;
import com.waterflow.rich.strategy.RetreatStrategy;
import com.waterflow.rich.strategy.RichBean;
import com.waterflow.rich.strategy.StdRichBean;
import com.waterflow.rich.strategy.StdStrategy;
import com.waterflow.rich.util.MsgUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FundTask {

    Logger logger = LoggerFactory.getLogger(FundTask.class);

    @Autowired
    private FundGrab fundGrab;

    @Autowired
    StdStrategy stdStrategy;

    @Autowired
    RetreatStrategy retreat;

//    @Scheduled(initialDelay = 1000 * 100000, fixedRate = 3000)
    @Scheduled(cron = "0 0 9 * * ?")
    public void scheduledTask() {

        try{
            String fundCode = "110020";

            String fundName = "沪深300";
            StdRichBean sixmonth = output(fundCode, 3);
            StdRichBean oneyear = output(fundCode, 12);

            double six = (sixmonth.getPrice() - sixmonth.getP2fsd()) / (sixmonth.getP2sd() - sixmonth.getP2fsd()) * 100;
            double one = (oneyear.getPrice() - oneyear.getP2fsd()) / (oneyear.getP2sd() - oneyear.getP2fsd()) * 100;

            double sixMonthStd = NumberUtils.toScaledBigDecimal(six, Integer.valueOf(1), RoundingMode.HALF_UP).doubleValue();
            double oneYearStd = NumberUtils.toScaledBigDecimal(one, Integer.valueOf(1), RoundingMode.HALF_UP).doubleValue();

            List<RichBean> richBeans = fundGrab.autoGrabFundData(fundCode);
            retreat.setConfig(10, 140, 5);
            retreat.initRichBeans(richBeans);
            retreat.handleBaseData();
            retreat.dealStrategy();
            RichBean richBean = retreat.profit();

            double retreat = NumberUtils.toScaledBigDecimal(richBean.getRetreat(), Integer.valueOf(1), RoundingMode.HALF_UP).doubleValue();
            double maxRetreat = NumberUtils.toScaledBigDecimal(richBean.getMaxRetreat(), Integer.valueOf(1), RoundingMode.HALF_UP).doubleValue();

            String summary = String.format("%s，1ystd：%s%%，3mstd:%s%%, 回撤：%s/%s%%", fundName, sixMonthStd, oneYearStd, retreat, maxRetreat);

            logger.info("summar is {}", summary);

            MsgUtil.sendWxNotice(summary, summary, "http://47.109.105.18/fund/std?code=110020&month=3");
        }catch (Exception e) {
            logger.error("error", e);
        }
    }

    public StdRichBean output(String fundCode, int month) throws Exception{
        long diffTime = 1000L * 60 * 60 * 24 * 30 * month;

        List<RichBean> richBeans = fundGrab.autoGrabFundData(fundCode);
        stdStrategy.initRichBeans(richBeans);
        stdStrategy.handleBaseData();

        List<StdRichBean> stdRichBeans = stdStrategy.convert2Std(richBeans, diffTime);
        StdRichBean last = stdRichBeans.get(stdRichBeans.size()-1);
        return last;
    }
}
