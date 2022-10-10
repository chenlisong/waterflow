package com.waterflow.rich.test;

import com.waterflow.rich.dao.TestBeanRepository;
import com.waterflow.rich.grab.FundGrab;
import com.waterflow.rich.grab.QuoteGrab;
import com.waterflow.rich.init.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes= Application.class)
@RunWith(SpringRunner.class)
@ActiveProfiles("online")
public class GrabTest {

	private Logger logger = LoggerFactory.getLogger(GrabTest.class);

	@Autowired
	TestBeanRepository testBeanService;

	@Autowired
	FundGrab fundGrab;

	@Autowired
	QuoteGrab quoteGrab;

	@Test
	public void fundDownloadTest(){
		fundGrab.downloadFundFile("001018");
	}

	@Test
	public void fundLoadTest(){

		try {
			fundGrab.convertFile2Bean("001018");
		}catch (Exception e) {
			logger.error("error.", e);
		}
	}

	@Test
	public void quoteGrabTest(){

		quoteGrab.downloadQuoteFile("0600150", "20010101", "20230101");
	}

}

