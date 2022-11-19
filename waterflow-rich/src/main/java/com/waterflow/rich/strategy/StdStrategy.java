package com.waterflow.rich.strategy;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * 基于回撤的算法
 */
@Service
public class StdStrategy extends BaseStrategy{

    Logger logger = LoggerFactory.getLogger(StdStrategy.class);

    @Override
    public int calBuyShare(RichBean richBean, RichBean pre) {
        return 0;
    }

    @Override
    public void handleBaseData() {
        super.handleBaseData();
    }

    public List<StdRichBean> convert2Std(List<RichBean> richBeans, long diffTime){

        List<StdRichBean> stdRichBeans = new ArrayList<>();
        for(int i=0; i<richBeans.size(); i++) {
//            logger.info("progress is {} / {}", i, richBeans.size());

            RichBean richBean = richBeans.get(i);
            StdRichBean stdRichBean = new StdRichBean();

            //1. 求平均值
            double sum = 0.0;
            int cnt = 0;
            RichBean tmp = richBean;
            while(tmp != null && tmp.getPre()!= null && richBean.getTime().getTime() - tmp.getTime().getTime() < diffTime) {
                cnt += 1;
                sum += tmp.getPrice();
                tmp = tmp.getPre();
            }

            double average = cnt > 0 ? sum/cnt : 0.0;
            tmp = richBean;
            sum = 0.0;
            while(tmp != null && tmp.getPre()!= null && richBean.getTime().getTime() - tmp.getTime().getTime() < diffTime) {
                sum += Math.pow(tmp.getPrice() - average, 2);
                tmp = tmp.getPre();
            }

            double std = cnt > 0 ? Math.sqrt(sum/(cnt-1)) : 0.0;

            double curPrice = richBean.getPrice();

            stdRichBean.setDate(richBean.getTimeFormat());
            stdRichBean.setPrice(curPrice);

            stdRichBean.setP1sd(average + std);
            stdRichBean.setP2sd(average + std + std);
            stdRichBean.setP1fsd(average - std);
            stdRichBean.setP2fsd(average - std - std);

//            stdRichBean.setP1sd(NumberUtils.toScaledBigDecimal(curPrice + std, Integer.valueOf(4), RoundingMode.HALF_UP).doubleValue());
//            stdRichBean.setP2sd(NumberUtils.toScaledBigDecimal(curPrice + std + std, Integer.valueOf(4), RoundingMode.HALF_UP).doubleValue());
//            stdRichBean.setP1fsd(NumberUtils.toScaledBigDecimal(curPrice - std, Integer.valueOf(4), RoundingMode.HALF_UP).doubleValue());
//            stdRichBean.setP1fsd(NumberUtils.toScaledBigDecimal(curPrice - std - std, Integer.valueOf(4), RoundingMode.HALF_UP).doubleValue());

            stdRichBeans.add(stdRichBean);
        }

        return stdRichBeans;
    }



    @Override
    public void deal4Debug(RichBean richBean, RichBean pre, int buyShare) {
        // code for debug
//        try{
//            long begin = DateUtils.parseDate("2019-08-23", "yyyy-MM-dd").getTime();
//            long end = DateUtils.parseDate("2019-08-30", "yyyy-MM-dd").getTime();
//
//            if(richBean.getTime().getTime() > begin && richBean.getTime().getTime() < end) {
//                logger.info("deal rich bean is {}", richBean.toString());
//            }
//        }catch (Exception e) {
//            logger.error("error.", e);
//        }
    }

    @Override
    public void initConfig() {
    }
}
