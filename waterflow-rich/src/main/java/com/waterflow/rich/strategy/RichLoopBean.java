package com.waterflow.rich.strategy;

public class RichLoopBean extends RichBean{

    private int diffBuyTime;

    private int diffSellTime;

    public void convert2Loop(RichBean richBean) {
        setMarketValue(richBean.getMarketValue());
    }

    public int getDiffBuyTime() {
        return diffBuyTime;
    }

    public void setDiffBuyTime(int diffBuyTime) {
        this.diffBuyTime = diffBuyTime;
    }

    public int getDiffSellTime() {
        return diffSellTime;
    }

    public void setDiffSellTime(int diffSellTime) {
        this.diffSellTime = diffSellTime;
    }

    @Override
    public String toString() {
        return "RichLoopBean{" +
                "diffBuyTime=" + diffBuyTime +
                ", diffSellTime=" + diffSellTime +
                ", marketValue=" + getMarketValue() +
                '}';
    }
}
