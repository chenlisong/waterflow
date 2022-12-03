package com.waterflow.ccopen.bean;

public class RepayBean {

    // 原还款总额
    private Double totalMoney;

    // 贷款总额
    private Double principal;

    // 原还款总利息
    private Double interest;

    // 原每月还款金额
    private Double preLoan;

    //已还总金额
    private Double payTotal;

    // 已还本金
    private Double payLoan;

    // 已还利息
    private Double payInterest;

    // 一次性还清支付金额
    private Double totalPayAhead;

    // 节省利息
    private Double saveInterest;

    // 剩余还款期限
    private int months;

    // 最后还款月份
    private String lastPayMonth;

    // 第一个月还款金额 【等额本金】
    private Double firstMonth;

    // 每月递减还款额 【等额本金】
    private Double decreaseMonth;

    // 提前还款总额 【部分提前还款】
    private Double aheadTotalMoney;

    // 剩余还款总额 【部分提前还款】
    private Double leftTotalMoney;

    // 剩余还款总利息 【部分提前还款】
    private Double leftInterest;

    //剩余每月还款金额 【部分提前还款】
    private Double newPreLoan;

    // 剩余首月还款金额【部分提前还款、等额本金】
    private Double newFirstMonth;

    // 剩余月递减利息【部分提前还款、等额本金】
    private Double newDecreaseMonth;

    public Double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(Double totalMoney) {
        this.totalMoney = totalMoney;
    }

    public Double getPrincipal() {
        return principal;
    }

    public void setPrincipal(Double principal) {
        this.principal = principal;
    }

    public Double getInterest() {
        return interest;
    }

    public void setInterest(Double interest) {
        this.interest = interest;
    }

    public Double getPreLoan() {
        return preLoan;
    }

    public void setPreLoan(Double preLoan) {
        this.preLoan = preLoan;
    }

    public Double getPayTotal() {
        return payTotal;
    }

    public void setPayTotal(Double payTotal) {
        this.payTotal = payTotal;
    }

    public Double getPayLoan() {
        return payLoan;
    }

    public void setPayLoan(Double payLoan) {
        this.payLoan = payLoan;
    }

    public Double getPayInterest() {
        return payInterest;
    }

    public void setPayInterest(Double payInterest) {
        this.payInterest = payInterest;
    }

    public Double getTotalPayAhead() {
        return totalPayAhead;
    }

    public void setTotalPayAhead(Double totalPayAhead) {
        this.totalPayAhead = totalPayAhead;
    }

    public Double getSaveInterest() {
        return saveInterest;
    }

    public void setSaveInterest(Double saveInterest) {
        this.saveInterest = saveInterest;
    }

    public int getMonths() {
        return months;
    }

    public void setMonths(int months) {
        this.months = months;
    }

    public String getLastPayMonth() {
        return lastPayMonth;
    }

    public void setLastPayMonth(String lastPayMonth) {
        this.lastPayMonth = lastPayMonth;
    }

    public Double getFirstMonth() {
        return firstMonth;
    }

    public void setFirstMonth(Double firstMonth) {
        this.firstMonth = firstMonth;
    }

    public Double getDecreaseMonth() {
        return decreaseMonth;
    }

    public void setDecreaseMonth(Double decreaseMonth) {
        this.decreaseMonth = decreaseMonth;
    }

    public Double getAheadTotalMoney() {
        return aheadTotalMoney;
    }

    public void setAheadTotalMoney(Double aheadTotalMoney) {
        this.aheadTotalMoney = aheadTotalMoney;
    }

    public Double getLeftTotalMoney() {
        return leftTotalMoney;
    }

    public void setLeftTotalMoney(Double leftTotalMoney) {
        this.leftTotalMoney = leftTotalMoney;
    }

    public Double getLeftInterest() {
        return leftInterest;
    }

    public void setLeftInterest(Double leftInterest) {
        this.leftInterest = leftInterest;
    }

    public Double getNewPreLoan() {
        return newPreLoan;
    }

    public void setNewPreLoan(Double newPreLoan) {
        this.newPreLoan = newPreLoan;
    }

    public Double getNewFirstMonth() {
        return newFirstMonth;
    }

    public void setNewFirstMonth(Double newFirstMonth) {
        this.newFirstMonth = newFirstMonth;
    }

    public Double getNewDecreaseMonth() {
        return newDecreaseMonth;
    }

    public void setNewDecreaseMonth(Double newDecreaseMonth) {
        this.newDecreaseMonth = newDecreaseMonth;
    }
}
