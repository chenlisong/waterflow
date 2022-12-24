package com.waterflow.rich.bean;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.*;
import java.util.stream.Collectors;

public class BuyPoint {

    private Date time;

    private double price;

    private int last;

    private double amp;

    private double ampLast;

    public BuyPoint(Date time, double price, int last) {
        this.time = time;
        this.price = price;
        this.last = last;
    }

    public static double avgAmp(List<BuyPoint> list) {

        double avgAmp = list.stream()
                .sorted(Comparator.comparing(BuyPoint::getAmp).reversed())
                .mapToDouble(BuyPoint::getAmp)
                .limit(list.size() / 2)
                .average()
                .orElse(0);

        return avgAmp;
    }

    public static double avgAmpLast(List<BuyPoint> list) {

        double avgAmpLast = list.stream()
                .sorted(Comparator.comparing(BuyPoint::getAmpLast).reversed())
                .mapToDouble(BuyPoint::getAmpLast)
                .limit(list.size() / 2)
                .average()
                .orElse(0);

        return avgAmpLast;
    }

    public static String output(List<BuyPoint> list) {

        if(list == null || list.size() <= 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("</br></br>");

        int max = list.stream().mapToInt(BuyPoint::getLast).max().getAsInt();
        int min = list.stream().mapToInt(BuyPoint::getLast).min().getAsInt();

        String common = String.format("在-2std之下区域. 总计次数： %s, 最长持续时间： %s, 最短： %s", list.size(), max, min);
        sb.append(common).append("</br></br>");

        double avgAmp = avgAmp(list);
        double avgAmpLast = avgAmpLast(list);
        String common2 = String.format("在-2std之上，涨幅情况。平均涨幅：%s, 平均涨幅持续时间：%s",
                String.format("%.2f", avgAmp),
                String.format("%.2f", avgAmpLast));
        sb.append(common2).append("</br></br>");

        for(BuyPoint point : list) {
            String line = String.format("buy point is %s, last day is %s, amp: %s, amp last: %s </br>",
                    DateFormatUtils.format(point.getTime(), "yyyy-MM-dd"),
                    point.getLast(),
                    String.format("%.2f", point.getAmp()),
                    point.getAmpLast());
            sb.append(line);
        }
        return sb.toString();
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getLast() {
        return last;
    }

    public void setLast(int last) {
        this.last = last;
    }

    public double getAmp() {
        return amp;
    }

    public void setAmp(double amp) {
        this.amp = amp;
    }

    public double getAmpLast() {
        return ampLast;
    }

    public void setAmpLast(double ampLast) {
        this.ampLast = ampLast;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
