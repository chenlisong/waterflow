package com.waterflow.ccopen.init;

import com.waterflow.ccopen.service.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(
//        exclude = {DataSourceAutoConfiguration.class},
        scanBasePackages={"com.waterflow.ccopen"})
@EnableJpaRepositories(basePackages = {"com.waterflow.ccopen.dao"})
@EntityScan(basePackages = "com.waterflow.ccopen.bean")
@RestController
public class Application {
    
    Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    HelloService helloService;

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        String dist = helloService.hello(name);
        logger.info(dist);
        return dist;
    }

}
