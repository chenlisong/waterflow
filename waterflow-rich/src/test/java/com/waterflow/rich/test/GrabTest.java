package com.waterflow.rich.test;

import com.waterflow.common.util.HttpUtil;
import com.waterflow.rich.grab.FundGrab;
import com.waterflow.rich.grab.QuoteGrab;
import com.waterflow.rich.init.Application;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

@SpringBootTest(classes= Application.class)
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class GrabTest {

	private Logger logger = LoggerFactory.getLogger(GrabTest.class);

//	@Autowired
//	TestBeanRepository testBeanService;

	@Autowired
	FundGrab fundGrab;

	@Autowired
	QuoteGrab quoteGrab;

	@Test
	public void fundDownloadTest(){
//		fundGrab.downloadFundFile("161005");
//		fundGrab.downloadFundFile("002943");
		fundGrab.downloadFundFile("519300");
	}

	@Test
	public void fundLoadTest(){

		try {
			fundGrab.convertFile2Bean("519300");
		}catch (Exception e) {
			logger.error("error.", e);
		}
	}

	@Test
	public void quoteGrabTest(){

		quoteGrab.downloadQuoteFile("0600150", "20010101", "20230101");
	}

	@Test
	public void grabBaiduPicTest() throws Exception{

		String htmlPath = "/Users/chenlisong/Desktop/3.html";
		String filePath = "/Users/chenlisong/Desktop/pics/";

		Document document = Jsoup.parse(new File(htmlPath));
		Elements elements = document.select("img[class=main_img img-hover]");

		int index = 1;
		for(Element element: elements) {
			String url = element.attr("data-imgurl");
			HttpUtil.download(url, filePath + index + ".jpg");
			index++;
		}
	}

	@Test
	public void autoGrabFundDataTest() throws Exception{
		fundGrab.autoGrabFundData("001018");
	}

	@Test
	public void fundName() throws Exception{
		String fundCode = "001018";

		String fundName = fundGrab.fundName(fundCode);
		logger.info("fund code is {}, name is {}", fundCode, fundName);
	}

}

