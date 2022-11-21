package com.waterflow.rich.test;

import com.alibaba.fastjson.JSONObject;
import com.waterflow.common.util.HttpUtil;
import com.waterflow.rich.init.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes= Application.class)
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class MsgTest {

	private Logger logger = LoggerFactory.getLogger(MsgTest.class);

	@Test
	public void testSendMsg() throws Exception {
		String url = "https://wxpusher.zjiecode.com/api/send/message";

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("appToken", "AT_kRFYVdXkwUQagqbrvQH4goRqF29fnxAt");
		jsonObject.put("content", "市场情况每日分析沪深300，1ystd：-10%，6mstd:-20%, 回撤：24/64沪深300，1ystd：-10%，6mstd:-20%, 回撤：24/64沪深300，1ystd：-10%，6mstd:-20%, 回撤：24/64沪深300，1ystd：-10%，6mstd:-20%, 回撤：24/64沪深300，1ystd：-10%，6mstd:-20%, 回撤：24/64沪深300，1ystd：-10%，6mstd:-20%, 回撤：24/64沪深300，1ystd：-10%，6mstd:-20%, 回撤：24/64沪深300，1ystd：-10%，6mstd:-20%, 回撤：24/64沪深300，1ystd：-10%，6mstd:-20%, 回撤：24/64沪深300，1ystd：-10%，6mstd:-20%, 回撤：24/64沪深300，1ystd：-10%，6mstd:-20%, 回撤：24/64沪深300，1ystd：-10%，6mstd:-20%, 回撤：24/64沪深300，1ystd：-10%，6mstd:-20%, 回撤：24/64沪深300，1ystd：-10%，6mstd:-20%, 回撤：24/64沪深300，1ystd：-10%，6mstd:-20%, 回撤：24/64沪深300，1ystd：-10%，6mstd:-20%, 回撤：24/64");
		jsonObject.put("summary", "沪深300，1ystd：-10%，6mstd:-20%, 回撤：24/64");
		jsonObject.put("contentType", 2);
		jsonObject.put("topicIds", new Integer[]{8150});
		jsonObject.put("url", "http://www.baidu.com");
		jsonObject.put("verifyPay", false);

		String resp = HttpUtil.postWithJson(url, null, jsonObject.toJSONString());
		logger.info("resp is {}", resp);
	}
}

