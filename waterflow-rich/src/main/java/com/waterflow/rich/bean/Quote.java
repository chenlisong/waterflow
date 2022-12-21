package com.waterflow.rich.bean;

import com.waterflow.rich.grab.QuoteGrab;
import com.waterflow.rich.strategy.StdRichBean;

public class Quote extends StdRichBean {
    // 收盘价
    private Double closePrice;

    // 最高价
    private Double highPrice;

    // 最低价
    private Double lowPrice;

    // 开盘价
    private Double beginPrice;

    // 成交量
    private int vol;

    public Double getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(Double closePrice) {
        this.closePrice = closePrice;
    }

    public Double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(Double highPrice) {
        this.highPrice = highPrice;
    }

    public Double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(Double lowPrice) {
        this.lowPrice = lowPrice;
    }

    public Double getBeginPrice() {
        return beginPrice;
    }

    public void setBeginPrice(Double beginPrice) {
        this.beginPrice = beginPrice;
    }

    public int getVol() {
        return vol;
    }

    public void setVol(int vol) {
        this.vol = vol;
    }

}
