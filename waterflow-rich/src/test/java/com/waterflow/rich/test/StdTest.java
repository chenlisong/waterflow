package com.waterflow.rich.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.waterflow.common.util.UploadUtil;
import com.waterflow.rich.grab.FundGrab;
import com.waterflow.rich.init.Application;
import com.waterflow.rich.strategy.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes= Application.class)
@RunWith(SpringRunner.class)
@ActiveProfiles("online")
public class StdTest {

	private Logger logger = LoggerFactory.getLogger(StdTest.class);

	@Autowired
	RetreatStrategy retreat;

	@Autowired
	FundGrab fundGrab;

	@Autowired
	StdStrategy stdStrategy;

	String endpoint = "oss-cn-beijing.aliyuncs.com";
	String accessKeyId = "";
	String accessKeySecret = "";
	String bucketName = "chenlisong";

	@Test
	public void stdExec() throws Exception{
//		String[] fundCodes = new String[] {"001018", "161005"};

		String[] fundCodes = new String[] {"519300"};
		for(String fundCode: fundCodes) {
			for(int year=1; year<5;year++) {
				long diffTime = 1000L * 60 * 60 * 24 * 365 * year;

				UploadUtil uploadUtil = UploadUtil.instance();
				uploadUtil.aliyunConfig(accessKeyId,accessKeySecret, endpoint,bucketName);

				List<RichBean> richBeans = fundGrab.autoGrabFundData(fundCode);
				retreat.initRichBeans(richBeans);
				retreat.handleBaseData();

				List<StdRichBean> stdRichBeans = stdStrategy.convert2Std(richBeans, diffTime);

				ValueFilter doubleFilter = new ValueFilter() {
					@Override
					public Object process(Object o, String s, Object value){
						try{
							if(value instanceof BigDecimal || value instanceof Double) {
								return NumberUtils.toScaledBigDecimal((double)value, Integer.valueOf(4), RoundingMode.HALF_UP).doubleValue();
//					return new BigDecimal(value.toString());
							}
						}catch (Exception e) {
//					e.printStackTrace();
						}
						return value;
					}
				};

				String objectName = "rich/std_"+year + "_" + fundCode+".json";
				String filePath = "/Users/chenlisong/Downloads/" + objectName;
				FileUtils.writeByteArrayToFile(new File(filePath), JSON.toJSONBytes(stdRichBeans, doubleFilter));

				logger.info("rich loop bean data is {}", JSON.toJSONString(stdRichBeans.stream().limit(10).collect(Collectors.toList())));
				uploadUtil.upload(filePath, objectName);
			}
		}
	}

	/**
	 * 标准差计算
	 * @throws Exception
	 */
	@Test
	public void sqrtTest() throws Exception{

		logger.info("-1 pow is {}", Math.pow(-1.0, 2));

		int[] array = new int[] {73, 72, 71, 69, 68, 67};
//		int[] array2 = new int[] {95, 85, 75, 65, 55, 45};

		int sum = 0;
		int cnt = 0;
		for(int unit: array) {
			sum += unit;
			cnt += 1;
		}
		int average = sum/cnt;

		sum = 0;
		for(int unit: array) {
			sum += Math.pow((unit-average)*1.0, 2);
		}

		double std = Math.sqrt(sum/(cnt-1));
		double stdFormat = NumberUtils.toScaledBigDecimal(std, NumberUtils.INTEGER_ONE, RoundingMode.HALF_UP).doubleValue();

		logger.info("sqrt test array average is {}, sum is{}, std is {}", average, sum, stdFormat);


	}

}

