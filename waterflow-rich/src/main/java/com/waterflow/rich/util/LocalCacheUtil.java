package com.waterflow.rich.util;

import com.waterflow.common.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LocalCacheUtil {

    Map<String, String> cacheData = new HashMap<>();
    Map<String, Long> cacheTtl = new HashMap<>();

    static String TTL = "::ttltime";

    static LocalCacheUtil util = new LocalCacheUtil();

    private LocalCacheUtil(){}

    public static LocalCacheUtil instance() {
        return util;
    }

    public void set(String key, String value) {
        cacheData.put(key, value);
        cacheTtl.put(key + TTL, new Date().getTime());
    }

    public String get(String key) {
        return cacheData.get(key);
    }

    public String getWithTtl(String key, int hour) {
        Long ttlTime = cacheTtl.get(key + TTL);
        long nowTime = DateUtils.addHours(new Date(), -1 * hour).getTime();

        if(cacheData.get(key) == null) {
            return null;
        }

        if(ttlTime != null && ttlTime > nowTime) {
            return cacheData.get(key);
        }
        remove(key);

        return null;
    }

    public String getWithTradeTime(String key) {
        String value = cacheData.get(key);
        Long ttl = cacheTtl.get(key + TTL);

        if(value == null) {
            return null;
        }

        if(ttl == null) {
            remove(key);
            value = null;
        }else if(!validTradeTime(ttl)) {
            remove(key);
            value = null;
        }

        return value;
    }

    public void remove(String key) {
        cacheData.remove(key);
        cacheTtl.remove(key + TTL);
    }

    /**
     * 针对这个时间，当前时刻下，是否还有效
     * @param lastModify true：有效、false：无效
     * @return
     */
    public boolean validTradeTime(long lastModify) {

        Date now = new Date();
        Date yesDay = DateUtils.addDays(now, -1);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 30);
        Date fundEndTime = calendar.getTime();

        Date fund1fEndTime = DateUtils.addDays(fundEndTime, -1);

        /**
         * 1. 超出一天，则重新下载
         * 2. 如果中间间隔今天3点，则下载
         * 3. 如果间隔昨天3点，则下载
         */
        if(lastModify < yesDay.getTime()
                || (lastModify < fundEndTime.getTime() && now.getTime() > fundEndTime.getTime())
                || (lastModify < fund1fEndTime.getTime() && now.getTime() > fund1fEndTime.getTime())) {
            return false;
        }
        return true;
    }

}
