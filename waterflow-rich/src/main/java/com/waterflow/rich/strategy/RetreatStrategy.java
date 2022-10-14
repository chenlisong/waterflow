package com.waterflow.rich.strategy;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 基于回撤的算法
 */
@Service
public class RetreatStrategy extends BaseStrategy{

    Logger logger = LoggerFactory.getLogger(RetreatStrategy.class);

    /**
     * 买入：如果回撤到达最大的1/2，买入1/10，距离上次买入最小间隔10天
     * 卖出：如果触摸到净值最高点，卖出1/10，距离上次卖出最小间隔10天
     */
    @Override
    public void dealStrategy() {
        long diffTime = 1000 * 60 * 60 * 24 * 10;
        RichBean firstRichBean = richBeans.get(0);

        Date lastBuyTime = firstRichBean.getTime();
        Date lastSellTime = firstRichBean.getTime();
        double maxRetreat = 0.0;
        double maxPrice = 0.0;

        richLoop: for(RichBean richBean: richBeans) {
            // 定义昨天的数据
            if(pre == null) {
                deal(richBean, 0);
                continue richLoop;
            }

            // 记录数据
            int buyShare = 0;

            // 跳过前3年，不买也不卖
            if(DateUtils.addMonths(firstRichBean.getTime(), skipTime).getTime() > richBean.getTime().getTime()) {
                deal(richBean, buyShare);
                continue richLoop;
            }

            maxRetreat = NumberUtils.max(maxRetreat, richBean.getRetreat());

            if(richBean.getRetreat() >= maxRetreat * 0.5
                && richBean.getTime().getTime() - lastBuyTime.getTime() > diffTime) {

                double curPrice = richBean.getPrice();
                double curCash = pre.getCash();
                int curShare = pre.getShare();

                buyShare = (int)Math.floor((curShare * curPrice + curCash) * 0.1 / curPrice);
            }

            if(richBean.getRetreat() < maxRetreat * 0.5 && richBean.getPrice() >= maxPrice
                && richBean.getTime().getTime() - lastSellTime.getTime() > diffTime) {
                double curPrice = richBean.getPrice();
                double curCash = pre.getCash();
                int curShare = pre.getShare();
                buyShare = (int)Math.floor((curShare * curPrice + curCash) * 0.1 / curPrice) * -1;
            }

            deal(richBean, buyShare);

            // 恢复游标数据
            if(buyShare > 0) {
                lastBuyTime = richBean.getTime();
            }
            if(buyShare < 0) {
                lastSellTime = richBean.getTime();
            }

            maxPrice = NumberUtils.max(maxPrice, richBean.getPrice());
        }
    }

    @Override
    public void deal(RichBean richBean, int buyShare) {
        // code for debug
        try{
            long begin = DateUtils.parseDate("2022-01-19", "yyyy-MM-dd").getTime();
            long end = DateUtils.parseDate("2022-01-22", "yyyy-MM-dd").getTime();

            if(richBean.getTime().getTime() > begin && richBean.getTime().getTime() < end) {
                logger.info("deal rich bean is {}", richBean.toString());
            }
        }catch (Exception e) {
            logger.error("error.", e);
        }

        if(pre == null) {
            richBean.setCash(initCash);
            richBean.setShare(0);
            richBean.setBuyShare(0);
            richBean.setMarketValue(initCash);
        }else {
            richBean.setCash(pre.getCash());
            richBean.setShare(pre.getShare());
            richBean.setBuyShare(pre.getBuyShare());
            double marketValue = richBean.getShare() * richBean.getPrice() + richBean.getCash();
            richBean.setMarketValue(marketValue);
        }

        if(pre != richBean && buyShare != 0) {

            // 买入的成本
            double buyPrice = buyShare * richBean.getPrice();

            // 买入时现金要足够，卖出时share要足够
            if(pre.getCash() - buyPrice < 0 || pre.getShare() + buyShare < 0) {
                logger.info("cash not enough, time is {}, buyShare is {}. buyPrice is {}"
                    , richBean.getTime(), buyShare, buyPrice);
                return;
            }

            richBean.setCash(pre.getCash() - buyPrice);
            richBean.setShare(pre.getShare() + buyShare);
            richBean.setBuyShare(buyShare);
            richBean.setMarketValue(richBean.getPrice() * richBean.getShare() + richBean.getCash());

            if(buyShare > 0) {
                logger.info("deal suc, buy info: time is {}, share is {}, own share is {}, price is {}, cash is {}, market value is {}"
                    , DateFormatUtils.format(richBean.getTime(), "yyyyMMdd"), buyShare, richBean.getShare(), richBean.getPrice(), richBean.getCash(), richBean.getMarketValue());
            }
            if(buyShare < 0) {
                logger.info("deal suc, sell info: time is {}, share is {}, own share is {}, price is {}, cash is {}, market value is {}"
                        , DateFormatUtils.format(richBean.getTime(), "yyyyMMdd"), buyShare, richBean.getShare(), richBean.getPrice(), richBean.getCash(), richBean.getMarketValue());
            }
        }

        pre = richBean;
    }

}
