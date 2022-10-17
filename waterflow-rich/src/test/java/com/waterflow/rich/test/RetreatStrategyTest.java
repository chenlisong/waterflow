package com.waterflow.rich.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waterflow.common.util.HttpUtil;
import com.waterflow.rich.grab.FundGrab;
import com.waterflow.rich.init.Application;
import com.waterflow.rich.strategy.RetreatStrategy;
import com.waterflow.rich.strategy.RichBean;
import com.waterflow.rich.strategy.RichLoopBean;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes= Application.class)
@RunWith(SpringRunner.class)
@ActiveProfiles("online")
public class RetreatStrategyTest {

	private Logger logger = LoggerFactory.getLogger(RetreatStrategyTest.class);

	@Autowired
	RetreatStrategy retreat;

	@Autowired
	FundGrab fundGrab;

	@Test
	public void retreatStrategyTest() throws Exception{

		String endpoint = "oss-cn-beijing.aliyuncs.com";
		String accessKeyId = "";
		String accessKeySecret = "";
		String bucketName = "chenlisong";

		retreat.aliyunConfig(accessKeyId, accessKeySecret, endpoint, bucketName);

		List<RichBean> richBeans = fundGrab.convertFile2Bean("001018");
		retreat.initRichBeans(richBeans);
		retreat.handleBaseData();
		retreat.dealStrategy();

		retreat.profit();
		retreat.output2File("001018");
		retreat.upload("001018");
	}

	@Test
	public void loopTest() throws Exception{

		List<RichLoopBean> richLoopBeans = new ArrayList<>();

		for(int i=10; i<180; i+=5) {
			for(int y=10; y< 180; y+=5) {
				List<RichBean> richBeans = fundGrab.convertFile2Bean("001018");
				retreat.initRichBeans(richBeans);
				retreat.setConfig(i,y);
				retreat.handleBaseData();
				retreat.dealStrategy();

				retreat.profit();

				RichLoopBean richLoopBean = new RichLoopBean();
				richLoopBean.setDiffBuyTime(i);
				richLoopBean.setDiffSellTime(y);
				richLoopBean.convert2Loop(richBeans.get(richBeans.size()-1));
				richLoopBeans.add(richLoopBean);

				logger.info("loop run i is {}, y is {}, market value is {}", i, y, richBeans.get(richBeans.size()-1).getMarketValue());
			}
		}

		richLoopBeans = richLoopBeans.stream()
					.sorted(Comparator.comparing(RichLoopBean::getMarketValue).reversed())
						.limit(10)
							.collect(Collectors.toList());

		logger.info("rich loop bean data is {}", JSON.toJSONString(richLoopBeans));
	}

	@Test
	public void uploadTest() {
		retreat.upload("001018");
	}

	@Test
	public void jsonTest() throws Exception{
		RichBean richBean = new RichBean(new Date(), 1.0);
		richBean.setMarketValue(2.0);
		logger.info("rich json by aliyun is {} ", JSON.toJSONString(richBean, SerializerFeature.WriteNonStringValueAsString));

		ObjectMapper mapper = new ObjectMapper();
		logger.info("rich json by jackson is {}", mapper.writeValueAsString(richBean));
	}

	@Test
	public void dateTest() {
		Date now = new Date();
		for(int i=1; i<30; i++) {
			Date newDate = DateUtils.addDays(now, i);
			logger.info("date format is {}", DateFormatUtils.format(newDate, "M/d/yyyy"));
		}
	}

	@Test
	public void downloadTest() {
		String urlPath = "https://gw.alipayobjects.com/os/bmw-prod/c335e0c4-caa5-4c76-a321-20df96b6e5c8.json";
		String filePath = RetreatStrategy.filePath("000000");

		HttpUtil.download(urlPath, filePath);
		retreat.upload("000000");
	}

	@Test
	public void doubleTest() {
		double a = 1.2323232323d;
		double b = NumberUtils.toScaledBigDecimal(a, NumberUtils.INTEGER_ONE, RoundingMode.HALF_UP).doubleValue();

		logger.info("a is {}, b is {}", a, b);

	}

}

