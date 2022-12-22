package com.waterflow.rich.bean;

import java.util.Date;

public class ProfitQueueBean {

    public Date time;

    public int type;//1 买入，-1卖出

    public int cnt;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public ProfitQueueBean(Date time, int type, int cnt) {
        this.type = type;
        this.time = time;
        this.cnt = cnt;
    }
}
