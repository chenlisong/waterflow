package com.waterflow.rich.util;

import com.alibaba.fastjson.JSONObject;
import com.waterflow.common.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MsgUtil {

    static Logger logger = LoggerFactory.getLogger(MsgUtil.class);

    public static String sendWxNotice(String summary, String content) {
        String url = "https://wxpusher.zjiecode.com/api/send/message";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appToken", "AT_kRFYVdXkwUQagqbrvQH4goRqF29fnxAt");
//        jsonObject.put("summary", "沪深300，1ystd：-10%，6mstd:-20%, 回撤：24/64");
        jsonObject.put("summary", summary);
        jsonObject.put("content", content);
        jsonObject.put("contentType", 2);
        jsonObject.put("topicIds", new Integer[]{8150});
        jsonObject.put("url", "http://www.baidu.com");
        jsonObject.put("verifyPay", false);

        String resp = null;
        try{
            HttpUtil.postWithJson(url, null, jsonObject.toJSONString());
        }catch (Exception e) {
            logger.error("error", e);
        }

        logger.info("send wx notice msg. url is {}, json is {}, resp is {}", url, jsonObject.toJSONString(), resp);

        return resp;
    }


}
