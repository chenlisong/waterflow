package com.waterflow.rich.test;

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
@ActiveProfiles("online")
public class JpaTest {

	private Logger logger = LoggerFactory.getLogger(JpaTest.class);


	@Test
	public void helloTest() {
		String words = "xx";
		logger.info("words log print is: " + words);
	}

}

