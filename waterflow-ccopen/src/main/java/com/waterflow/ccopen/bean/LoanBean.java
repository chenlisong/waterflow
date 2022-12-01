package com.waterflow.ccopen.bean;

public class LoanBean {

    // 还款总额
    private Double totalMoney;

    // 贷款总额
    private Double principal;

    // 还款总利息
    private Double interest;

    // 每月还款金额
    private Double preLoan;

    // 还款期限
    private int month;

    // 第一个月还款金额
    private Double firstMonth;

    // 每月递减金额
    private Double decreaseMonth;

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

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
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
}
