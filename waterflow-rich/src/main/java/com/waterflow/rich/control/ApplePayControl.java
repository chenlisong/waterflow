package com.waterflow.rich.control;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;


@RestController
@RequestMapping("applePay")
public class ApplePayControl {

    //购买凭证验证地址
    private static final String certificateUrl = "https://buy.itunes.apple.com/verifyReceipt";

    //测试的购买凭证验证地址
    private static final String certificateUrlTest = "https://sandbox.itunes.apple.com/verifyReceipt";

    Logger logger = LoggerFactory.getLogger(ApplePayControl.class);

    /**
     * 重写X509TrustManager
     */
    private static TrustManager myX509TrustManager = new X509TrustManager() {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
    };

    /**
     * 接收自己APP客户端发过来的购买凭证
     *
     * @param userId    用户ID
     * @param receipt   苹果传递前端给的值
     * @param chooseEnv 是否时测试环境
     */
    @GetMapping("/setIapCertificate")
    public String setIapCertificate(@RequestParam(value = "userId") String userId, @RequestParam(value = "receipt") String receipt, @RequestParam(value = "chooseEnv", defaultValue = "false") boolean chooseEnv) {
            //log.info("IOS端发送的购买凭证。数据有 userId = {},receipt = {},chooseEnv = {}",userId,receipt,chooseEnv);
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(receipt)) {
            return "用户ID 或者 receipt为空";
        }
        String url = null;
        url = chooseEnv == true ? certificateUrl : certificateUrlTest;
        final String certificateCode = receipt;
        if (StringUtils.isNotEmpty(certificateCode)) {
            String s = sendHttpsCoon(url, certificateCode, userId);
            if ("支付成功".equals(s)) {
                return s;
            } else {
                return s;
            }
        } else {
            return "receipt 为空!";
        }
    }

    /**
     * 发送请求 向苹果发起验证支付请求是否有效：本方法有认证方法进行调用
     *
     * @param url  支付的环境校验
     * @param code 接口传递的 receipt
     * @return 结果
     */
    private String sendHttpsCoon(String url, String code, String userId) {
        if (url.isEmpty()) {
            return null;
        }
        try {
            //设置SSLContext
            SSLContext ssl = SSLContext.getInstance("SSL");
            ssl.init(null, new TrustManager[]{myX509TrustManager}, null);

            //打开连接
            HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
            //设置套接工厂
            conn.setSSLSocketFactory(ssl.getSocketFactory());
            //加入数据
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-type", "application/json");


            JSONObject obj = new JSONObject();
            obj.put("receipt-data", code);

            BufferedOutputStream buffOutStr = new BufferedOutputStream(conn.getOutputStream());
            buffOutStr.write(obj.toString().getBytes());
            buffOutStr.flush();
            buffOutStr.close();
            //获取输入流
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            System.out.println(sb);
            // 错误的 sb对象是：{"status":21002},苹果官网写的错误也都是2XXXX 具体含义可查：https://developer.apple.com/documentation/appstorereceipts/status
            // 所以 通过长度等于16，我们就可确定苹果支付成功是否有效
            if (sb.length() == 16) {
                return "支付失败,苹果说status异常";
            }
            // 将一个Json对象转成一个HashMap
            JSONObject alljsoncode = JSON.parseObject(sb.toString());
            Object receipt = alljsoncode.get("receipt");
            HashMap hashMap = JSONUtil.toBean(receipt.toString(), HashMap.class);
            // 苹果给的订单号
            String original_transaction_id = (String) hashMap.get("original_transaction_id");
            //TODO 存储订单ID，并检查此订单ID是否存在，如果存在就证明已经发货了(避免二次发货)
            if ("com.hefeixunliao.zhenliao12yuan".equals(hashMap.get("product_id"))) {
                //TODO 执行12元的钻石增加
                logger.info("用户ID：{},执行12元钻石追加",userId);
            } else if ("com.hefeixunliao.zhenliao30yuan".equals(hashMap.get("product_id"))) {
                //TODO 执行30元的钻石增加
                logger.info("用户ID：{},执行30元钻石追加",userId);
            } else {
                logger.info("用户ID：{},向苹果发起验证支付请求没有次product_id",userId);
                return "支付失败,当前没有次product_id";
            }
            return "支付成功";
        } catch (Exception e) {
            logger.error("向苹果发起验证支付请求是否有效出现异常：{}", e.getMessage());
            return "支付过程中,出现了异常!";
        }
    }
}
