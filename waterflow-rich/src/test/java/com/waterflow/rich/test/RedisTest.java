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
@ActiveProfiles("dev")
public class RedisTest {

	private Logger logger = LoggerFactory.getLogger(RedisTest.class);

//	@Autowired
//	RedisTemplate redisTemplate;

	@Test
	public void helloTest(){
		String words = "xx";
		logger.info("words log print is: " + words);
//		logger.info("redis key cls value is {}", redisTemplate.opsForValue().get("cls"));
	}

}

