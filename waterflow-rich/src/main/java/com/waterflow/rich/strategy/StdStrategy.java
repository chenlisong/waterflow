package com.waterflow.rich.strategy;


import cn.hutool.core.date.DateUnit;
import com.alibaba.fastjson.JSON;
import com.waterflow.rich.bean.Quote;
import com.waterflow.rich.bean.QuoteView;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
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
            stdRichBean.setTime(richBean.getTime());

//            stdRichBean.setP1sd(NumberUtils.toScaledBigDecimal(curPrice + std, Integer.valueOf(4), RoundingMode.HALF_UP).doubleValue());
//            stdRichBean.setP2sd(NumberUtils.toScaledBigDecimal(curPrice + std + std, Integer.valueOf(4), RoundingMode.HALF_UP).doubleValue());
//            stdRichBean.setP1fsd(NumberUtils.toScaledBigDecimal(curPrice - std, Integer.valueOf(4), RoundingMode.HALF_UP).doubleValue());
//            stdRichBean.setP1fsd(NumberUtils.toScaledBigDecimal(curPrice - std - std, Integer.valueOf(4), RoundingMode.HALF_UP).doubleValue());

            stdRichBeans.add(stdRichBean);
        }

        return stdRichBeans;
    }

    public void convert2CommonStd(List<? extends StdRichBean> beans, int diffTime){

        for(int i=0; i<beans.size(); i++) {

            StdRichBean richBean = beans.get(i);

            //1. 求平均值
            double sum = 0.0;
            int cnt = 0;
            StdRichBean tmp = richBean;

            int tmpDiffTime = diffTime;
            while(tmp != null && tmp.getPre()!= null && tmpDiffTime-- > 0) {
                cnt += 1;
                sum += tmp.getPrice();
                tmp = tmp.getPre();
            }

            double average = cnt > 0 ? sum/cnt : 0.0;
            tmp = richBean;
            sum = 0.0;

            tmpDiffTime = diffTime;
            while(tmp != null && tmp.getPre()!= null && tmpDiffTime-- > 0) {
                sum += Math.pow(tmp.getPrice() - average, 2);
                tmp = tmp.getPre();
            }

            double std = cnt > 0 ? Math.sqrt(sum/(cnt-1)) : 0.0;

            richBean.setP1sd(average + std);
            richBean.setP2sd(average + std + std);
            richBean.setP1fsd(average - std);
            richBean.setP2fsd(average - std - std);
        }
    }

    public String breachView(List<QuoteView> beans, int diffTime){
        StringBuilder sb = new StringBuilder();
        StdRichBean bean = breach(beans, diffTime);
        sb.append("<br/>")
                .append("预测明日承压线，time: " + bean.getDate())
                .append(", price: " + bean.getPrice())
                .append(", std content: ");

        String line = String.format("-2std: %s, -1std: %s, +1std: %s, +2std: %s",
                String.format("%.2f", bean.getP2fsd()),
                String.format("%.2f", bean.getP1fsd()),
                String.format("%.2f", bean.getP1sd()),
                String.format("%.2f", bean.getP2sd()));

        sb.append(line)
                .append("<br/><br/>");

        return sb.toString();
    }

    public StdRichBean breach(List<QuoteView> beans, int diffTime){

        int size = beans.size();

        if(size > 2 * diffTime) {
            beans = beans.subList(size - 2 * diffTime, size);
        }

        // 初始化现有std
        convert2CommonStd(beans, diffTime);
        StdRichBean last = beans.get(beans.size() - 1);
        double x = 0.003 * last.getPrice();

        int cnt = 0;
        double newP2fsd = 0;
        double newP1fsd = 0;
        double newP1sd = 0;
        double newP2sd = 0;

        breanchLoop: while(true) {
            if(newP2fsd > 0 && newP1fsd > 0 && newP1sd > 0 && newP2sd > 0) {
                break breanchLoop;
            }

            QuoteView append = new QuoteView();
            append.setTime(DateUtils.addDays(last.getTime(), 1));
            append.setDate(DateFormatUtils.format(append.getTime(), "yyyy-MM-dd"));
            append.setPrice(last.getPrice() + x * cnt);
            append.setPre(last);
            beans.add(append);
            convert2CommonStd(beans, diffTime);
            QuoteView newLast = beans.get(beans.size()-1);

            beans = beans.subList(0, beans.size()-1);

            // -2std up
            if(newP2fsd == 0 && (last.getPrice() < last.getP2fsd() && newLast.getPrice() > newLast.getP2fsd())) {
                newP2fsd = newLast.getPrice();
            }

            // -2std down
            if(newP2fsd == 0 && (last.getPrice() > last.getP2fsd() && newLast.getPrice() < newLast.getP2fsd())) {
                newP2fsd = newLast.getPrice();
            }

            // -1std up
            if(newP1fsd == 0 && (last.getPrice() < last.getP1fsd() && newLast.getPrice() > newLast.getP1fsd())) {
                newP1fsd = newLast.getPrice();
            }

            // -1std down
            if(newP1fsd == 0 && (last.getPrice() > last.getP1fsd() && newLast.getPrice() < newLast.getP1fsd())) {
                newP1fsd = newLast.getPrice();
            }

            // +1std up
            if(newP1sd == 0 && (last.getPrice() < last.getP1sd() && newLast.getPrice() > newLast.getP1sd())) {
                newP1sd = newLast.getPrice();
            }

            // +1std down
            if(newP1sd == 0 && (last.getPrice() > last.getP1sd() && newLast.getPrice() < newLast.getP1sd())) {
                newP1sd = newLast.getPrice();
            }

            // +2std up
            if(newP2sd == 0 && (last.getPrice() < last.getP2sd() && newLast.getPrice() > newLast.getP2sd())) {
                newP2sd = newLast.getPrice();
            }

            // +2std down
            if(newP2sd == 0 && (last.getPrice() > last.getP2sd() && newLast.getPrice() < newLast.getP2sd())) {
                newP2sd = newLast.getPrice();
            }

            if(cnt == 0) {
                cnt = 1;
            }else if(cnt > 0) {
                cnt = cnt * -1;
            }else if(cnt < 0) {
                cnt = cnt * -1 + 1;
            }
        }

        StdRichBean result = new StdRichBean();
        result.setTime(DateUtils.addDays(last.getTime(), 1));
        result.setDate(DateFormatUtils.format(result.getTime(), "yyyy-MM-dd"));
        result.setPrice(last.getPrice());
        result.setP2fsd(newP2fsd);
        result.setP1fsd(newP1fsd);
        result.setP1sd(newP1sd);
        result.setP2sd(newP2sd);

        return result;
    }

//    public static void main(String[] args) {
//        List list = new ArrayList();
//        list.add("1"); // 0
//        list.add("2"); // 1
//        list.add("3"); // 2
//        list.add("4"); // 3
//        System.out.println(JSON.toJSONString(list.subList(0,3)));
//        System.out.println(JSON.toJSONString(list.subList(1,2)));
//    }

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
