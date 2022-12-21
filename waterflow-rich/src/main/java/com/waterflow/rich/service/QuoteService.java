package com.waterflow.rich.service;

import com.waterflow.rich.bean.QuoteView;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class QuoteService {

    Logger logger = LoggerFactory.getLogger(QuoteService.class);

    public void writeLink(List<QuoteView> quoteViews) throws Exception{
        // 植入链路
        QuoteView pre = null;
        for(QuoteView quoteView: quoteViews) {
            quoteView.setPreQuoteView(pre);
            pre = quoteView;
        }
    }

    public void signleDo(List<QuoteView> quoteViews, double stand) throws Exception{
        for(int i=0;i <quoteViews.size(); i++) {
            QuoteView quoteView = quoteViews.get(i);
            double pressure = (quoteView.getClosePrice() - quoteView.getLowPrice() - (quoteView.getBeginPrice() - quoteView.getLowPrice()));// * quoteView.getVol();
            quoteView.setPressure(pressure);

            int buyflag = buyFlag(quoteView);
            quoteView.setBuyFlag(buyflag * stand / 4);
        }
    }

    /**
     * -1 标准差之下，突破5日均线或者突破-2标准差，执行买入
     * +1 标准差之上，突破5日均线或者突破+2标准差，执行卖出
     * @param quoteView
     * @return 0 未触发、1买入、-1卖出
     */
    private int buyFlag(QuoteView quoteView) {

        if(quoteView.getPreQuoteView() != null && quoteView.getPreQuoteView().getPreQuoteView() != null) {
            QuoteView pre = quoteView.getPreQuoteView();

            if(quoteView.getPrice() < quoteView.getP1fsd() && (
//                    (quoteView.getPrice() > quoteView.getAvg5Price() && pre.getPrice() < pre.getAvg5Price()) ||
                            (quoteView.getPrice() > quoteView.getP2fsd() && pre.getPrice() < pre.getP2fsd())) ) {
                return 1;
            }

            if(quoteView.getPrice() > quoteView.getP1sd() && (
                    (quoteView.getPrice() < quoteView.getAvg5Price() && pre.getPrice() > pre.getAvg5Price())
                            || (quoteView.getPrice() < quoteView.getP2sd() && pre.getPrice() > pre.getP2sd())) ) {
                return -1;
            }

//            if(prepre.getPrice() < prepre.getP2fsd() && quoteView.getPrice() > prepre.getP2fsd()) {
//                return 2;
//            }
//
//            if(prepre.getPrice() < prepre.getP1fsd() && quoteView.getPrice() > prepre.getP1fsd()) {
//                return 1;
//            }
//
//            if(prepre.getPrice() > prepre.getP2sd() && quoteView.getPrice() < prepre.getP2sd()) {
//                return -2;
//            }
//
//            if(prepre.getPrice() > prepre.getP1sd() && quoteView.getPrice() < prepre.getP1sd()) {
//                return -1;
//            }
        }

        return 0;
    }

    public void avgCommon(List<? extends Object> list, String fromFieldName,
                          String toFieldName, double stand, int queueLen) throws Exception{
        Class clazz = list.get(0).getClass();
        Field fromField = null;
        Field toField = null;

        while (clazz != null){
            for(Field tmp : clazz.getDeclaredFields()) {
                if(fromFieldName.equals(tmp.getName())) {
                    fromField = tmp;
                }else if(toFieldName.equals(tmp.getName())) {
                    toField = tmp;
                }
                tmp.setAccessible(true);
            }
            clazz = clazz.getSuperclass();
        }

        Double max = -1 * Double.MAX_VALUE;
        Double min = Double.MAX_VALUE;

        for(int i=0;i <list.size(); i++) {
            Object obj = list.get(i);
            double value = 0.0;
            if(fromField.getType().getName().equals("int")) {
                value = (int)fromField.get(obj) * 1.0;
            }else {
                value = (double)fromField.get(obj);
            }

            max = NumberUtils.max(value, max);
            min = NumberUtils.min(value, min);
        }

        max = NumberUtils.max(Math.abs(max), Math.abs(min));

        Double[] avgQueue = new Double[queueLen];

        for(int i=0;i <list.size(); i++) {
            Object obj = list.get(i);
            double value = 0.0;
            if(fromField.getType().getName().equals("int")) {
                value = (int)fromField.get(obj) * 1.0;
            }else {
                value = (double)fromField.get(obj);
            }

            avgQueue[i%avgQueue.length] = value;

            double avgValue = avgValue(avgQueue);
            if(stand > 0) {
                avgValue = stand / (max / avgValue);
            }
            toField.set(obj, avgValue);
        }
    }

    private double avgValue(Double[] values) {
        double result = 0;
        if(values == null || values.length <= 0) {
            return result;
        }

        double sum = 0;
        int cnt = 0;
        for(Double value: values) {
            if(value != null && value > 0) {
                cnt ++;
                sum += value;
            }
        }

        if(cnt > 0) {
            result = sum / cnt;
        }

        return result;
    }

}
