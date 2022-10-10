package com.waterflow.rich.test;

import com.waterflow.rich.dao.TestBeanRepository;
import com.waterflow.rich.grab.FundGrab;
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
public class JpaTest {

	private Logger logger = LoggerFactory.getLogger(JpaTest.class);

	@Autowired
	TestBeanRepository testBeanService;

	@Autowired
	FundGrab fundGrab;

	@Test
	public void helloTest() {
		String words = "xx";
		logger.info("words log print is: " + words);
		logger.info("redis key cls value is {}", testBeanService.findById(1L));
	}

	@Test
	public void fundGrabTest(){
		logger.info("redis key cls value is {}", testBeanService.findById(1L));

		fundGrab.downloadFundFile("001018");
//		fundGrab.downloadFundFile("00000");

	}

}

