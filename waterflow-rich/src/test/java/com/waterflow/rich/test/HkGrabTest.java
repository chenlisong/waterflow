package com.waterflow.rich.test;

import com.waterflow.common.util.HttpUtil;
import com.waterflow.rich.grab.FundGrab;
import com.waterflow.rich.grab.HkGrab;
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
public class HkGrabTest {

	private Logger logger = LoggerFactory.getLogger(HkGrabTest.class);

	@Autowired
	HkGrab hkGrab;

	@Test
	public void hkDownloadTest(){
		hkGrab.downloadQuoteFile("00520");
	}

	@Test
	public void findTest() throws Exception{

		hkGrab.findAll("00520");

	}

}

