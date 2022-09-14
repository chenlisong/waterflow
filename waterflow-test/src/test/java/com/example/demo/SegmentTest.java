package com.example.demo;

import com.waterflow.test.segment.CheModel;
import com.waterflow.test.segment.Solution;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class SegmentTest {

    com.waterflow.test.segment.Solution segmentSolution = new com.waterflow.test.segment.Solution();

    Logger logger = LoggerFactory.getLogger(SegmentTest.class);

    @Test
    public void searchTreeTest() throws Exception{
        List<CheModel> cheModels = segmentSolution.loadFile("/Users/chenlisong/Desktop/che-model-withid.csv");
        logger.info("build index data suc. size is {}", cheModels.size());

        segmentSolution.buildIndex(cheModels);
        CheModel queryModel = new CheModel("奥迪", "A4L", "2011款技术版本");
        CheModel dist = segmentSolution.query(queryModel);

        logger.info("dist model is {}, query is {} ", dist.simpleString(), queryModel.simpleString());


    }
}
