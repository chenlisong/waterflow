package com.example.demo;

import com.waterflow.common.util.SecUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecTest {

	Logger logger = LoggerFactory.getLogger(SecTest.class);

	@Test
	public void secTest() {

		long[] data = new long[]{0, 1, 19,100009,765432189};

		for(long unit : data) {
			String sec = SecUtil.sec(unit);
			long dec = SecUtil.dec(sec);

			logger.info("true unit is {}, sec : {}, dec : {}", unit, sec, dec);
		}
	}
}

