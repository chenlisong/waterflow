package com.waterflow.rich.bean;

public class NoticeBean {

    private String fundCode;

    private String fundName;

    private double monthStd;

    private double yearStd;

    private double retreat;

    private double maxRetreat;

    private int retreatDay;

    private int maxRetreatDay;

    public String toSummary () {
        String summary = String.format("%s，3mstd：%s%%，1ystd:%s%%, 回撤：%s/%s%%，回撤天数：%s/%s交易日",
                fundName, monthStd, yearStd, retreat, maxRetreat, retreatDay, maxRetreatDay);

        return summary;
    }

    public String toContent () {
        String summary = String.format("%s，3mstd：%s%%，1ystd:%s%%, 回撤：%s/%s%%，回撤天数：%s/%s交易日",
                fundName, monthStd, yearStd, retreat, maxRetreat, retreatDay, maxRetreatDay);

        String url = String.format(", <a href=\"http://47.109.105.18/fund/std?code=%s&month=3\">3m标准差</a>", fundCode);
        return summary + url;
    }

    public String getFundCode() {
        return fundCode;
    }

    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public double getMonthStd() {
        return monthStd;
    }

    public void setMonthStd(double monthStd) {
        this.monthStd = monthStd;
    }

    public double getYearStd() {
        return yearStd;
    }

    public void setYearStd(double yearStd) {
        this.yearStd = yearStd;
    }

    public double getRetreat() {
        return retreat;
    }

    public void setRetreat(double retreat) {
        this.retreat = retreat;
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
}
