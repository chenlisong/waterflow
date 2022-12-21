package com.waterflow.rich.strategy;

import java.math.BigDecimal;
import java.util.Date;

public class StdRichBean {

    private String date;

    private double price;

    private double p1sd;

    private double p2sd;

    private double p1fsd;

    private double p2fsd;

    private BigDecimal x;

    private Date time;

    private StdRichBean pre;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getP1sd() {
        return p1sd;
    }

    public void setP1sd(double p1sd) {
        this.p1sd = p1sd;
    }

    public double getP2sd() {
        return p2sd;
    }

    public void setP2sd(double p2sd) {
        this.p2sd = p2sd;
    }

    public double getP1fsd() {
        return p1fsd;
    }

    public void setP1fsd(double p1fsd) {
        this.p1fsd = p1fsd;
    }

    public double getP2fsd() {
        return p2fsd;
    }

    public void setP2fsd(double p2fsd) {
        this.p2fsd = p2fsd;
    }

    public BigDecimal getX() {
        return x;
    }

    public void setX(BigDecimal x) {
        this.x = x;
    }

    public StdRichBean getPre() {
        return pre;
    }

    public void setPre(StdRichBean pre) {
        this.pre = pre;
    }
}
