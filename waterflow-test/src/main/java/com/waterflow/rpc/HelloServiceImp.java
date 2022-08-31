package com.waterflow.rpc;

import com.waterflow.service.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloServiceImp implements HelloService {

    Logger logger = LoggerFactory.getLogger(HelloServiceImp.class);

    @Override
    public String hello(String name) {
        logger.info("hello service impl output, name is " + name);
        return "hello world. " + name;
    }
}