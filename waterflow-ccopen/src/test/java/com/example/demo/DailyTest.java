package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.waterflow.ccopen.init.Application;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.List;

@SpringBootTest(classes= Application.class)
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class DailyTest {

	private Logger logger = LoggerFactory.getLogger(DailyTest.class);

	@Test
	public void diffPics()throws Exception{
		String filePath = "/Users/chenlisong/Downloads/pic-all2.csv";
		String toFilePath = "/Users/chenlisong/Downloads/pic-all-nosame.csv";

		new File(toFilePath).delete();

		List<String> lines = FileUtils.readLines(new File(filePath), "utf-8");

		int sameCnt = 0;
		int noSameCnt = 0;

		for(int i=1; i<lines.size(); i++) {
			String line = lines.get(i);
			String picall = line.split(",10241024,")[1];
			JSONObject jsonObject = JSON.parseObject(picall);
			String licenseOccludePic = jsonObject.getString("licenseOccludePic");
			String pic = jsonObject.getString("pic");
			if (pic.equals(licenseOccludePic)) {
				sameCnt ++;
			}else {
				noSameCnt ++;

				String content = line+"\r\n";
				FileUtils.writeByteArrayToFile(new File(toFilePath), content.getBytes(), true);
			}
		}

		logger.info("same is {}, no same is {}", sameCnt, noSameCnt);

	}

}

