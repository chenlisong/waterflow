package com.waterflow.rich.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waterflow.common.util.HttpUtil;
import com.waterflow.rich.dao.TestBeanRepository;
import com.waterflow.rich.grab.FundGrab;
import com.waterflow.rich.grab.QuoteGrab;
import com.waterflow.rich.init.Application;
import com.waterflow.rich.strategy.Retreat;
import com.waterflow.rich.strategy.RichBean;
import org.apache.commons.io.FileUtils;
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

import java.util.Date;
import java.util.List;

@SpringBootTest(classes= Application.class)
@RunWith(SpringRunner.class)
@ActiveProfiles("online")
public class RetreatTest {

	private Logger logger = LoggerFactory.getLogger(RetreatTest.class);

	@Autowired
	Retreat retreat;

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
//		retreat.upload("001018");
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
		String filePath = Retreat.filePath("000000");

		HttpUtil.download(urlPath, filePath);
		retreat.upload("000000");
	}

}

