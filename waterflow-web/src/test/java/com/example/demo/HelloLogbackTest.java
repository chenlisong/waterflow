package com.example.demo;

import com.waterflow.service.HelloService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HelloLogbackTest {

	private Logger logger = LoggerFactory.getLogger(HelloLogbackTest.class);

	@Test
	public void helloTest(){

		String words = "xx";

		System.out.println("words out print is: " + words);
		logger.info("words log print is: " + words);
	}

}

