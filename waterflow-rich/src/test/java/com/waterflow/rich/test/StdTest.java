package com.waterflow.rich.test;

import com.alibaba.fastjson.JSON;
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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.net.URL;

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
	public void hund() throws Exception {

		double money = 12;

		int year = 0;
		while(money < 100) {
			money = money * 1.1 + 12;
			year++;
		}

		logger.info("year is {}", year);

	}

	@Test
	public void convertHtml2Png2() throws Exception {

		// 控制浏览器打开网页，仅适用于JdK1.6及以上版本
		Desktop.getDesktop().browse(new URL("/Users/chenlisong/Documents/code/files/Archive/Line-20221025的副本.html").toURI());
		Robot robot = new Robot();
		// 延迟一秒
		robot.delay(1000);
		// 获取屏幕宽度和高度
		Dimension d = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		int width = (int) d.getWidth();
		int height = (int) d.getHeight();
		// 最大化浏览器
		robot.keyRelease(KeyEvent.VK_F11);
		robot.delay(3000);
		// 对屏幕进行截图
		Image image = robot.createScreenCapture(new Rectangle(0, 0, width, height));
		// 通过图形绘制工具类将截图保存
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = img.createGraphics();
		g.drawImage(image, 0, 0, width, height, null);
		// 保存图片
		ImageIO.write(img, "jpg", new File("a1.jpg"));
		System.out.println("done!");

	}

	@Test
	public void convertHtml2Png() throws Exception {

//		FtlService.renderHtml2Image("file:///Users/chenlisong/Documents/code/app.2022-10-17.html");
//		FtlService.renderHtml2Image("file:///Users/chenlisong/Documents/code/files/Archive/Line-20221025的副本.html");
//		FtlService.renderHtml2Image("http://localhost:2224/a2");

//		FtlService.renderHtml2Image("https://chenlisong.oss-cn-beijing.aliyuncs.com/rich/Line-20221025%E7%9A%84%E5%89%AF%E6%9C%AC.html");
//		FtlService.transferHtml2Image("/Users/chenlisong/Documents/code/app.2022-10-17.html", "result.png", 600, 700);
//		FtlService.transferHtml2Image("/Users/chenlisong/Documents/code/files/Archive/Line-20221025的副本.html", "result.png", 600, 700);
	}

	@Test
	public void stdExec() throws Exception{
//		String[] fundCodes = new String[] {"/**/001018", "161005"};

		String[] fundCodes = new String[] {"510310"};
		for(String fundCode: fundCodes) {
			for(int month=1; month<13;month++) {
				long diffTime = 1000L * 60 * 60 * 24 * 30 * month;

				month++;

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

				String objectName = "rich/std_"+month + "_" + fundCode+".json";
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

