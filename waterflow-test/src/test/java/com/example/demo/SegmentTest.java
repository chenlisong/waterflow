package com.example.demo;

import com.waterflow.test.segment.CheModel;
import com.waterflow.test.segment.Solution;
import com.waterflow.test.util.HttpUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class SegmentTest {

    com.waterflow.test.segment.Solution segmentSolution = new com.waterflow.test.segment.Solution();

    Logger logger = LoggerFactory.getLogger(SegmentTest.class);

    @Test
    public void searchTreeTest() throws Exception{

        String url = "https://chenlisong.oss-cn-beijing.aliyuncs.com/file/che-model-withid.csv";
        String write2File = "/opt/data/che-model-withid.csv";

        HttpUtil.download(url, write2File);

        List<CheModel> cheModels = segmentSolution.loadFile(write2File);
//        List<CheModel> cheModels = new ArrayList<>();
        logger.info("build index data suc. size is {}", cheModels.size());

        segmentSolution.buildIndex(cheModels);
        CheModel queryModel = new CheModel("奥迪", "A4L", "2011款技术版本");
        CheModel dist = segmentSolution.query(queryModel);

        logger.info("dist model is {}, query is {} ", dist.simpleString(), queryModel.simpleString());

    }
}
