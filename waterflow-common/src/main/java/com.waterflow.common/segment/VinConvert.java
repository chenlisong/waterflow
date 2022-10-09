package com.waterflow.common.segment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.waterflow.common.util.HttpUtil;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class VinConvert {

    int searchCnt = 1;

    public String search4Free(String vin) {
        String url = "http://118.31.113.49/api/vin/v2/index?key=d7ba9fa7634764f2fd5bb81e8183ce18&vin="
                + vin;

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        headers.put("Accept-Encoding", "gzip, deflate");
        headers.put("Accept-Language", "zh-CN,zh;q=0.9");
        headers.put("Cache-Control", "max-age=0");
        headers.put("Connection", "keep-alive");
        headers.put("Host", "118.31.113.49");
        headers.put("Upgrade-Insecure-Requests", "1");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36" + searchCnt++);

        String resp = HttpUtil.get(url, headers);

        String result = "";

        if(JSON.isValidObject(resp)) {
            JSONObject jsonObject = JSON.parseObject(resp);
            if(jsonObject.containsKey("code") && jsonObject.containsKey("data")
                && jsonObject.getInteger("code") == 1) {
                JSONObject data = jsonObject.getJSONObject("data");
                if(data != null) {
                    String brandName = data.getString("brand_name");
                    String seriesName = data.getString("series_name");
                    String modelName = data.getString("name");

                    if(!StringUtils.isEmpty(brandName) && !StringUtils.isEmpty(seriesName)
                            && !StringUtils.isEmpty(modelName)) {
                        result = brandName + seriesName + modelName;
                    }
                }
            }
        }
        return result;
    }
}
