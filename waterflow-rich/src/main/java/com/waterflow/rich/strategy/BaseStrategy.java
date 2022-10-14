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
    int skipTime = 12 * 3;

    protected List<RichBean> richBeans;

    protected RichBean pre = null;

    protected double initCash = 10 * 1000.0d;

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
    }

    /**
     * 处理基础字段
     */
    public void handleBaseData() {

        RichBean pre = null;
        double maxPrice = 0.0;

        richLoop: for(RichBean richBean : richBeans) {

            // format time to string
            String timeFormat = DateFormatUtils.format(richBean.getTime(), "M/d/yyyy");
            richBean.setTimeFormat(timeFormat);

            // 定义昨天的数据
            if(pre == null) {
                pre = richBean;
                continue richLoop;
            }

            // 昨日回撤
            double yesterdayRetreat = (richBean.getPrice() - pre.getPrice()) / pre.getPrice() * 100;
            richBean.setYesterdayRetreat(yesterdayRetreat);

            maxPrice = NumberUtils.max(maxPrice, richBean.getPrice());

            // 当前回撤
            double retreat = (maxPrice - richBean.getPrice()) / maxPrice * 100;
            richBean.setRetreat(retreat);

            pre = richBean;
        }
    }

    public void profit() {

        RichBean last = richBeans.get(richBeans.size()-1);

        logger.info("last is {}", last.toString());
    }

    public void output2File(String fundCode) throws Exception{

        Date date = DateUtils.addMonths(richBeans.get(0).getTime(), skipTime);

        List<RichBean> outputRichBeans = richBeans.stream()
                .filter(richBean -> {
                    double newCash = NumberUtils.toScaledBigDecimal(richBean.getCash(), NumberUtils.INTEGER_ONE, RoundingMode.HALF_UP).doubleValue();
                    double newMarketValue = NumberUtils.toScaledBigDecimal(richBean.getMarketValue(), NumberUtils.INTEGER_ONE, RoundingMode.HALF_UP).doubleValue();
                    richBean.setCash(newCash);
                    richBean.setMarketValue(newMarketValue);
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

    public abstract void dealStrategy() ;

    public abstract void deal(RichBean richBean, int buyShare);

}
