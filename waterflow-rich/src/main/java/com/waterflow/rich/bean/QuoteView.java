package com.waterflow.rich.bean;

import com.alibaba.fastjson2.JSON;
import com.waterflow.rich.strategy.RichBean;
import org.apache.commons.lang3.time.DateFormatUtils;

public class QuoteView extends Quote{

    private double avg5Price;

    private double avg5Vol;

    private double volView;

    private QuoteView preQuoteView;

    // 买方压力 - 卖方压力
    private double pressure;

    private double avg5Pressure;

    private double buyFlag;

    public double getAvg5Price() {
        return avg5Price;
    }

    public void setAvg5Price(double avg5Price) {
        this.avg5Price = avg5Price;
    }

    public double getAvg5Vol() {
        return avg5Vol;
    }

    public void setAvg5Vol(double avg5Vol) {
        this.avg5Vol = avg5Vol;
    }

    public double getVolView() {
        return volView;
    }

    public void setVolView(double volView) {
        this.volView = volView;
    }

    public QuoteView getPreQuoteView() {
        return preQuoteView;
    }

    public void setPreQuoteView(QuoteView preQuoteView) {
        this.preQuoteView = preQuoteView;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getAvg5Pressure() {
        return avg5Pressure;
    }

    public void setAvg5Pressure(double avg5Pressure) {
        this.avg5Pressure = avg5Pressure;
    }

    public double getBuyFlag() {
        return buyFlag;
    }

    public void setBuyFlag(double buyFlag) {
        this.buyFlag = buyFlag;
    }

    public QuoteView () {}

    public static QuoteView convert (Quote quote) {
        byte[] bytes = JSON.toJSONBytes(quote);
        return JSON.parseObject(bytes, QuoteView.class);
    }

    public static QuoteView convert (RichBean richBean) {
        QuoteView qv = new QuoteView();
        double price = richBean.getPrice();
        qv.setHighPrice(price);
        qv.setPrice(price);
        qv.setClosePrice(price);
        qv.setBeginPrice(price);
        qv.setLowPrice(price);
        qv.setDate(DateFormatUtils.format(richBean.getTime(), "yyyy-MM-dd"));
        qv.setTime(richBean.getTime());
        return qv;
    }
}
