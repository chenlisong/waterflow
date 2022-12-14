package com.waterflow.rich.strategy;


import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.util.Date;

public class RichBean {

    @JSONField(serialize = false)
    // 时间 init
    private Date time;

    @JSONField(name="Date", ordinal = 1)
    // 时间 init
    private String timeFormat;

    // 净值 init
    @JSONField(name="price", ordinal = 2, deserializeUsing = ToStringSerializer.class)
    private double price;

//    @JSONField(serialize = true)
//    // 昨日回撤 base
//    private double yesterdayRetreat;

    @JSONField(serialize = true)
    // 回撤 base
    private double retreat;

    // 最大回撤
    private double maxRetreat;

    @JSONField(serialize = true)
    // 拥有的份额 buy strategy
    private int share;

    @JSONField(serialize = true, ordinal = 4)
    // 买入份额 buy strategy
    private int buyShare;

    @JSONField(serialize = true, ordinal = 5)
    // 现金头寸 buy strategy
    private double cash;

    @JSONField(name="marketValue", ordinal = 3, serializeUsing = ToStringSerializer.class)
    // 市值
    private double marketValue;

    // 回撤持续时间，单位：天
    private int retreatDay;

    // 回撤持续时间，单位：天
    private int maxRetreatDay;

    private RichBean pre;

    public RichBean() {
    }

    public RichBean(Date time, double price) {
        this.time = time;
        this.price = price;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

//    public double getYesterdayRetreat() {
//        return yesterdayRetreat;
//    }
//
//    public void setYesterdayRetreat(double yesterdayRetreat) {
//        this.yesterdayRetreat = yesterdayRetreat;
//    }

    public double getCash() {
        return cash;
    }

    public void setCash(double cash) {
        this.cash = cash;
    }

    public int getShare() {
        return share;
    }

    public void setShare(int share) {
        this.share = share;
    }

    public int getBuyShare() {
        return buyShare;
    }

    public void setBuyShare(int buyShare) {
        this.buyShare = buyShare;
    }

    public double getRetreat() {
        return retreat;
    }

    public void setRetreat(double retreat) {
        this.retreat = retreat;
    }

    public double getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(double marketValue) {
        this.marketValue = marketValue;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public RichBean getPre() {
        return pre;
    }

    public void setPre(RichBean pre) {
        this.pre = pre;
    }

    public double getMaxRetreat() {
        return maxRetreat;
    }

    public void setMaxRetreat(double maxRetreat) {
        this.maxRetreat = maxRetreat;
    }

    public int getRetreatDay() {
        return retreatDay;
    }

    public void setRetreatDay(int retreatDay) {
        this.retreatDay = retreatDay;
    }

    public int getMaxRetreatDay() {
        return maxRetreatDay;
    }

    public void setMaxRetreatDay(int maxRetreatDay) {
        this.maxRetreatDay = maxRetreatDay;
    }

    @Override
    public String toString() {
        return "RichBean{" +
                "time=" + time +
                ", price=" + price +
//                ", yesterdayRetreat=" + yesterdayRetreat +
                ", retreat=" + retreat +
                ", share=" + share +
                ", buyShare=" + buyShare +
                ", cash=" + cash +
                ", marketValue=" + marketValue +
                '}';
    }

    public String toSimpleString() {
        return "RichBean{" +
                "time=" + time +
                ", price=" + price +
                '}';
    }
}
