package com.waterflow.rich.bean;

import org.apache.commons.lang3.math.NumberUtils;

public class Profit {

    public double cash;

    public int stock;

    public double price;

    // view data
    public String date;

    public double all;

    public int buyFlag;

    public int suc;

    public Profit(double cash, int stock) {
        this.cash = cash;
        this.stock = stock;
    }

    public Profit() {
    }

    public Profit copy(QuoteView qv) {
        Profit newProfit = new Profit();
        newProfit.cash = cash;
        newProfit.stock = stock;
        newProfit.price = price;
        newProfit.date = qv.getDate();
        newProfit.all = cash + stock * price;
        newProfit.buyFlag = buyFlag;
        newProfit.suc = suc;
        return newProfit;
    }

    public void buy(double per) {
        double all = cash + stock * price;

        double oneTimeMoney = all * per;
        oneTimeMoney = NumberUtils.min(cash, oneTimeMoney);

        int buyStock = (int) Math.floor(oneTimeMoney / price);

        if(buyStock > 0) {
            stock = stock + buyStock;
            cash = cash - buyStock * price;
            suc = 2;
        }

        buyFlag = 1;
    }

    public void sell(double per) {
        double all = cash + stock * price;

        double oneTimeMoney = all * per;

        int sellStock = (int) Math.floor(oneTimeMoney / price);
        sellStock = NumberUtils.min(sellStock, stock);

        if(sellStock > 0) {
            stock = stock - sellStock;
            cash = cash + sellStock * price;
            suc = -2;
        }

        buyFlag = -1;
    }

    public void nothing() {
        buyFlag = 0;
        suc = 0;
    }

}
