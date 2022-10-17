package com.waterflow.rich.strategy;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;


/**
 * 基于回撤的算法
 */
@Service
public class RetreatStrategy extends BaseStrategy{

    Logger logger = LoggerFactory.getLogger(RetreatStrategy.class);

    long buyDiffTime = 0L;

    long sellDiffTime = 0L;

    /**
     * 买入：如果回撤到达最大的1/2，买入1/10，距离上次买入最小间隔10天
     * 卖出：如果触摸到净值最高点，卖出1/10，距离上次卖出最小间隔10天
     */
    @Override
    public int calBuyShare(RichBean richBean, RichBean pre) {
        int buyShare = 0;

        double curPrice = pre.getPrice();
        double curCash = pre.getCash();
        int curShare = pre.getShare();

        if(richBean.getRetreat() >= maxRetreat * 0.5
            && richBean.getTime().getTime() - lastBuyTime.getTime() > buyDiffTime) {

            buyShare = (int)Math.floor((curShare * curPrice + curCash) * 0.1 / curPrice);

            // 买入的不能超出现金份额
            int maxBuyShare = NumberUtils.toScaledBigDecimal(curCash / curPrice, NumberUtils.INTEGER_ONE, RoundingMode.HALF_DOWN).intValue();

            buyShare = NumberUtils.min(buyShare, maxBuyShare);
        }

        if(richBean.getRetreat() < maxRetreat * 0.5 && richBean.getPrice() >= maxPrice
                && richBean.getTime().getTime() - lastSellTime.getTime() > sellDiffTime) {
            buyShare = (int)Math.floor((curShare * curPrice + curCash) * 0.1 / curPrice) * -1;

            // 卖出的不能超出拥有的
            buyShare = NumberUtils.max(buyShare, curShare * -1);
        }

        if(buyShare > 0) {
            lastBuyTime = richBean.getTime();
        }else if(buyShare < 0) {
            lastSellTime = richBean.getTime();
        }

        return buyShare;
    }

    @Override
    public void deal4Debug(RichBean richBean, RichBean pre, int buyShare) {
        // code for debug
        try{
            long begin = DateUtils.parseDate("2019-08-23", "yyyy-MM-dd").getTime();
            long end = DateUtils.parseDate("2019-08-30", "yyyy-MM-dd").getTime();

            if(richBean.getTime().getTime() > begin && richBean.getTime().getTime() < end) {
                logger.info("deal rich bean is {}", richBean.toString());
            }
        }catch (Exception e) {
            logger.error("error.", e);
        }
    }

    @Override
    public void initConfig() {
        this.buyDiffTime = 1000L * 60 * 60 * 24 * 15;
        this.sellDiffTime = 1000L * 60 * 60 * 24 * 30 * 2;
    }

    public void setConfig(int buyDiffDay, int sellDiffDay) {
        this.buyDiffTime = 1000L * 60 * 60 * 24 * buyDiffDay;
        this.sellDiffTime = 1000L * 60 * 60 * 24 * sellDiffDay;
    }
}
