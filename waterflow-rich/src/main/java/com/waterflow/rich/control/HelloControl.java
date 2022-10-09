package com.waterflow.rich.control;

import com.waterflow.rich.dao.TestBeanRepository;
import com.waterflow.rich.service.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/control")
public class HelloControl {

    Logger logger = LoggerFactory.getLogger(HelloControl.class);

    @Resource
    HelloService helloService;

    @Resource
    TestBeanRepository testBeanRepository;

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        String dist = helloService.hello(name);
        logger.info("hello control, name is " + name);
        logger.info("test bean data is {}", testBeanRepository.findById(1L).get().getTitle());
        return dist;
    }

}
