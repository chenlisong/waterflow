package com.waterflow.rich.strategy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 基于回撤的算法
 */
@Service
public class Retreat {
    String endpoint = "";
    String accessKeyId = "";
    String accessKeySecret = "";
    String bucketName = "";

    int skipTime = 12 * 3;
    // 填写文件完整路径。文件完整路径中不能包含Bucket名称。

    Logger logger = LoggerFactory.getLogger(Retreat.class);

    private List<RichBean> richBeans;

    private RichBean pre = null;

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

    /**
     * 买入：如果回撤到达最大的1/2，买入1/10，距离上次买入最小间隔10天
     * 卖出：如果触摸到净值最高点，卖出1/10，距离上次卖出最小间隔10天
     */
    public void dealStrategy() {

        // 现金10w
        double cash = 100000.0;

        long diffTime = 1000 * 60 * 60 * 24 * 10;


        RichBean firstRichBean = richBeans.get(0);

        Date lastBuyTime = firstRichBean.getTime();
        Date lastSellTime = firstRichBean.getTime();
        double maxRetreat = 0.0;
        double maxPrice = 0.0;

        richLoop: for(RichBean richBean: richBeans) {
            // 定义昨天的数据
            if(pre == null) {
                deal(richBean, 0, cash);
                continue richLoop;
            }

            // 记录数据
            int buyShare = 0;

            // 跳过前3年，不买也不卖
            if(DateUtils.addMonths(firstRichBean.getTime(), skipTime).getTime() > richBean.getTime().getTime()) {
                deal(richBean, buyShare, cash);
                continue richLoop;
            }

            maxRetreat = NumberUtils.max(maxRetreat, richBean.getRetreat());

            if(richBean.getRetreat() >= maxRetreat * 0.5
                && richBean.getTime().getTime() - lastBuyTime.getTime() > diffTime) {

                double curPrice = richBean.getPrice();
                double curCash = pre.getCash();
                int curShare = pre.getShare();

                buyShare = (int)Math.floor((curShare * curPrice + curCash) * 0.1 / curPrice);
            }

            if(richBean.getRetreat() < maxRetreat * 0.5 && richBean.getPrice() >= maxPrice
                && richBean.getTime().getTime() - lastSellTime.getTime() > diffTime) {
                double curPrice = richBean.getPrice();
                double curCash = pre.getCash();
                int curShare = pre.getShare();
                buyShare = (int)Math.floor((curShare * curPrice + curCash) * 0.1 / curPrice) * -1;
            }

            deal(richBean, buyShare, cash);

            // 恢复游标数据
            if(buyShare > 0) {
                lastBuyTime = richBean.getTime();
            }
            if(buyShare < 0) {
                lastSellTime = richBean.getTime();
            }

            maxPrice = NumberUtils.max(maxPrice, richBean.getPrice());
        }
    }

    private void deal(RichBean richBean, int buyShare, double initCash) {
        // code for debug
        try{
            long begin = DateUtils.parseDate("2022-01-19", "yyyy-MM-dd").getTime();
            long end = DateUtils.parseDate("2022-01-22", "yyyy-MM-dd").getTime();

            if(richBean.getTime().getTime() > begin && richBean.getTime().getTime() < end) {
                logger.info("deal rich bean is {}", richBean.toString());
            }
        }catch (Exception e) {
            logger.error("error.", e);
        }

        if(pre == null) {
            richBean.setCash(initCash);
            richBean.setShare(0);
            richBean.setBuyShare(0);
            richBean.setMarketValue(initCash);
        }else {
            richBean.setCash(pre.getCash());
            richBean.setShare(pre.getShare());
            richBean.setBuyShare(pre.getBuyShare());
            double marketValue = richBean.getShare() * richBean.getPrice() + richBean.getCash();
            richBean.setMarketValue(marketValue);
        }

        if(pre != richBean && buyShare != 0) {

            // 买入的成本
            double buyPrice = buyShare * richBean.getPrice();

            // 买入时现金要足够，卖出时share要足够
            if(pre.getCash() - buyPrice < 0 || pre.getShare() + buyShare < 0) {
                logger.info("cash not enough, time is {}, buyShare is {}. buyPrice is {}"
                    , richBean.getTime(), buyShare, buyPrice);
                return;
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

        pre = richBean;
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

}
