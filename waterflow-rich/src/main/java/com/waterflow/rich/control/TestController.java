package com.waterflow.rich.control;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController {

    Logger logger = LoggerFactory.getLogger(TestController.class);

    @RequestMapping("/a1")
    public String test(){
        logger.info("a1 123");
        return "index";
    }

    @RequestMapping("/a2")
    public String test2(){
        logger.info("a2 123");
        return "canvas";
    }
}