package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.waterflow.ccopen.bean.AdLog;
import com.waterflow.ccopen.bean.Catalog;
import com.waterflow.ccopen.bean.Novel;
import com.waterflow.ccopen.dao.AdLogDao;
import com.waterflow.ccopen.dao.CatalogDao;
import com.waterflow.ccopen.dao.NovelDao;
import com.waterflow.ccopen.init.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest(classes= Application.class)
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class DBTest {

    private Logger logger = LoggerFactory.getLogger(DBTest.class);

	@Autowired
    AdLogDao adLogDao;

    @Value("${update.param}")
    String param;

    @Value("${update.param2}")
    String param2;

    @Autowired
    NovelDao novelDao;
    
    @Autowired
    CatalogDao catalogDao;

    @Test
    public void outputParamTest() {
        logger.info("set param {}, the value is {}", "world".equals(param), param);
        logger.info("set param2 {}, the value is {}", "world".equals(param2), param2);
    }

    @Test
    public void insertDBTest(){
//        AdLog adLog = new AdLog();
//        adLog.setAdTime(new Date());
//        adLog.setUserId("10000000001");
//        adLog.setPlatform(1);
//        adLog.setReadStatus(1);
//        adLog = adLogDao.save(adLog);
//
//        logger.info("insert db suc, and id is {}", adLog.getId());
//
//        Novel novel = new Novel();
//        novel.setName("123");
//        novel.setContent("123");
//        novel.setCreateTime(new Date());
//        novel.setAuthor("author");
//        novelDao.save(novel);
//        logger.info("id : {}", novel.getId());

        List<Catalog> list = new ArrayList<>();
        Catalog catalog = new Catalog();
        catalog.setNovelId(1L);
        catalog.setName("123");
        
        Catalog catalog2 = new Catalog();
        catalog2.setNovelId(1L);
        catalog2.setName("123");
        list.add(catalog); list.add(catalog2);
        catalogDao.saveAll(list);
    }

    @Test
    public void findDBTest(){
        List<AdLog> adLogList = adLogDao.findTop10("10000000001");
        logger.info("ad logs list is {}", JSON.toJSONString(adLogList));
    }

}