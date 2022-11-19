package com.waterflow.rich.strategy;


import com.alibaba.fastjson.JSON;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseStrategy {

    Logger logger = LoggerFactory.getLogger(BaseStrategy.class);

    // 阿里云信息
    protected String endpoint = "";
    protected String accessKeyId = "";
    protected String accessKeySecret = "";
    protected String bucketName = "";

    // 要跳过计算的月份
    int skipTime = 12 * 2;

    protected List<RichBean> richBeans;

    // 初始化现金
    protected double initCash = 10 * 10000.0d;

    RichBean firstRichBean = null;

    Date lastBuyTime = null;
    Date lastSellTime = null;
    double maxRetreat = 0.0;
    double maxPrice = 0.0;

    /**
     * 处理排序
     * @param richBeans
     */
    public void initRichBeans(List<RichBean> richBeans) {
        if(richBeans == null || richBeans.size() <= 0) {
            return;
        }

        this.richBeans = richBeans.stream()
                .sorted((p1, p2) -> p1.getTime().compareTo(p2.getTime()))
                .collect(Collectors.toList());

        logger.info("init rich bean data. size is {}, first is {}, last is {}",
                richBeans.size(), richBeans.get(0).toSimpleString(),
                richBeans.get(richBeans.size()-1).toSimpleString());

        // init data
        firstRichBean = richBeans.get(0);
        lastBuyTime = firstRichBean.getTime();
        lastSellTime = firstRichBean.getTime();
        maxPrice = 0.0;
        maxRetreat = 0.0;

        initConfig();
    }

    /**
     * 处理基础字段
     */
    public void handleBaseData() {

        RichBean pre = null;
        double maxPrice = 0.0;

        richLoop: for(RichBean richBean : richBeans) {
            richBean.setPre(pre);

            // format time to string
            String timeFormat = DateFormatUtils.format(richBean.getTime(), "M/d/yyyy");
            richBean.setTimeFormat(timeFormat);

            // 定义昨天的数据
            if(pre == null) {
                pre = richBean;
                continue richLoop;
            }

            maxPrice = NumberUtils.max(maxPrice, richBean.getPrice());

            // 当前回撤
            double retreat = (maxPrice - richBean.getPrice()) / maxPrice * 100;
            richBean.setRetreat(retreat);

            pre = richBean;
        }
    }

    public void dealStrategy() {
        RichBean pre = firstRichBean;
        richLoop: for(RichBean richBean: richBeans) {
            // 记录数据
            int buyShare = 0;

            // 定义昨天的数据
            if(pre == firstRichBean) {
                deal(richBean, pre, buyShare);
                pre = richBean;
                continue richLoop;
            }

            // 跳过前3年，不买也不卖
            if(DateUtils.addMonths(firstRichBean.getTime(), skipTime).getTime() > richBean.getTime().getTime()) {
                deal(richBean, pre, buyShare);
                pre = richBean;
                continue richLoop;
            }

            maxRetreat = NumberUtils.max(maxRetreat, richBean.getRetreat());
            richBean.setMaxRetreat(maxRetreat);

            buyShare = calBuyShare(richBean, pre);

            deal(richBean, pre, buyShare);

            // 恢复游标数据
            if(buyShare > 0) {
                lastBuyTime = richBean.getTime();
            }
            if(buyShare < 0) {
                lastSellTime = richBean.getTime();
            }

            maxPrice = NumberUtils.max(maxPrice, richBean.getPrice());
            pre = richBean;
        }
    }

    public void deal(RichBean richBean, RichBean pre, int buyShare) {
        deal4Debug(richBean, pre, buyShare);

        if(pre == firstRichBean) {
            richBean.setCash(initCash);
            richBean.setShare(0);
            richBean.setBuyShare(0);
            richBean.setMarketValue(initCash);
        }else {
            richBean.setCash(pre.getCash());
            richBean.setShare(pre.getShare());
            richBean.setBuyShare(buyShare);
            double marketValue = richBean.getShare() * richBean.getPrice() + richBean.getCash();
            richBean.setMarketValue(marketValue);
        }

        if(pre != richBean && buyShare != 0) {

            // 买入的成本
            double buyPrice = buyShare * richBean.getPrice();

            // 买入时现金要足够，卖出时share要足够
            if(pre.getCash() - buyPrice < 0 || pre.getShare() + buyShare < 0) {
                logger.info("cash or share not enough, time is {}, buyShare is {}. buyPrice is {}, share is {}, cash is {}"
                        , richBean.getTime(), buyShare, buyPrice, richBean.getShare(), richBean.getCash());
                buyShare = 0;
                buyPrice = 0.0;
            }

            richBean.setCash(pre.getCash() - buyPrice);
            richBean.setShare(pre.getShare() + buyShare);
            richBean.setBuyShare(buyShare);
            richBean.setMarketValue(richBean.getPrice() * richBean.getShare() + richBean.getCash());

            if(buyShare > 0) {
                logger.info("deal suc, buy info: time is {}, share is {}, own share is {}, price is {}, cash is {}, market value is {}"
                        , DateFormatUtils.format(richBean.getTime(), "yyyyMMdd"), buyShare, richBean.getShare(), richBean.getPrice(), richBean.getCash(), richBean.getMarketValue());
            }
            if(buyShare < 0) {
                logger.info("deal suc, sell info: time is {}, share is {}, own share is {}, price is {}, cash is {}, market value is {}"
                        , DateFormatUtils.format(richBean.getTime(), "yyyyMMdd"), buyShare, richBean.getShare(), richBean.getPrice(), richBean.getCash(), richBean.getMarketValue());
            }
        }

//        pre = richBean;
    }

    public RichBean profit() {

        RichBean last = richBeans.get(richBeans.size()-1);

        logger.info("last is {}", last.toString());

        return last;
    }

    public void output2File(String fundCode) throws Exception{

        Date date = DateUtils.addMonths(richBeans.get(0).getTime(), skipTime);

        List<RichBean> outputRichBeans = richBeans.stream()
                .filter(richBean -> {
                    double newCash = NumberUtils.toScaledBigDecimal(richBean.getCash(), NumberUtils.INTEGER_ONE, RoundingMode.HALF_UP).doubleValue();
                    double newMarketValue = NumberUtils.toScaledBigDecimal(richBean.getMarketValue(), NumberUtils.INTEGER_ONE, RoundingMode.HALF_UP).doubleValue();
                    double newRetreat = NumberUtils.toScaledBigDecimal(richBean.getRetreat(), NumberUtils.INTEGER_ONE, RoundingMode.HALF_UP).doubleValue() * -1;
                    double newPrice = NumberUtils.toScaledBigDecimal(richBean.getPrice(), NumberUtils.INTEGER_ONE, RoundingMode.HALF_UP).doubleValue();
                    richBean.setCash(newCash);
                    richBean.setMarketValue(newMarketValue);
                    richBean.setRetreat(newRetreat);
                    richBean.setPrice(newPrice);
                    return richBean.getTime().getTime() - date.getTime() > 0;
                })
                .collect(Collectors.toList());

        File file = new File(filePath(fundCode));
//        FileUtils.writeByteArrayToFile(file, JSON.toJSONBytes(outputRichBeans, SerializerFeature.WriteNonStringValueAsString));
        FileUtils.writeByteArrayToFile(file, JSON.toJSONBytes(outputRichBeans));

        logger.info("output to file suc. file url is {}, size is {}", file.toURI(), file.length());
    }

    public void aliyunConfig(String key, String secret, String endPoint, String bucketName) {
        this.accessKeyId = key;
        this.accessKeySecret = secret;
        this.endpoint = endPoint;
        this.bucketName = bucketName;
    }

    /**
     * 生成json文件，上传到阿里云
     */
    public void upload(String fundCode) {
        String objectName = String.format("rich/%s.json", fundCode);
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            String toFilePath = filePath(fundCode);

            File toFile = new File(toFilePath);

            ossClient.deleteObject(bucketName, objectName);
            ossClient.putObject(bucketName, objectName, toFile);

            Date expiration = DateUtils.addDays(new Date(), 3);

            URL url = ossClient.generatePresignedUrl(bucketName, objectName, expiration);

            logger.info("result is {}", url);
        } catch (Exception e) {
            logger.error("aliyun oss error.", e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    public static String filePath(String fundCode) {
        String filePath = System.getProperty("file.path");
        if(StringUtils.isEmpty(filePath)) {
            filePath = System.getProperty("user.dir");
        }
        filePath = filePath + "/aliyun/" + fundCode + ".json";

        return filePath;
    }

    public abstract void deal4Debug(RichBean richBean, RichBean pre, int buyShare);

    public abstract int calBuyShare(RichBean richBean, RichBean pre);

    public abstract void initConfig();

}
