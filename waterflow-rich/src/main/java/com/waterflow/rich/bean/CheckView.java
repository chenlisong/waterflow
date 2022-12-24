package com.waterflow.rich.bean;


public class CheckView {

    private String code;

    private String name;

    private Double amp;

    private Double ampLast;

    private Double stdPer;

    /**
     * 1. 低于 -2，
     * 2. 在-2标准差到-1之间
     * 3. 在-1 到 + 1
     * 4. 在+1 到 +2
     * -1. 在+2 之上
     */
    private int level;

    private String levelView;

    public String skip() {
        int len = code.length() + name.length();
        String skip = "";
        if(len < 30) {
            for(int i=0; i<20-len; i++) {
                skip += "&#12288;";
            }
        }
        return skip;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getAmp() {
        return amp;
    }

    public void setAmp(Double amp) {
        this.amp = amp;
    }

    public Double getStdPer() {
        return stdPer;
    }

    public void setStdPer(Double stdPer) {
        this.stdPer = stdPer;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;

        if(level == -1) {
            this.levelView = "强烈卖出区间";
        }else if(level == 1) {
            this.levelView = "强烈买入区间";
        }else if(level == 2) {
            this.levelView = "-2std到-1std之间";
        }else if(level == 3) {
            this.levelView = "-1std到+1std之间";
        }else if(level == 4) {
            this.levelView = "+1std到+2std之间";
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevelView() {
        return levelView;
    }

    public void setLevelView(String levelView) {
        this.levelView = levelView;
    }

    public Double getAmpLast() {
        return ampLast;
    }

    public void setAmpLast(Double ampLast) {
        this.ampLast = ampLast;
    }
}
