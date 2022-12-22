package com.waterflow.rich.service;

import com.waterflow.rich.bean.CheckView;
import com.waterflow.rich.bean.Quote;
import com.waterflow.rich.bean.QuoteView;
import com.waterflow.rich.grab.FundGrab;
import com.waterflow.rich.grab.QuoteGrab;
import com.waterflow.rich.strategy.RichBean;
import com.waterflow.rich.strategy.StdStrategy;
import com.waterflow.rich.util.LocalCacheUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuoteService {

    @Autowired
    QuoteGrab quoteGrab;

    @Autowired
    FundGrab fundGrab;

    @Autowired
    StdStrategy stdStrategy;

    Logger logger = LoggerFactory.getLogger(QuoteService.class);

    public void writeLink(List<QuoteView> quoteViews) throws Exception{
        // 植入链路
        QuoteView preView = null;

        for(QuoteView quoteView: quoteViews) {
            quoteView.setPreQuoteView(preView);
            quoteView.setPre(preView);

            preView = quoteView;
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

    public void signleDo(List<QuoteView> quoteViews) throws Exception{
        for(int i=0;i <quoteViews.size(); i++) {
            QuoteView quoteView = quoteViews.get(i);
            double pressure = (quoteView.getClosePrice() - quoteView.getLowPrice() - (quoteView.getBeginPrice() - quoteView.getLowPrice()));// * quoteView.getVol();
            quoteView.setPressure(pressure);

            int buyflag = buyFlag(quoteView);
            quoteView.setBuyFlag(buyflag);
        }
    }

    public String outputBuyPoint(List<QuoteView> quoteViews) throws Exception{
        LinkedHashMap<Date, Integer> timeMap = new LinkedHashMap<>();
        StringBuilder sb = new StringBuilder("");

        Date in = null;
        int cnt = 0;
        for(QuoteView qv: quoteViews) {
            QuoteView pre = qv.getPreQuoteView();
            if(pre == null) continue;

            if(pre.getPrice() > pre.getP2fsd() && qv.getPrice() < qv.getP2fsd()) {
                in = qv.getTime();
            }
            if(in != null && pre.getPrice() < pre.getP2fsd() && qv.getPrice() > qv.getP2fsd()) {
                timeMap.put(in, cnt);
                in = null;
                cnt = 0;
            }

            if(in != null) {
                cnt ++;
            }
        }

        int max = 0;
        int min = Integer.MAX_VALUE;
        for(Date inTmp : timeMap.keySet()) {
            max = NumberUtils.max(timeMap.get(inTmp), max);
            min = NumberUtils.min(timeMap.get(inTmp), min);
        }

        sb.append("</br></br>");
        String common = String.format("total time is %s, max is %s, min is %s", timeMap.size(), max, min);
        sb.append(common).append("</br></br>");

        for(Date inTmp : timeMap.keySet()) {
            String line = String.format("buy point is %s, last day is %s </br>",
                    DateFormatUtils.format(inTmp, "yyyy-MM-dd"), timeMap.get(inTmp));
            sb.append(line);
        }

        return sb.toString();
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

            if(quoteView.getPrice() > quoteView.getP2fsd() && pre.getPrice() < pre.getP2fsd()) {
                return 1;
            }

//            if(quoteView.getPrice() > pre.getP1sd() &&
//                    (quoteView.getPrice() < pre.getAvg5Price() && pre.getPrice() > pre.getAvg5Price())) {
//                return -1;
//            }

            if(quoteView.getPrice() < quoteView.getP2sd() && pre.getPrice() > pre.getP2sd()) {
                return -1;
            }
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

    public void stand(List<? extends Object> list, String fromFieldName, double stand) throws Exception{
        Class clazz = list.get(0).getClass();
        Field fromField = null;

        while (clazz != null){
            for(Field tmp : clazz.getDeclaredFields()) {
                if(fromFieldName.equals(tmp.getName())) {
                    fromField = tmp;
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

        for(int i=0;i <list.size(); i++) {
            Object obj = list.get(i);
            double value = 0.0;
            if(fromField.getType().getName().equals("int")) {
                value = (int)fromField.get(obj) * 1.0;
            }else {
                value = (double)fromField.get(obj);
            }

            if(stand > 0) {
                value = stand / (max / value);
            }
            fromField.set(obj, value);
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

    public String checkViewHtml(List<CheckView> views) {

        StringBuilder sb = new StringBuilder();

        String timeFormat = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH");

        sb.append("</br></br>每日标准差分析")
                .append(" ")
                .append(timeFormat)
                .append("</br></br>");
        for(CheckView view: views) {
            String line = String.format("%s:%s/%s, std: %s, amp: %s", view.getLevelView(), view.getName()
                , view.getCode(), view.getStdPer(), view.getAmp());

            String href = null;
            if(view.getCode().length() == 6) {
                href = String.format(", <a href=\"http://rich.ccopen.top/quote/fund/std?skipYear=-1&code=%s&month=8\">8m标准差</a>", view.getCode());
            }else {
                href = String.format(", <a href=\"http://rich.ccopen.top/quote/std?skipYear=-1&code=%s&month=2\">2m标准差</a>", view.getCode());
            }

            sb.append(line)
                    .append(href)
                    .append("</br>");
        }

        return sb.toString();
    }

    public List<CheckView> checkView(String[] codes) throws Exception{
        List<CheckView> views = new ArrayList<>();
        for(String code: codes) {
            double amp = avgAmp(code);
            CheckView view = new CheckView();
            view.setCode(code);
            view.setAmp(amp);

            QuoteView last = lastWithStd(code);

            double stdPer = (last.getPrice() - last.getP2fsd()) / last.getP2fsd();
            view.setStdPer(stdPer);
            if(last.getPrice() < last.getP2fsd()) {
                view.setLevel(1);
            }else if(last.getPrice() < last.getP1fsd()) {
                view.setLevel(2);
            }else if(last.getPrice() < last.getP1sd()) {
                view.setLevel(3);
            }else if(last.getPrice() < last.getP2sd()) {
                view.setLevel(4);
            }else {
                view.setLevel(-1);
            }

            String name = "";
            if(code.length() == 6) {
                name = fundGrab.fundName(code);
            }else {
                name = quoteGrab.quoteName(code);
            }
            view.setName(name);

            views.add(view);
        }

        Collections.sort(views, new Comparator<CheckView>() {
            @Override
            public int compare(CheckView o1, CheckView o2) {
                int diff = o1.getLevel() - o2.getLevel();
                if(diff > 0 ) {
                    return 1;
                }else if(diff < 0) {
                    return -1;
                }
                double diffAmp = o1.getAmp() - o2.getAmp();
                if(diffAmp > 0) {
                    return -1;
                }else {
                    return 1;
                }
            }
        });

        return views;
    }

    private QuoteView lastWithStd(String code) throws Exception{
        List<QuoteView> quoteViews = null;
        if(code.length() != 6) {
            quoteGrab.downloadQuoteFile(code);
            List<Quote> quotes = quoteGrab.findAll(code);

            quoteViews = quotes.stream()
                    .map(quote -> {
                        return QuoteView.convert(quote);
                    })
                    .collect(Collectors.toList());
        }else {
            List<RichBean> richBeans = fundGrab.autoGrabFundData(code);
            quoteViews = richBeans.stream()
                    .map(richBean -> {
                        return QuoteView.convert(richBean);
                    })
                    .collect(Collectors.toList());
        }

        writeLink(quoteViews);

        int diffTime = 8 * 20;

        if(code.length() != 6) {
            diffTime = 2 * 20;
        }

        stdStrategy.convert2CommonStd(quoteViews, diffTime);

        return quoteViews.get(quoteViews.size()-1);
    }

    /**
     * 求近3年平均振幅
     * @param code
     * @return
     * @throws Exception
     */
    public double avgAmp(String code) throws Exception{
        List<QuoteView> quoteViews = null;
        if(code.length() != 6) {
            quoteGrab.downloadQuoteFile(code);
            List<Quote> quotes = quoteGrab.findAll(code);

            quoteViews = quotes.stream()
                    .map(quote -> {
                        return QuoteView.convert(quote);
                    })
                    .collect(Collectors.toList());
        }else {
            List<RichBean> richBeans = fundGrab.autoGrabFundData(code);
            quoteViews = richBeans.stream()
                    .map(richBean -> {
                        return QuoteView.convert(richBean);
                    })
                    .collect(Collectors.toList());
        }

        Date now = new Date();
        Date begin = DateUtils.addYears(now, -3);
        Date end = DateUtils.addYears(begin, 1);

        List<Double> amp = new ArrayList<>();

        double max = 0;
        double min = Double.MAX_VALUE;

        for(QuoteView qv : quoteViews) {
            long time = qv.getTime().getTime();
            double price = qv.getPrice();
            if(time < begin.getTime()) {
                continue;
            }else if(time > begin.getTime() && time < end.getTime()) {
                max = NumberUtils.max(price, max);
                min = NumberUtils.min(price, min);
            }else if(time > end.getTime()) {
                if(max > 0 && min != Double.MAX_VALUE) {
                    amp.add((max - min)/min);
                }
                begin = DateUtils.addYears(begin, 1);
                end = DateUtils.addYears(end, 1);
                max = 0;
                min = Double.MAX_VALUE;
            }
        }

        return amp.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
    }

}
