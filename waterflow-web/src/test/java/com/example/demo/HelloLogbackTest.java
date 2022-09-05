package com.example.demo;

import com.waterflow.service.HelloService;
import com.waterflow.web.init.Application;
import org.junit.Test;
import org.junit.internal.Classes;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes= Application.class)
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class HelloLogbackTest {

	private Logger logger = LoggerFactory.getLogger(HelloLogbackTest.class);

	@Test
	public void helloTest(){
		String words = "xx";
		System.out.println("words out print is: " + words);
		logger.info("words log print is: " + words);
	}

}

