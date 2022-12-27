package com.waterflow.rich.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.waterflow.rich.bean.NoticeBean;
import com.waterflow.rich.grab.FundGrab;
import com.waterflow.rich.strategy.RetreatStrategy;
import com.waterflow.rich.strategy.RichBean;
import com.waterflow.rich.strategy.StdRichBean;
import com.waterflow.rich.strategy.StdStrategy;
import com.waterflow.rich.util.LocalCacheUtil;
import com.waterflow.rich.util.NumberFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FundService {

    @Autowired
    FundGrab fundGrab;

    @Autowired
    StdStrategy stdStrategy;

    @Autowired
    RetreatStrategy retreat;

    @Value(value="${quote.codes}")
    String quoteCodes;

    Logger logger = LoggerFactory.getLogger(FundService.class);

    public List<NoticeBean> fundTable(String[] fundCodes) {
        List<NoticeBean> noticeBeans = new ArrayList<>();
        Arrays.asList(fundCodes).stream().forEach(unit -> noticeBeans.add(output(unit)));

        List<NoticeBean> newNoticeBeans = noticeBeans.stream()
                .sorted(Comparator.comparing(notice -> notice.getMonthStd() - notice.getYearStd()))
                .collect(Collectors.toList());

        return newNoticeBeans;
    }

    private NoticeBean output(String fundCode) {
        NoticeBean noticeBean = new NoticeBean();
        try{
            String fundName = fundGrab.fundName(fundCode);
            StdRichBean month = lastRichBean(fundCode, 3);
            StdRichBean year = lastRichBean(fundCode, 12);

            double monthStdFrom = (month.getPrice() - month.getP2fsd()) / (month.getP2sd() - month.getP2fsd()) * 100;
            double yearStdFrom = (year.getPrice() - year.getP2fsd()) / (year.getP2sd() - year.getP2fsd()) * 100;

            double monthStd = NumberUtils.toScaledBigDecimal(monthStdFrom, Integer.valueOf(1), RoundingMode.HALF_UP).doubleValue();
            double yearStd = NumberUtils.toScaledBigDecimal(yearStdFrom, Integer.valueOf(1), RoundingMode.HALF_UP).doubleValue();

            List<RichBean> richBeans = fundGrab.autoGrabFundData(fundCode);
            retreat.setConfig(10, 140, 5);
            retreat.initRichBeans(richBeans);
            retreat.handleBaseData();
            retreat.dealStrategy();
            RichBean richBean = retreat.profit();

            double retreat = NumberUtils.toScaledBigDecimal(richBean.getRetreat(), Integer.valueOf(1), RoundingMode.HALF_UP).doubleValue();
            double maxRetreat = NumberUtils.toScaledBigDecimal(richBean.getMaxRetreat(), Integer.valueOf(1), RoundingMode.HALF_UP).doubleValue();

            String summary = String.format("%s，1ystd：%s%%，3mstd:%s%%, 回撤：%s/%s%%，回撤天数：%s/%s交易日",
                    fundName, monthStd, yearStd, retreat, maxRetreat, richBean.getRetreatDay(), richBean.getMaxRetreatDay());

            logger.info("summar is {}", summary);

            noticeBean.setFundCode(fundCode);
            noticeBean.setFundName(fundName);
            noticeBean.setMonthStd(monthStd);
            noticeBean.setYearStd(yearStd);
            noticeBean.setRetreat(retreat);
            noticeBean.setMaxRetreat(maxRetreat);
            noticeBean.setRetreatDay(richBean.getRetreatDay());
            noticeBean.setMaxRetreatDay(richBean.getMaxRetreatDay());
        }catch (Exception e) {
            logger.error("error", e);
        }
        return noticeBean;
    }

    public StdRichBean lastRichBean(String fundCode, int month) throws Exception{
        long diffTime = 1000L * 60 * 60 * 24 * 30 * month;

        List<RichBean> richBeans = fundGrab.autoGrabFundData(fundCode);
        stdStrategy.initRichBeans(richBeans);
        stdStrategy.handleBaseData();

        List<StdRichBean> stdRichBeans = stdStrategy.convert2Std(richBeans, diffTime);
        StdRichBean last = stdRichBeans.get(stdRichBeans.size()-1);
        return last;
    }

}
