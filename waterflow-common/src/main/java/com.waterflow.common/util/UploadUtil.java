package com.waterflow.common.util;

import org.apache.commons.lang3.time.DateUtils;

import java.io.File;
import java.net.URL;
import java.util.Date;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

public class UploadUtil {

    private String accessKeyId;

    private String accessKeySecret;

    private String endpoint;

    private String bucketName;

    private static UploadUtil uploadUtil = new UploadUtil();

    private UploadUtil(){}

    public static UploadUtil instance() {
        return uploadUtil;
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
    public void upload(String filePath, String objectName) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            File toFile = new File(filePath);

            ossClient.deleteObject(bucketName, objectName);
            ossClient.putObject(bucketName, objectName, toFile);

            Date expiration = DateUtils.addDays(new Date(), 3);

            URL url = ossClient.generatePresignedUrl(bucketName, objectName, expiration);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

}
