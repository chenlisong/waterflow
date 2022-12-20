package com.example.demo;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class EmpResourceTest {

    Logger logger = LoggerFactory.getLogger(EmpResourceTest.class);


    @Test
    public void ocrTest() throws Exception{

        String filepath = "/Users/chenlisong/Downloads/111.csv";
        File file = new File(filepath);
        List<String> list = FileUtils.readLines(file, "UTF-8");
        logger.info("first line is {}", list.get(1));

        if(list == null || list.size() <= 1) {
            logger.info("file list size not enough, size is {}", list == null ? 0 : list.size());
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss");

        for(int i=1; i<list.size(); i++) {
            String line = list.get(i);
            String[] array = line.split(",");
            long id = Long.parseLong(array[0]);
            long empId = Long.parseLong(array[1]);
            long resourceId = Long.parseLong(array[2]);
            int status = Integer.parseInt(array[3]);
            Date addTime = sdf.parse(array[4]);
            long addOperator = Long.parseLong(array[5]);
            int type = Integer.parseInt(array[6]);
            Date updateTime = null;
            if(array.length > 7 && array[7] != null && !"".equals(array[7])) {
                updateTime = sdf.parse(array[7]);
            }

            if(i < 100) {
                logger.info("id: {}, empId: {}, resourceId: {}, status: {},addTime:{}, addOperator: {}, type: {}, updateTime: {} "
                        , id, empId, resourceId, status, addTime, addOperator, type, updateTime);
            }
        }

        logger.info("hello world");
    }
}
