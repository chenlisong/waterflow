package com.waterflow.rich.test;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.waterflow.common.util.HttpUtil;
import com.waterflow.rich.grab.FundGrab;
import com.waterflow.rich.init.Application;
import com.waterflow.rich.strategy.RetreatStrategy;
import com.waterflow.rich.strategy.RichBean;
import com.waterflow.rich.strategy.StdRichBean;
import com.waterflow.rich.strategy.StdStrategy;
import com.waterflow.rich.util.MetaAntvUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest(classes= Application.class)
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class MsgTest {

	private Logger logger = LoggerFactory.getLogger(MsgTest.class);

	@Autowired
	Configuration config;

	@Value("${project.file.path}")
	String projectFilePath;

	@Autowired
	FundGrab fundGrab;

	@Autowired
	RetreatStrategy retreat;

	@Autowired
	StdStrategy stdStrategy;

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

	@Test
	public void testRelocationMsg() throws Exception {
		String url = "https://wxpusher.zjiecode.com/api/send/message";

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("appToken", "AT_kRFYVdXkwUQagqbrvQH4goRqF29fnxAt");
		jsonObject.put("content", "<meta http-equiv=\"refresh\" content=\"0,url = http://47.109.105.18/fund/std?code=110020&month=3\"></meta>");
		jsonObject.put("summary", "test auto href location");
		jsonObject.put("contentType", 2);
		jsonObject.put("topicIds", new Integer[]{8150});
		jsonObject.put("url", "http://www.baidu.com");
		jsonObject.put("verifyPay", false);

		String resp = HttpUtil.postWithJson(url, null, jsonObject.toJSONString());
		logger.info("resp is {}", resp);
	}

	@Test
	public void testHtmlMsg() throws Exception {

		String fundCode = "110020";
		int month = 3;

		List<RichBean> richBeans = null;
		Map<String, Object> model = new HashMap();
		try{
			richBeans = fundGrab.autoGrabFundData(fundCode);

			retreat.initRichBeans(richBeans);
			retreat.handleBaseData();

			long diffTime = 1000L * 60 * 60 * 24 * 30 * month;

//			Date begin = DateUtils.addYears(new Date(), -5);
			Date begin = DateUtils.addDays(new Date(), -10);
			List<StdRichBean> stdRichBeans = stdStrategy.convert2Std(richBeans, diffTime);
			stdRichBeans = stdRichBeans.stream()
					.filter(bean -> bean.getTime().getTime() > begin.getTime())
					.collect(Collectors.toList());

			List<JSONObject> jsonObjects = MetaAntvUtil.convert2Antv(stdRichBeans, "date", "price", "p1sd", "p1fsd", "p2sd", "p2fsd");
			List<JSONObject> list = new ArrayList<>();

			JSONObject bugfix = new JSONObject();
			bugfix.put("x", "1-1");
			bugfix.put("y", 0);
			bugfix.put("series", "t1");
			list.add(bugfix);

			list.addAll(jsonObjects);
			model.put("data", JSON.toJSONString(list));

		}catch (Exception e) {
			logger.error("error.", e);
		}

		Template template = config.getTemplate("antv-line.ftl");
		StringWriter stringWriter = new StringWriter();
		template.process(model, stringWriter);
		String content = stringWriter.toString();

		String url = "https://wxpusher.zjiecode.com/api/send/message";

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("appToken", "AT_kRFYVdXkwUQagqbrvQH4goRqF29fnxAt");
		jsonObject.put("content", content);
		jsonObject.put("summary", "test auto href location");
		jsonObject.put("contentType", 2);
		jsonObject.put("topicIds", new Integer[]{8150});
		jsonObject.put("url", "http://www.baidu.com");
		jsonObject.put("verifyPay", false);

		String resp = HttpUtil.postWithJson(url, null, jsonObject.toJSONString());
		logger.info("resp is {}", resp);
	}
}

